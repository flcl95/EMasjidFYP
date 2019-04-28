package com.fyp.emasjid;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class HomeFragment extends Fragment {
    private RecyclerView mRecyclerView1,mRecyclerView2;
    private DatabaseReference mRef,fRef,ref;
    private View view;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home,container,false);
        //recycleView
        mRecyclerView1 = (RecyclerView) view.findViewById(R.id.hrecyclerView1);
        mRecyclerView2 = (RecyclerView) view.findViewById(R.id.hrecyclerView2);
        //set layout as LinearLayout
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        layoutManager1.setReverseLayout(true);
        layoutManager1.setStackFromEnd(true);
        mRecyclerView1.setLayoutManager(layoutManager1);
        mRecyclerView2.setLayoutManager(layoutManager2);
        //snap horizontal scroll
        SnapHelper helper1 = new LinearSnapHelper();
        SnapHelper helper2 = new LinearSnapHelper();
        helper1.attachToRecyclerView(mRecyclerView1);
        helper2.attachToRecyclerView(mRecyclerView2);
        //send Query to FirebaseDB
        mRef = FirebaseDatabase.getInstance().getReference().child("Event");
        fRef = FirebaseDatabase.getInstance().getReference().child("Favorite");
        ref = FirebaseDatabase.getInstance().getReference();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Query sortedQuery = mRef.orderByChild("registered");
        FirebaseRecyclerOptions<Event> options1 =
                new FirebaseRecyclerOptions.Builder<Event>()
                        .setQuery(sortedQuery, Event.class)
                        .build();

        //Query sortedQuery = mRef.orderByChild("title");
        FirebaseRecyclerOptions<Event> options2 =
                new FirebaseRecyclerOptions.Builder<Event>()
                        .setQuery(fRef, Event.class)
                        .build();

        FirebaseRecyclerAdapter<Event,EventFragment.EventViewHolder> adapter1 = new FirebaseRecyclerAdapter<Event, EventFragment.EventViewHolder>(options1) {
            @Override
            protected void onBindViewHolder(@NonNull final EventFragment.EventViewHolder holder, final int position, @NonNull Event model) {

                holder.title.setText(model.getTitle());
                holder.date.setText(model.getDate());
                holder.location.setText(model.getLocation());
                Picasso.get().load(model.getImage()).placeholder(R.drawable.banner).into(holder.image);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String eventId = getRef(position).getKey();
                        Intent eventDetailIntent = new Intent(getActivity(), EventDetailActivity.class);
                        eventDetailIntent.putExtra("eventId", eventId);
                        startActivity(eventDetailIntent);
                    }
                });

            }

            @NonNull
            @Override
            public EventFragment.EventViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.horizontal_row,viewGroup,false);
                EventFragment.EventViewHolder viewHolder = new EventFragment.EventViewHolder(view);
                int width = mRecyclerView1.getWidth();
                ViewGroup.LayoutParams params = view.getLayoutParams();
                params.width = (int)(width * 0.8);
                view.setLayoutParams(params);
                return viewHolder;
            }
        };

        mRecyclerView1.setAdapter(adapter1);
        adapter1.startListening();

        FirebaseRecyclerAdapter<Event,EventFragment.EventViewHolder> adapter2 = new FirebaseRecyclerAdapter<Event, EventFragment.EventViewHolder>(options2) {
            @Override
            protected void onBindViewHolder(@NonNull final EventFragment.EventViewHolder holder, final int position, @NonNull final Event model) {

                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if ((dataSnapshot.hasChild("Favorite"))) {

                            TextView text = (TextView) view.findViewById(R.id.hm_title2_sub);
                            text.setText("Events that you liked");
                            holder.title.setText(model.getTitle());
                            holder.date.setText(model.getDate());
                            holder.location.setText(model.getLocation());
                            Picasso.get().load(model.getImage()).placeholder(R.drawable.banner).into(holder.image);

                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    String eventId = getRef(position).getKey();
                                    Intent eventDetailIntent = new Intent(getActivity(), EventDetailActivity.class);
                                    eventDetailIntent.putExtra("eventId", eventId);
                                    startActivity(eventDetailIntent);
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
            @NonNull
            @Override
            public EventFragment.EventViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.horizontal_row,viewGroup,false);
                EventFragment.EventViewHolder viewHolder = new EventFragment.EventViewHolder(view);
                int width = mRecyclerView1.getWidth();
                ViewGroup.LayoutParams params = view.getLayoutParams();
                params.width = (int)(width * 0.8);
                view.setLayoutParams(params);
                return viewHolder;
            }
        };


        mRecyclerView2.setAdapter(adapter2);
        adapter2.startListening();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder{

        TextView title, date, location;
        ImageView image;
        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.rTitleVu);
            date = (TextView) itemView.findViewById(R.id.rDateVu);
            location = (TextView) itemView.findViewById(R.id.rLocationVu);
            image = (ImageView) itemView.findViewById(R.id.rImageVu);
        }
    }
}

