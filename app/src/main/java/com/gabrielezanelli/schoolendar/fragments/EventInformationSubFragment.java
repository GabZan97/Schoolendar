package com.gabrielezanelli.schoolendar.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gabrielezanelli.schoolendar.StoreManager;
import com.gabrielezanelli.schoolendar.database.Event;
import com.gabrielezanelli.schoolendar.R;

import java.text.SimpleDateFormat;

public class EventInformationSubFragment extends Fragment {

    private Event displayedEvent;
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


        if (EventFragment.getSelectedEvent() != null)
            displayEventInformation();
        else
            Log.d("Event Information", "Event not found");

        return thisFragment;
    }

    private void displayEventInformation() {
        displayedEvent = EventFragment.getSelectedEvent();
        textTitle.setText(displayedEvent.getTitle());
        textTypeSubject.setText(displayedEvent.chainTypeSubject());
        textDescription.setText(displayedEvent.getDescription());
        textDate.setText(new SimpleDateFormat(dateFormat).format(displayedEvent.getDate()));
        if (displayedEvent.hasNotification())
            textNotification.setText(new SimpleDateFormat(dateFormat).format(displayedEvent.getNotificationDate()));
        else
            textNotification.setText(getString(R.string.message_any_notification_set));
    }
}
