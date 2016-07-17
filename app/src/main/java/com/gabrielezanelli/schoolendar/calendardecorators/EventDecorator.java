package com.gabrielezanelli.schoolendar.calendardecorators;

import android.content.Context;
import android.graphics.Color;
import android.preference.PreferenceManager;

import com.gabrielezanelli.schoolendar.R;
import com.gabrielezanelli.schoolendar.StoreManager;
import com.gabrielezanelli.schoolendar.database.Event;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;


public class EventDecorator implements DayViewDecorator {
    private StoreManager storeManager;
    private Calendar date;
    private List<Event.EventType> eventTypes;
    private int color;

    public EventDecorator(Context context) {
        date = Calendar.getInstance(TimeZone.getDefault());
        storeManager = StoreManager.getInstance();
        // TODO: Add a preference for the color and get it from default shared preferences
        color = PreferenceManager.getDefaultSharedPreferences(context).
                getInt(context.getString(R.string.pref_key_event_highlight_color),Color.RED);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        //return false;
        date.setTimeInMillis(day.getDate().getTime());
       /** eventTypes = storeManager.getEventTypesInDate(date);
        if(eventTypes.isEmpty())
            return false;
        return true;
        */
        return storeManager.areThereEventsInDate(date);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new DotSpan(8, color));
    }


}


