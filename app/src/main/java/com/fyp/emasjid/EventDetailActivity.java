package com.fyp.emasjid;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.CalendarContract;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import at.blogc.android.views.ExpandableTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nineoldandroids.view.ViewHelper;
import com.squareup.picasso.Picasso;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class EventDetailActivity extends AppCompatActivity implements ObservableScrollViewCallbacks {

    private View mImageView;
    private View mToolbarView;
    private ObservableScrollView mScrollView;
    private int mParallaxImageHeight;
    private String eventId,eventTitle, eventLocation, eventdate;
    private ImageView ed_image;
    private TextView ed_title, ed_location, ed_date, ed_description, ed_gender, ed_age, ed_organizer, ed_contact, ed_participantLimit;
    private DatabaseReference eventRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setTitle("Event Details");

        eventRef = FirebaseDatabase.getInstance().getReference().child("Event");
        eventId = getIntent().getExtras().get("eventId").toString();

        //observable scroll
        mImageView = findViewById(R.id.ed_image);
        mToolbarView = findViewById(R.id.toolbar);
        mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, getResources().getColor(R.color.colorPrimary)));
        mScrollView = (ObservableScrollView) findViewById(R.id.scroll);
        mScrollView.setScrollViewCallbacks(this);
        mParallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.parallax_image_height);

        //expandable text view
        final ExpandableTextView expandableTextView = (ExpandableTextView) this.findViewById(R.id.expandableTextView);
        final Button buttonToggle = (Button) this.findViewById(R.id.button_toggle);
        expandableTextView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                boolean isExpandable = true;

                float scale = getResources().getDisplayMetrics().density;
                int sizeInDp = 16;
                int dpAsPixels = (int) (sizeInDp*scale + 0.5f);
                if(isExpandable){
                    isExpandable = false;
                    {
                        if(expandableTextView.getLineCount()< 4){
                            buttonToggle.setVisibility(View.GONE);
                            expandableTextView.setPadding(0,0,0,dpAsPixels);
                        }
                    }
                }
            }
        });

        buttonToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                if (expandableTextView.isExpanded()) {
                    expandableTextView.collapse();
                    buttonToggle.setText(R.string.expand);
                } else {
                    expandableTextView.expand();
                    buttonToggle.setText(R.string.collapse);
                }
            }
        });

        ed_image = (ImageView) findViewById(R.id.ed_image);
        ed_title = (TextView) findViewById(R.id.ed_title);
        ed_location = (TextView) findViewById(R.id.ed_location);
        ed_date = (TextView) findViewById(R.id.ed_date);
        ed_description = (TextView) findViewById(R.id.expandableTextView);
        ed_age = (TextView) findViewById(R.id.ed_agecontent);
        ed_contact = (TextView) findViewById(R.id.ed_contactcontent);
        ed_gender = (TextView) findViewById(R.id.ed_gendercontent);
        ed_organizer = (TextView) findViewById(R.id.ed_organizercontent);
        ed_participantLimit = (TextView) findViewById(R.id.ed_participantcontent);

        retrieveEventDetails();

        final Button registerButton = (Button) this.findViewById(R.id.button_register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(EventDetailActivity.this, RegisterActivity.class);
                registerIntent.putExtra("eventId",eventId);
                startActivity(registerIntent);
            }
        });

        final Button contactButton = (Button) this.findViewById(R.id.button_contact);
        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent contactIntent = new Intent(EventDetailActivity.this, ContactOrganizerActivity.class);
                contactIntent.putExtra("eventId",eventId);
                startActivity(contactIntent);
            }
        });

        final ImageView overflowButton = (ImageView) this.findViewById(R.id.ed_overflowMenu);

        overflowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(EventDetailActivity.this, overflowButton);
                popup.getMenuInflater().inflate(R.menu.calendar_fav_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {

                        Intent intent = new Intent(Intent.ACTION_EDIT);
                        intent.setType("vnd.android.cursor.item/event");
                        intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);
                        intent.putExtra(CalendarContract.Events.TITLE, eventTitle);
                        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, eventLocation);
                        startActivity(intent);
                        //Toast.makeText(EventDetailActivity.this,"You Clicked : " + eventdate, Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });

                popup.show();//showing popup menu

            }
        });

    }

    private void retrieveEventDetails() {

        eventRef.child(eventId).addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //eventdate = dataSnapshot.child("date").getValue().toString();
                //LocalDate date = Instant.ofEpochMilli(Long.parseLong(eventdate))
                //        .atOffset(ZoneOffset.of("+08:00"))
                //        .toLocalDate();

                String eventImage = dataSnapshot.child("image").getValue().toString();
                eventTitle = dataSnapshot.child("title").getValue().toString();
                eventLocation = dataSnapshot.child("location").getValue().toString();
                String eventDate = dataSnapshot.child("date").getValue().toString();
                //String eventDate = date.toString();
                String eventDesc = dataSnapshot.child("detail").getValue().toString();
                String eventGender = dataSnapshot.child("gender").getValue().toString();
                String eventLimit = dataSnapshot.child("limit").getValue().toString();
                String eventOrganizer = dataSnapshot.child("organizer").getValue().toString();
                String eventAge = dataSnapshot.child("age").getValue().toString();
                String eventContact = dataSnapshot.child("contact").getValue().toString();

                Picasso.get().load(eventImage).into(ed_image);
                ed_title.setText(eventTitle);
                ed_location.setText(eventLocation);
                ed_date.setText(eventDate);
                ed_description.setText(eventDesc);
                ed_age.setText(eventAge);
                ed_contact.setText(eventContact);
                ed_gender.setText(eventGender);
                ed_organizer.setText(eventOrganizer);
                ed_participantLimit.setText(eventLimit);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {

        int baseColor = getResources().getColor(R.color.colorPrimary);
        float alpha = Math.min(1, (float) scrollY / mParallaxImageHeight);
        mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));
        ViewHelper.setTranslationY(mImageView, scrollY / 8);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        onScrollChanged(mScrollView.getCurrentScrollY(), false, false);
    }


    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }
}
