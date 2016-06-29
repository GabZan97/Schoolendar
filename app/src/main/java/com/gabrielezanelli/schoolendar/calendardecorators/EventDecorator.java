package com.gabrielezanelli.schoolendar.calendardecorators;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

import com.gabrielezanelli.schoolendar.Event;
import com.gabrielezanelli.schoolendar.EventManager;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;


public class EventDecorator implements DayViewDecorator {
    private EventManager eventManager;
    private Calendar date;
    private List<Event.eventType> eventTypes;
    private int color = Color.RED;

    public EventDecorator(Context context) {
        date = Calendar.getInstance(TimeZone.getDefault());
        eventManager = EventManager.getInstance(context);
        // TODO: Add a preference for the color and get it from default shared preferences
        //color = context.getSharedPreferences()
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        date.setTimeInMillis(day.getDate().getTime());
        eventTypes = eventManager.getEventTypesInDate(date);
        if(eventTypes.isEmpty())
            return false;
        return true;
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new DotSpan(8, color));
    }


}


