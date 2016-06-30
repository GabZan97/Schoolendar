package com.gabrielezanelli.schoolendar.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

public class EventInformationSubFragment extends Fragment {

    private Event notificationEvent;
    private String dateFormat;

    // UI References
    private TextView textTitle;
    private TextView textTypeSubject;
    private TextView textDescription;
    private TextView textDate;
    private TextView textNotification;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View thisFragment = inflater.inflate(R.layout.sub_fragment_event_information, container, false);

        dateFormat = getString(R.string.long_date_format);

        textTitle = (TextView) thisFragment.findViewById(R.id.event_title);
        textTypeSubject = (TextView) thisFragment.findViewById(R.id.event_type_subject);
        textDescription = (TextView) thisFragment.findViewById(R.id.event_description);
        textDate = (TextView) thisFragment.findViewById(R.id.event_date);
        textNotification = (TextView) thisFragment.findViewById(R.id.event_notification);

        if (getEventFromID(EventFragment.getSelectedEventID()))
            displayEventInformation();


        return thisFragment;
    }

    private boolean getEventFromID(String eventID) {
        if (eventID.equals("")) {
            Log.d("Event", "Error while retrieving  Event ID from Args");
            return false;
        }

        try {
            notificationEvent = EventManager.getInstance(getActivity()).getEvent(eventID);
            if (notificationEvent == null) {
                Log.d("Event", "Event ID " + eventID + " not found");
                return false;
            }
            Log.d("Event", "Event load succeeded, ID: " + notificationEvent.getId());
            return true;
        } catch (SQLException e) {
            Log.d("Event", "Database error while retrieving event");
            return false;
        }
    }

    private void displayEventInformation() {
        textTitle.setText(notificationEvent.getTitle());
        textTypeSubject.setText(notificationEvent.chainTypePlusSubject());
        textDescription.setText(notificationEvent.getDescription());
        textDate.setText(new SimpleDateFormat(dateFormat).format(notificationEvent.getDate()));
        if (notificationEvent.hasNotification())
            textNotification.setText(new SimpleDateFormat(dateFormat).format(notificationEvent.getNotificationDate()));
        else
            textNotification.setText(getString(R.string.message_any_notification_set));
    }
}
