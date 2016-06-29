package com.gabrielezanelli.schoolendar.fragments;

import android.app.Fragment;
import android.os.Bundle;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.SearchView;

import com.gabrielezanelli.schoolendar.Event;
import com.gabrielezanelli.schoolendar.EventAdapter;
import com.gabrielezanelli.schoolendar.EventManager;
import com.gabrielezanelli.schoolendar.R;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ListViewFragment extends Fragment implements View.OnClickListener {

    private boolean onlyFutureEvents = true;

    private List<Event> allEvents;

    // UI References
    private EventAdapter eventAdapter;
    private RecyclerView recyclerView;

    private SearchView searchView;
    private EventManager eventManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View thisFragment = inflater.inflate(R.layout.fragment_list_view, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getActivity().setTitle(getString(R.string.fragment_title_all_events));
        setHasOptionsMenu(true);
        eventManager = EventManager.getInstance(getActivity());

        recyclerView = (RecyclerView) thisFragment.findViewById(R.id.events_recycler_view);
        searchView = (SearchView) thisFragment.findViewById(R.id.event_search_view);

        initEventsList();
        initSearchViewListener();
        getExtraDateFilterFromArgs();

        // Inflate the layout for this fragment
        return thisFragment;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem item= menu.findItem(R.id.menu_show_old_events);
        item.setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_show_old_events:
                if(item.getTitle().toString().equals(getString(R.string.action_show_old_events))) {
                    onlyFutureEvents = false;
                    item.setTitle(getString(R.string.action_dont_show_old_events));
                }
                else {
                    onlyFutureEvents = true;
                    item.setTitle(getString(R.string.action_show_old_events));
                }
                try {
                    eventAdapter.updateEventList(eventManager.getAllEvents(onlyFutureEvents),searchView.getQuery().toString());

                } catch (SQLException e) {
                    e.printStackTrace();
                }
                eventAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(0);
                return true;
        }
        return false;
    }

    private void initEventsList() {
        try {
            allEvents = eventManager.getAllEvents(onlyFutureEvents);
        }
        catch (SQLException e) {
            Log.d("Events","Database exception");
            e.printStackTrace();
        }

        // Create the Recycle view for the list of events
        eventAdapter = new EventAdapter(allEvents);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(eventAdapter);
    }

    private void initSearchViewListener() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<Event> filteredEvents = new ArrayList<>();
                try {
                    filteredEvents = eventManager.getEventsFiltered(newText, onlyFutureEvents);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                eventAdapter.updateEventList(filteredEvents, newText);
                eventAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(0);
                return true;
            }
        });
    }


    @Override
    public void onClick(View v) {
    }

    private void getExtraDateFilterFromArgs() {
        if(getArguments()==null)
            return;
        long daySelectedMillis = getArguments().getLong(getString(R.string.EXTRA_LONG_DAY_SELECTED), 0);
        if (daySelectedMillis > 0) {
            SimpleDateFormat format = new SimpleDateFormat(getString(R.string.event_date_search_format));
            searchView.setQuery(format.format(new Date(daySelectedMillis)),true);
        }
    }

    // TODO: Divide events per day

    /**
    private void initEventsList() {

        FirebaseRecyclerAdapter<Event, EventViewHolder> eventAdapter = new FirebaseRecyclerAdapter<Event, EventViewHolder>
                (Event.class, R.layout.card_event, EventViewHolder.class, userRef.child("events").orderByChild("subject").equalTo("Inglese")) {
            @Override
            protected void populateViewHolder(final EventViewHolder viewHolder, final Event event, int position) {
                // Sets the title of the event
                String title = event.getTitle();
                viewHolder.titleView.setText(title);

                // Sets the date of the event
                String dateFormat = viewHolder.titleView.getResources().getString(R.string.event_date_format);
                String date = new SimpleDateFormat(dateFormat).format(event.getDate());
                viewHolder.dateView.setText(date);

                // Sets the type+subject of the event
                String typeSubject;
                if (event.hasSubject())
                    typeSubject = event.getSubject() + "'s " + event.getType();
                else
                    typeSubject = event.getType();
                viewHolder.typeSubjectView.setText(typeSubject);

                // Gets the first linear layout parent on the event card and adds the click listener
                ((LinearLayout) viewHolder.titleView.getParent().getParent()).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent eventSelectedIntent = new Intent(v.getContext(), EventFragment.class);
                        eventSelectedIntent.putExtra(viewHolder.EXTRA_LONG_NOTIFICATION_ID, event.getId());
                        v.getContext().startActivity(eventSelectedIntent);
                    }
                });
            }
        };
    }



    private static class EventViewHolder extends RecyclerView.ViewHolder {
        final TextView titleView;
        final TextView dateView;
        final TextView typeSubjectView;
        final String EXTRA_LONG_NOTIFICATION_ID;

        public EventViewHolder(View itemView) {
            super(itemView);
            titleView = (TextView) itemView.findViewById(R.id.eventCardTitle);
            dateView = (TextView) itemView.findViewById(R.id.eventCardDate);
            typeSubjectView = (TextView) itemView.findViewById(R.id.eventCardTypeSubject);
            EXTRA_LONG_NOTIFICATION_ID = itemView.getContext().getString(R.string.EXTRA_LONG_NOTIFICATION_ID);
        }
    }

     */
}
