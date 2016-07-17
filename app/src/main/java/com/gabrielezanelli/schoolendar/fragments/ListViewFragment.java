package com.gabrielezanelli.schoolendar.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.SearchView;

import com.gabrielezanelli.schoolendar.R;
import com.gabrielezanelli.schoolendar.StoreManager;
import com.gabrielezanelli.schoolendar.adapters.EventAdapter;
import com.gabrielezanelli.schoolendar.database.Event;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ListViewFragment extends Fragment implements View.OnClickListener {

    private boolean onlyFutureEvents = true;

    // UI References
    private EventAdapter eventAdapter;
    private RecyclerView recyclerView;

    private SearchView searchView;
    private StoreManager storeManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View thisFragment = inflater.inflate(R.layout.fragment_list_view, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getActivity().setTitle(getString(R.string.fragment_title_all_events));
        setHasOptionsMenu(true);
        storeManager = StoreManager.getInstance();

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
        MenuItem item = menu.findItem(R.id.menu_show_old_events);
        item.setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_show_old_events:
                if (item.getTitle().toString().equals(getString(R.string.action_show_old_events))) {
                    onlyFutureEvents = false;
                    item.setTitle(getString(R.string.action_dont_show_old_events));
                } else {
                    onlyFutureEvents = true;
                    item.setTitle(getString(R.string.action_show_old_events));
                }

                eventAdapter.updateEventList(storeManager.getEvents(onlyFutureEvents), searchView.getQuery().toString());
                eventAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(0);
                return true;
        }
        return false;
    }

    private void initEventsList() {
        // Create the Recycle view for the list of events
        eventAdapter = new EventAdapter(storeManager.getEvents(onlyFutureEvents));
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
                List<Event> filteredEvents = storeManager.getEventsByQueryText(newText, onlyFutureEvents);

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
        if (getArguments() == null)
            return;
        long daySelectedMillis = getArguments().getLong(getString(R.string.EXTRA_LONG_DAY_SELECTED), 0);
        if (daySelectedMillis > 0) {
            SimpleDateFormat format = new SimpleDateFormat(getString(R.string.event_date_search_format));
            searchView.setQuery(format.format(new Date(daySelectedMillis)), true);
        }
    }

    // TODO: Divide events per day
}
