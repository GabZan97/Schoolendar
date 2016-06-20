package com.gabrielezanelli.schoolendar.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gabrielezanelli.schoolendar.Event;
import com.gabrielezanelli.schoolendar.EventManager;
import com.gabrielezanelli.schoolendar.R;

import java.sql.SQLException;
import java.text.SimpleDateFormat;


public class FragmentEvent extends Fragment {
    private Event notificationEvent;
    private String dateFormat;

    // UI References
    private TextView textTitle;
    private TextView textTypeSubject;
    private TextView textDescription;
    private TextView textDate;
    private TextView textNotification;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View thisFragment = inflater.inflate(R.layout.fragment_event, container, false);


        // TODO: To define the title
        //getActivity().setTitle(getString(R.string.app_name));

        dateFormat = getString(R.string.long_date_format);

        textTitle = (TextView)thisFragment.findViewById(R.id.event_title);
        textTypeSubject = (TextView)thisFragment.findViewById(R.id.event_type_subject);
        textDescription = (TextView)thisFragment.findViewById(R.id.event_description);
        textDate = (TextView)thisFragment.findViewById(R.id.event_date);
        textNotification = (TextView)thisFragment.findViewById(R.id.event_notification);

        if(getEventFromArgs())
           displayEventInformation();

        // TODO: Make the shit pretty

        // TODO: Make the shit editable and save the changes

        // Inflate the layout for this fragment
        return thisFragment;
    }

    private boolean getEventFromArgs() {
        // Gets the ID of the triggered event
        long eventID = getArguments().getLong(getString(R.string.EXTRA_LONG_EVENT_ID),-1);

        if(eventID == -1) {
            Log.d("Event", "Error while retrieving  Event ID from Args");
            return false;
        }

        try {
            notificationEvent = EventManager.getInstance(getActivity()).getEvent(eventID);
            if(notificationEvent == null) {
                Log.d("Event", "Event ID "+ eventID+" not found");
                return false;
            }
            Log.d("Event", "Event load succeeded, ID: "+notificationEvent.getId());
            return true;
        } catch (SQLException e) {
            Log.d("Event", "Database error while retrieving event");
            return false;
        }
    }

    private void displayEventInformation(){
        textTitle.setText(notificationEvent.getTitle());
        textTypeSubject.setText(notificationEvent.getTypePlusSubject());
        textDescription.setText(notificationEvent.getDescription());
        textDate.setText(new SimpleDateFormat(dateFormat).format(notificationEvent.getDate()));
        textNotification.setText(new SimpleDateFormat(dateFormat).format(notificationEvent.getNotificationDate()));
    }
}
