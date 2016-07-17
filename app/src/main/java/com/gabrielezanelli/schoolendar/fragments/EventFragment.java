package com.gabrielezanelli.schoolendar.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.gabrielezanelli.schoolendar.StoreManager;
import com.gabrielezanelli.schoolendar.R;
import com.gabrielezanelli.schoolendar.activities.MainActivity;
import com.gabrielezanelli.schoolendar.database.Event;

import java.util.ArrayList;
import java.util.List;


public class EventFragment extends Fragment implements View.OnClickListener {

    private static Event selectedEvent;

    // UI References
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private FloatingActionButton editButton;
    private FloatingActionButton deleteButton;
    private static String TAG_EVENT_FRAGMENT = "Event Fragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View thisFragment = inflater.inflate(R.layout.fragment_event, container, false);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        viewPager = (ViewPager) thisFragment.findViewById(R.id.view_pager);
        editButton = (FloatingActionButton) thisFragment.findViewById(R.id.edit_event_fab);
        deleteButton = (FloatingActionButton) thisFragment.findViewById(R.id.delete_event_fab);
        tabLayout = (TabLayout) thisFragment.findViewById(R.id.tabs);

        // TODO: Fix Pager not initializing pages after the first time.
        if (getSelectedEventFromArgs()) {
            getActivity().setTitle(getString(R.string.fragment_title_event));
        } else
            getActivity().setTitle(getString(R.string.fragment_title_event_not_found));


        deleteButton.setOnClickListener(this);
        initPager();

        // TODO: Make the shit editable and save the changes

        // Inflate the layout for this fragment
        return thisFragment;
    }

    private void initPager() {
        Log.d(TAG_EVENT_FRAGMENT, "Initializing pager");

        PagerAdapter pagerAdapter = new PagerAdapter(((MainActivity) getActivity()).getSupportFragmentManager());
        pagerAdapter.addFragment(new EventInformationSubFragment(), getString(R.string.fragment_title_event_information));
        pagerAdapter.addFragment(new EventTasksSubFragment(), getString(R.string.fragment_title_event_tasks));

        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.delete_event_fab):
                StoreManager.getInstance().deleteEvent(selectedEvent);
                getFragmentManager().popBackStack();

                break;
            case (R.id.edit_event_fab):

                break;
        }
    }

    private boolean getSelectedEventFromArgs() {
        // Gets the ID of the triggered selectedEvent
        String eventId = getArguments().getString(getString(R.string.EXTRA_STRING_EVENT_ID), "");

        if (eventId.equals("")) {
            Log.d(TAG_EVENT_FRAGMENT, "Error while retrieving Event ID from Args");
            return false;
        }
        selectedEvent = StoreManager.getInstance().getEvent(eventId);
        Log.d(TAG_EVENT_FRAGMENT, "Event retrieved");
        return true;
    }

    public static Event getSelectedEvent() {
        return selectedEvent;
    }


    public static class PagerAdapter extends FragmentPagerAdapter {
        private final List<android.support.v4.app.Fragment> fragmentsList = new ArrayList<>();
        private final List<String> fragmentsTitleList = new ArrayList<>();

        public PagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return fragmentsList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentsList.size();
        }

        public void addFragment(android.support.v4.app.Fragment fragment, String title) {
            fragmentsList.add(fragment);
            fragmentsTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentsTitleList.get(position);
        }
    }


}
