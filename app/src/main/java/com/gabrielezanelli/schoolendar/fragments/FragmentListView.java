package com.gabrielezanelli.schoolendar.fragments;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.os.Bundle;


import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import com.gabrielezanelli.schoolendar.Event;
import com.gabrielezanelli.schoolendar.EventAdapter;
import com.gabrielezanelli.schoolendar.EventManager;
import com.gabrielezanelli.schoolendar.R;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentListView extends Fragment implements View.OnClickListener {

    private String typeFilter;
    private String subjectFilter;
    private Calendar dateFilter;

    private List<Event> allEvents;

    private int enabledColor;
    private int disableColor;

    // UI References
    private Spinner typeSpinner;
    private Spinner subjectSpinner;

    private EventAdapter eventAdapter;
    private RecyclerView recyclerView;

    private SearchView searchView;
    private EventManager eventManager;
    private DatePickerDialog dateFilterPicker;
    private TextView dateText;
    private FloatingActionButton addFilterButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View thisFragment = inflater.inflate(R.layout.fragment_list_view, container, false);

        getActivity().setTitle(getString(R.string.fragment_title_all_events));
        eventManager = EventManager.getInstance(getActivity());

        recyclerView = (RecyclerView) thisFragment.findViewById(R.id.events_recycler_view);
        searchView = (SearchView) thisFragment.findViewById(R.id.event_search_view);
        addFilterButton = (FloatingActionButton) thisFragment.findViewById(R.id.add_filter_button);


        /**
        typeSpinner = (Spinner) thisFragment.findViewById(R.id.type_spinner);
        subjectSpinner = (Spinner) thisFragment.findViewById(R.id.subject_spinner);
        dateText = (TextView) thisFragment.findViewById(R.id.event_date);

        thisFragment.findViewById(R.id.remove_type_filter).setOnClickListener(this);
        thisFragment.findViewById(R.id.remove_subject_filter).setOnClickListener(this);
        thisFragment.findViewById(R.id.remove_date_filter).setOnClickListener(this);
        */

        enabledColor =getActivity().getResources().getColor(R.color.black);
        disableColor=getActivity().getResources().getColor(R.color.grey);

        initEventsList();
        initSearchViewListener();
        //initFilters();
        //getExtraDateFilterFromArgs();
        //refreshEventsList();

        // Inflate the layout for this fragment
        return thisFragment;
    }

    private void initEventsList() {
        try {
            allEvents = EventManager.getInstance(getActivity()).getAllEvents();
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
        Log.d("Search View", "Setting listener...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("Search View", "I'm querying!");
                final List<Event> filteredEvents = filter(allEvents, newText);
                eventAdapter.animateTo(filteredEvents);
                recyclerView.scrollToPosition(0);
                return true;
            }
        });
    }

    private List<Event> filter(List<Event> events, String query) {
        query = query.toLowerCase();
        Log.d("Search View", "Query: "+query);
        final List<Event> filteredEvents = new ArrayList<>();
        for (Event event : events) {
            final String title = event.getTitle().toLowerCase();
            Log.d("Search View", "Title found: "+title);
            if (title.contains(query)) {
                filteredEvents.add(event);
            }
        }
        return filteredEvents;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case(R.id.add_filter_button):

                break;
            /**
            case(R.id.remove_type_filter):
                typeSpinner.findViewById(R.id.spinner_type_text).setVisibility(View.GONE);
                typeFilter = "";
                subjectSpinner.setEnabled(true);
                refreshEventsList();
                break;

            case(R.id.remove_subject_filter):
                subjectSpinner.findViewById(R.id.spinner_subject_text).setVisibility(View.GONE);
                subjectFilter = "";
                refreshEventsList();
                break;

            case(R.id.remove_date_filter):
                dateText.setText("");
                dateFilter = null;
                refreshEventsList();
                break;

            case(R.id.event_date):
                Calendar myCalendar = Calendar.getInstance(TimeZone.getDefault());
                if(!dateText.getText().equals(""))
                    myCalendar.setTime(dateFilter.getTime());

                dateFilterPicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    // Set the action triggered when the date is selected
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        SimpleDateFormat formatter = new SimpleDateFormat(getString(R.string.short_date_format));
                        dateFilter = Calendar.getInstance();
                        dateFilter.set(year, month, day, 0, 0, 0);
                        dateFilter.set(Calendar.MILLISECOND,0);
                        dateText.setText(formatter.format(dateFilter.getTime()));
                        dateText.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

                        refreshEventsList();
                    }
                }, // Sets the DatePicker's selected date to current when it shows up
                        myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                dateFilterPicker.show();
                break;
             */
        }
    }

    /**
    private void initFilters() {
        // Inflates the event types in the spinner
        final ArrayAdapter<CharSequence> eventTypeAdapter = ArrayAdapter.createFromResource
                (getActivity(), R.array.event_types, R.layout.item_spinner_type);
        typeSpinner.setAdapter(eventTypeAdapter);

        // Inflates the subjects in the spinner retrieving from Firebase Database
        final FirebaseListAdapter<String> eventSubjectAdapter = new FirebaseListAdapter<String>
                (getActivity(), String.class, R.layout.item_spinner_subject, FirebaseUser.getSubjectsRef()) {

            @Override
            protected void populateView(View v, String subject, int position) {
                ((TextView)v).setText(subject);
            }
        };
        subjectSpinner.setAdapter(eventSubjectAdapter);

        // Reset the type,subject and date filters
        typeFilter = Event.eventType.Homework.toString();
        subjectFilter ="";
        dateText.setText("");
        dateFilter = null;

        // TODO: FIX VISIBILITY ABSOLUTELY JESUS CHRIST HOW IS THIS POSSIBLE OMG
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View typeText, int position, long id) {

                // Checks for the event type in order to determine if subject is needed
                Event.eventType eventType = Event.eventType.valueOf((String) typeSpinner.getSelectedItem());
                TextView subjectText = (TextView)subjectSpinner.findViewById(R.id.spinner_subject_text);
                if (eventType == Event.eventType.Communication || eventType == Event.eventType.Note) {
                    subjectSpinner.setEnabled(false);
                    subjectFilter="";
                    if(subjectText !=null)
                        subjectText.setTextColor(disableColor);
                } else {
                    subjectSpinner.setEnabled(true);
                    if(subjectText!=null)
                        subjectText.setTextColor(enabledColor);
                }
                refreshEventsList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        subjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                subjectFilter = subjectSpinner.getSelectedItem().toString();
                refreshEventsList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dateText.setOnClickListener(this);
    }
     */

    private void getExtraDateFilterFromArgs() {
        if(getArguments()==null)
            return;
        long daySelectedMillis = getArguments().getLong(getString(R.string.EXTRA_LONG_DAY_SELECTED), 0);
        if (daySelectedMillis > 0) {
            SimpleDateFormat formatter = new SimpleDateFormat(getString(R.string.short_date_format));
            dateFilter = Calendar.getInstance();
            dateFilter.setTimeInMillis((daySelectedMillis / 1000) * 1000);
            dateText.setText(formatter.format(dateFilter.getTime()));
            dateText.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
        }
    }

    // TODO: Show events in chronologic order
    // TODO: Show only future events
    // TODO: Give the possiibily to show previus events
    // TODO: Divide events per day
    
    private void refreshEventsList() {

        // Creates the Map that contains the event filters
        Map<String, Object> eventFilter = new HashMap<>();

        Log.d("Tipo", "LOG: " + typeFilter);
        // If the type filter is not empty, adds it
        if (!typeFilter.equals(""))
            eventFilter.put("type", typeFilter);

        Log.d("Materia", "LOG: " + subjectFilter);
        // If the subject filter is not empty, adds it
        if (!subjectFilter.equals(""))
            eventFilter.put("subject", subjectFilter);

        // If the date filter is not null, adds it
        if (dateFilter != null) {
            Log.d("Data:", "LOG: " + dateFilter.getTimeInMillis());
            eventFilter.put("event_date", dateFilter.getTimeInMillis());
        }


        try {
            allEvents = eventManager.getEventsFiltered(eventFilter);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        eventAdapter.animateTo(allEvents);
    }

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
                        Intent eventSelectedIntent = new Intent(v.getContext(), FragmentEvent.class);
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
