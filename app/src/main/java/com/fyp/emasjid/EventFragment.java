package com.fyp.emasjid;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.appcompat.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import static android.view.MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW;
import static android.view.MenuItem.SHOW_AS_ACTION_IF_ROOM;

public class EventFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private DatabaseReference mRef,fRef;
    private View view;
    private LinearLayoutManager linearLayoutManager;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.activity_event,container,false);
        //recycleView
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        //set layout as LinearLayout

        linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        //send Query to FirebaseDB
        mRef = FirebaseDatabase.getInstance().getReference().child("Event");
        fRef = FirebaseDatabase.getInstance().getReference().child("Favorite");

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        Query sortedQuery = mRef.orderByChild("title");
        FirebaseRecyclerOptions<Event> options =
                new FirebaseRecyclerOptions.Builder<Event>()
                .setQuery(sortedQuery, Event.class)
                .build();

        FirebaseRecyclerAdapter<Event,EventViewHolder> adapter = new FirebaseRecyclerAdapter<Event, EventViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final EventViewHolder holder, final int position, @NonNull Event model) {

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

                final String fEventId = getRef(position).getKey();
                fRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    Boolean isFav = true;
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(fEventId)){
                            holder.favicon.setBackgroundResource(R.drawable.ic_star_black_24dp);
                            holder.favicon.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    holder.favicon.setBackgroundResource(R.drawable.ic_star_border_black_24dp);
                                    Toast.makeText(getActivity(),"fav clicked", Toast.LENGTH_SHORT).show();
                                    fRef.child(fEventId).removeValue();
                                }
                            });

                        }
                        else {
                            holder.favicon.setBackgroundResource(R.drawable.ic_star_border_black_24dp);
                            holder.favicon.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    holder.favicon.setBackgroundResource(R.drawable.ic_star_black_24dp);
                                    Toast.makeText(getActivity(),"fav clicked", Toast.LENGTH_SHORT).show();
                                    addtoFavorite(mRef,fRef,fEventId);
                                    //isFav = !isFav;
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
            public EventViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row,viewGroup,false);
                EventViewHolder viewHolder = new EventViewHolder(view);
                return viewHolder;
            }
        };

        mRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }


    public static class EventViewHolder extends RecyclerView.ViewHolder{

        TextView title, date, location;
        ImageView image, favicon;
        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.rTitleVu);
            date = (TextView) itemView.findViewById(R.id.rDateVu);
            location = (TextView) itemView.findViewById(R.id.rLocationVu);
            image = (ImageView) itemView.findViewById(R.id.rImageVu);
            favicon = (ImageView) itemView.findViewById(R.id.button_fav);
        }
    }

    public void addtoFavorite (final DatabaseReference fromPath, final DatabaseReference toPath, final String eventId){
        fromPath.child(eventId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                toPath.child(eventId).setValue(dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.search, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = new SearchView(((MainActivity) getActivity()).getSupportActionBar().getThemedContext());
        item.setShowAsAction(SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW|SHOW_AS_ACTION_IF_ROOM);
        item.setActionView(searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                firebaseSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                firebaseSearch(newText);
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    //Search View Function
    private void firebaseSearch(String searchtext){
        Query firebaseSearchQuery = mRef.orderByChild("title").startAt(searchtext).endAt(searchtext + "\uf8ff");

        FirebaseRecyclerOptions<Event> options =
                new FirebaseRecyclerOptions.Builder<Event>()
                        .setQuery(firebaseSearchQuery, Event.class)
                        .build();

        FirebaseRecyclerAdapter<Event,EventViewHolder> searchAdapter = new FirebaseRecyclerAdapter<Event, EventViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull EventViewHolder holder, final int position, @NonNull Event model) {

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
            public EventViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row,viewGroup,false);
                EventViewHolder viewHolder = new EventViewHolder(view);
                return viewHolder;
            }
        };

        mRecyclerView.setAdapter(searchAdapter);
        searchAdapter.startListening();
    }

    private void loadContentFragment() {
        EventFragment f2 = new EventFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, f2);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}

