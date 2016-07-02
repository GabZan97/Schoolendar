package com.gabrielezanelli.schoolendar.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gabrielezanelli.schoolendar.R;
import com.gabrielezanelli.schoolendar.activities.MainActivity;
import com.gabrielezanelli.schoolendar.calendardecorators.EventDecorator;
import com.gabrielezanelli.schoolendar.calendardecorators.HighlightWeekendsDecorator;
import com.gabrielezanelli.schoolendar.calendardecorators.TodayDecorator;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.format.TitleFormatter;
import com.prolificinteractive.materialcalendarview.format.WeekDayFormatter;

import org.apache.commons.lang.WordUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MonthViewFragment extends Fragment implements View.OnClickListener {

    // UI References
    private MaterialCalendarView calendarView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View thisFragment = inflater.inflate(R.layout.fragment_month_view, container, false);
        getActivity().setTitle(getString(R.string.app_name));

        calendarView = (MaterialCalendarView) thisFragment.findViewById(R.id.calendarView);
        thisFragment.findViewById(R.id.add_event_fab).setOnClickListener(this);

        initCalendarView();

        // Inflate the layout for this fragment
        return thisFragment;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case (R.id.add_event_fab):
                ((MainActivity)getActivity()).performFragmentTransaction(new AddEventFragment(),true);
        }
    }

    private void initCalendarView() {
        calendarView.addDecorator(new EventDecorator(getActivity()));
        calendarView.addDecorator(new HighlightWeekendsDecorator(getActivity()));
        calendarView.addDecorator(new TodayDecorator(getActivity()));

        // Re-define the title formatter in order to capitalize
        calendarView.setTitleFormatter(new TitleFormatter() {
            @Override
            public CharSequence format(CalendarDay day) {
                String title = new SimpleDateFormat("MMMM yyyy").format(day.getCalendar().getTimeInMillis());
                return WordUtils.capitalize(title);
            }
        });

        // Re-define the week formatter in order to capitalize
        calendarView.setWeekDayFormatter(new WeekDayFormatter() {
            @Override
            public CharSequence format(int dayOfWeek) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
                String weekName = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
                return WordUtils.capitalize(weekName);
            }
        });

        // When a day is selected searches for events in it
        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                ListViewFragment allEvents = new ListViewFragment();
                Bundle extras = new Bundle();
                extras.putLong(getString(R.string.EXTRA_LONG_DAY_SELECTED),date.getCalendar().getTimeInMillis());
                allEvents.setArguments(extras);

                ((MainActivity)getActivity()).performFragmentTransaction(allEvents,true);
            }
        });
    }

}
