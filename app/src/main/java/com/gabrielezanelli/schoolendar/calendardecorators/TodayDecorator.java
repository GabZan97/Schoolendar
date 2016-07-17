package com.gabrielezanelli.schoolendar.calendardecorators;

import android.content.Context;
import android.graphics.Typeface;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import com.gabrielezanelli.schoolendar.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Highlight Saturdays and Sundays with a background
 */
public class TodayDecorator implements DayViewDecorator {

    private final Calendar date = Calendar.getInstance(TimeZone.getDefault());
    private final Calendar today;
    private final int color;

    public TodayDecorator(Context context) {
        color = context.getResources().getColor(R.color.light_blue);
        today = Calendar.getInstance(TimeZone.getDefault());
        today.set(Calendar.HOUR_OF_DAY,0);
        today.clear(Calendar.MINUTE);
        today.clear(Calendar.SECOND);
        today.clear(Calendar.MILLISECOND);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        date.setTimeInMillis(day.getDate().getTime());
        return date.compareTo(today) == 0;
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new StyleSpan(Typeface.BOLD));
        view.addSpan(new RelativeSizeSpan(1.6f));
        view.addSpan(new ForegroundColorSpan(color));
    }
}