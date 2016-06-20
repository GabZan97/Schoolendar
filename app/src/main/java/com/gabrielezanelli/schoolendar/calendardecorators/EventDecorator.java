package com.gabrielezanelli.schoolendar.calendardecorators;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;

import com.gabrielezanelli.schoolendar.EventManager;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.Calendar;
import java.util.TimeZone;


public class EventDecorator implements DayViewDecorator {
    private Context context;
    private Calendar date;

    public EventDecorator(Context context) {
        date = Calendar.getInstance(TimeZone.getDefault());
        this.context = context;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        date.setTimeInMillis(day.getDate().getTime());
        return EventManager.getInstance(context).areThereEventsInDate(date);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new DotSpan(8, Color.RED));
        // TODO: Make the dot centered
    }


}


