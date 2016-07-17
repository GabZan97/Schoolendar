package com.gabrielezanelli.schoolendar.fragments;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.gabrielezanelli.schoolendar.NotificationManager;
import com.gabrielezanelli.schoolendar.R;
import com.gabrielezanelli.schoolendar.StoreManager;
import com.gabrielezanelli.schoolendar.activities.MainActivity;
import com.gabrielezanelli.schoolendar.adapters.SubjectSpinnerAdapter;
import com.gabrielezanelli.schoolendar.database.Event;
import com.gabrielezanelli.schoolendar.database.Subject;

import org.apache.commons.lang.WordUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class AddEventFragment extends Fragment implements View.OnClickListener {

    private StoreManager storeManager;
    private String dateFormat;
    private String dateTimeFormat;

    private Calendar displayedCalendar;
    private Calendar eventDate;
    private Calendar notificationDate;

    private SubjectSpinnerAdapter eventSubjectAdapter;

    // UI References
    private EditText eventTitleEdit;
    private EditText eventDescriptionEdit;
    private TextView dateText;
    private TextView notificationText;
    private DatePickerDialog datePicker;
    private DatePickerDialog notificationDatePicker;
    private TimePickerDialog notificationTimePicker;
    private Button cancelButton;
    private Button addButton;
    private FloatingActionButton manageSubjectsButton;
    private Spinner eventTypeSpinner;
    private Spinner eventSubjectSpinner;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View thisFragment = inflater.inflate(R.layout.fragment_add_event, container, false);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        getActivity().setTitle(getString(R.string.fragment_title_add_event));

        storeManager = StoreManager.getInstance();

        dateFormat = getString(R.string.long_date_format);
        dateTimeFormat = getString(R.string.full_date_time_format);

        eventTypeSpinner = (Spinner) thisFragment.findViewById(R.id.event_type);
        eventSubjectSpinner = (Spinner) thisFragment.findViewById(R.id.event_subject);

        eventTitleEdit = (EditText) thisFragment.findViewById(R.id.event_title);
        eventDescriptionEdit = (EditText) thisFragment.findViewById(R.id.event_description);
        dateText = (TextView) thisFragment.findViewById(R.id.event_date);
        notificationText = (TextView) thisFragment.findViewById(R.id.event_notification);

        cancelButton = (Button) thisFragment.findViewById(R.id.cancel_button);
        addButton = (Button) thisFragment.findViewById(R.id.add_button);
        manageSubjectsButton = (FloatingActionButton) thisFragment.findViewById(R.id.manage_subjects_button);

        initSpinners();
        initClickListeners();

        // Inflate the layout for this fragment
        return thisFragment;
    }

    private void setDefaultNotification() {
        boolean defaultNotificationEnabled = PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getBoolean(getString(R.string.pref_key_enable_default_notifications),false);

        if(!defaultNotificationEnabled)
            return;

        long daysBefore = Long.parseLong(PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getString(getString(R.string.pref_key_notification_day),"-1"));
        long time = PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getLong(getString(R.string.pref_key_notification_time),-1);

        if(daysBefore == -1 || time == -1) {
            Log.d("Default notification","Couldn't retrieve values");
            return;
        }

        long daysBeforeInMillis = daysBefore*24*60*60*1000;

        Calendar timeCalendar = Calendar.getInstance(TimeZone.getDefault());
        timeCalendar.setTimeInMillis((time));

        notificationDate = Calendar.getInstance();
        notificationDate.setTimeInMillis(eventDate.getTimeInMillis()-daysBeforeInMillis);
        notificationDate.set(Calendar.HOUR_OF_DAY,timeCalendar.get(Calendar.HOUR_OF_DAY));
        notificationDate.set(Calendar.MINUTE,timeCalendar.get(Calendar.MINUTE));

        notificationText.setText(WordUtils.capitalize(
                new SimpleDateFormat(getString(R.string.full_date_time_format)).format(notificationDate.getTime())));
    }

    // Sets up the spinners from layouts and resources
    private void initSpinners() {
        // Inflates the Events Types in the spinner from a resource array
        final ArrayAdapter<CharSequence> eventTypeAdapter = ArrayAdapter.createFromResource
                (getActivity(), R.array.event_types, R.layout.item_spinner_type);
        eventTypeSpinner.setAdapter(eventTypeAdapter);

        // Inflates the Event Subjects in the spinner retrieving data from Database
        List<Subject> subjects = storeManager.getSubjects();
        eventSubjectAdapter = new SubjectSpinnerAdapter(getActivity(),R.layout.item_spinner_subject,subjects);
        eventSubjectSpinner.setAdapter(eventSubjectAdapter);

        // Checks what is selected as event type and enables on disables the subject spinner when needed
        eventTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Event.EventType eventType = Event.EventType.valueOf((String) eventTypeSpinner.getSelectedItem());
                    TextView subjectText = (TextView)eventSubjectSpinner.findViewById(R.id.spinner_subject_text);
                    if (eventType == Event.EventType.Communication || eventType == Event.EventType.Note) {
                        eventSubjectSpinner.setEnabled(false);
                        if(subjectText !=null)
                            subjectText.setVisibility(View.GONE);
                    } else {
                        eventSubjectSpinner.setEnabled(true);
                        if(subjectText!=null)
                            subjectText.setVisibility(View.VISIBLE);
                    }
                } catch (NullPointerException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void initClickListeners() {
        cancelButton.setOnClickListener(this);
        addButton.setOnClickListener(this);
        manageSubjectsButton.setOnClickListener(this);
        dateText.setOnClickListener(this);
        notificationText.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            // Sets up the Date Picker
            case (R.id.event_date):
                if(eventDate==null)
                    displayedCalendar = Calendar.getInstance(TimeZone.getDefault());
                else
                    displayedCalendar.setTimeInMillis(eventDate.getTimeInMillis());

                datePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    // Set the action triggered when the date is selected
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
                        eventDate = Calendar.getInstance();
                        eventDate.set(year, month, day, 0, 0, 0);
                        eventDate.set(Calendar.MILLISECOND,0);

                        dateText.setText(WordUtils.capitalize(formatter.format(eventDate.getTime())));
                        dateText.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

                        setDefaultNotification();
                    }
                }, // Sets the DatePicker's selected date to current when it shows up
                        displayedCalendar.get(Calendar.YEAR), displayedCalendar.get(Calendar.MONTH), displayedCalendar.get(Calendar.DAY_OF_MONTH));
                datePicker.show();
                break;

            // Sets up the Date Picker and Time Picker for the Notification
            case (R.id.event_notification):
                if(notificationDate==null)
                    displayedCalendar = Calendar.getInstance(TimeZone.getDefault());
                else
                    displayedCalendar.setTimeInMillis(notificationDate.getTimeInMillis());

                // Init the Date Picker and show it
                notificationDatePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        notificationDate = Calendar.getInstance(TimeZone.getDefault());
                        notificationDate.set(year, month, day);
                        notificationTimePicker.show();
                    }
                },  // Set the DatePicker's selected date to the current
                        displayedCalendar.get(Calendar.YEAR), displayedCalendar.get(Calendar.MONTH), displayedCalendar.get(Calendar.DAY_OF_MONTH));

                notificationTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    public void onTimeSet(TimePicker view, int hour, int minute) {
                        SimpleDateFormat formatter = new SimpleDateFormat(dateTimeFormat);
                        notificationDate.set(Calendar.HOUR_OF_DAY, hour);
                        notificationDate.set(Calendar.MINUTE, minute);
                        notificationDate.set(Calendar.SECOND, 0);
                        notificationDate.set(Calendar.MILLISECOND, 0);

                        notificationText.setText(WordUtils.capitalize(formatter.format(notificationDate.getTime())));
                        notificationText.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
                    }
                }, // Set the TimePicker's selected time to the current
                        displayedCalendar.get(Calendar.HOUR_OF_DAY), displayedCalendar.get(Calendar.MINUTE), true);

                // If the user doesn't set a time for the notification undo the entire action
                notificationTimePicker.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        notificationDate = null;
                    }
                });

                notificationDatePicker.show();
                break;

            // Confirm event add
            case (R.id.add_button):
                if(!checkFields())
                    break;
                    try {
                        String title = eventTitleEdit.getText().toString();
                        String description= eventDescriptionEdit.getText().toString();
                        Event.EventType type= Event.EventType.valueOf(eventTypeSpinner.getSelectedItem().toString());
                        long date = eventDate.getTimeInMillis();

                        Event newEvent = new Event(title, description, type, date);
                        if (notificationDate != null)
                            newEvent.setNotificationDate(notificationDate.getTimeInMillis());
                        if(eventSubjectSpinner.isEnabled())
                            newEvent.setSubject(eventSubjectAdapter.getSubjectByPosition(eventSubjectSpinner.getSelectedItemPosition()));

                        storeManager.addEvent(newEvent);

                        // If the Event has a notification, schedules it
                        if (newEvent.hasNotification())
                            NotificationManager.scheduleNotification(getActivity(), newEvent.getId(), newEvent.getNotificationDate());

                        Toast.makeText(getActivity(), R.string.toast_event_add_succeeded, Toast.LENGTH_SHORT).show();

                    } catch (NullPointerException ex) {
                        ex.printStackTrace();
                        Toast.makeText(getActivity(), R.string.toast_event_add_failed, Toast.LENGTH_SHORT).show();
                    } finally {
                        getFragmentManager().popBackStack();
                    }
                break;

            // Cancel event add
            case (R.id.cancel_button):
                getFragmentManager().popBackStack();
                break;

            // Manage Subjects FAB
            case (R.id.manage_subjects_button):
                ((MainActivity)getActivity()).performFragmentTransaction(new ManageSubjectsFragment(),true);
        }
    }

    private boolean checkFields() {
        // Check title
        if (eventTitleEdit.getText() == null || eventTitleEdit.getText().toString().equals("")) {
            eventTitleEdit.setError(getString(R.string.error_missing_title));
            eventTitleEdit.requestFocus();
            return false;
        }

        // Check subject
        if (eventSubjectSpinner.isEnabled()) {
            if (eventSubjectSpinner.getSelectedItem() == null) {
                Toast.makeText(getActivity(), R.string.error_missing_subject, Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        // Check if the event has a date
        if (eventDate == null) {
            Toast.makeText(getActivity(), R.string.error_missing_date, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}