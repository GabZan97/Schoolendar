package com.gabrielezanelli.schoolendar.adapters;

import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gabrielezanelli.schoolendar.R;
import com.gabrielezanelli.schoolendar.activities.MainActivity;
import com.gabrielezanelli.schoolendar.database.Event;
import com.gabrielezanelli.schoolendar.fragments.EventFragment;

import org.apache.commons.lang.WordUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventHolder> {
    private final List<Event> events;
    private String filter = "";

    public EventAdapter(List<Event> events) {
        this.events = new ArrayList<>(events);
    }

    // When the View Holder is created inflates the layout
    @Override
    public EventHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View holderView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_event, parent, false);
        return new EventHolder(holderView);
    }

    // When the View Holder get scrolled or updated, updates the view holder's content
    @Override
    public void onBindViewHolder(EventHolder holder, int position) {
        holder.updateView(events.get(position), filter);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public void updateEventList(List<Event> events, String filter) {
        if (filter == null)
            this.filter = "";
        else
            this.filter = filter;

        applyAndAnimateRemovals(events);
        applyAndAnimateAdditions(events);
        applyAndAnimateMovedItems(events);
    }

    private void applyAndAnimateRemovals(List<Event> newEvents) {
        for (int i = events.size() - 1; i >= 0; i--) {
            final Event event = events.get(i);
            if (!newEvents.contains(event)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<Event> newEvents) {
        for (int i = 0, count = newEvents.size(); i < count; i++) {
            final Event event = newEvents.get(i);
            if (!events.contains(event)) {
                addItem(i, event);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<Event> newEvents) {
        for (int toPosition = newEvents.size() - 1; toPosition >= 0; toPosition--) {
            final Event event = newEvents.get(toPosition);
            final int fromPosition = events.indexOf(event);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public Event removeItem(int position) {
        final Event event = events.remove(position);
        notifyItemRemoved(position);
        return event;
    }

    public void addItem(int position, Event event) {
        events.add(position, event);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final Event event = events.remove(fromPosition);
        events.add(toPosition, event);
        notifyItemMoved(fromPosition, toPosition);
    }


    protected static class EventHolder extends RecyclerView.ViewHolder {

        private Event currentEvent;

        private TextView titleText;
        private TextView dateText;
        private TextView typeSubjectText;
        private String dateFormat;
        private StyleSpan bold;
        private BackgroundColorSpan backgroundHighlight;
        private GradientDrawable leftBorder;
        private Resources res;

        private EventHolder(View itemView) {
            super(itemView);
            titleText = (TextView) itemView.findViewById(R.id.event_card_title);
            dateText = (TextView) itemView.findViewById(R.id.event_card_date);
            dateFormat = itemView.getResources().getString(R.string.event_date_format);
            typeSubjectText = (TextView) itemView.findViewById(R.id.event_card_type_subject);
            bold = new StyleSpan(android.graphics.Typeface.BOLD);

            res = itemView.getResources();
            leftBorder = (GradientDrawable) ((LayerDrawable)itemView.getBackground())
                    .findDrawableByLayerId(R.id.left_coloured_border);
            
            backgroundHighlight = new BackgroundColorSpan(res.getColor(R.color.yellow));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventFragment event = new EventFragment();
                    Bundle extras = new Bundle();
                    extras.putString(v.getContext().getString(R.string.EXTRA_STRING_EVENT_ID),currentEvent.getId());
                    event.setArguments(extras);
                    Log.d("All Events", "Opening event with ID: " + currentEvent.getId());
                    ((MainActivity) v.getContext()).performFragmentTransaction(event, true);
                }
            });
        }

        private void updateView(final Event updatingEvent, String filter) {
            currentEvent = updatingEvent;
            // Highlight the searched text  {START}
            String newTitle = updatingEvent.getTitle();
            String newTypeSubject = updatingEvent.chainTypeSubject();
            String newDate = WordUtils.capitalize(new SimpleDateFormat(dateFormat).format(updatingEvent.getDate()));
            int filterLength = filter.length();

            if (filterLength<1) {
                titleText.setText(newTitle);
                typeSubjectText.setText(newTypeSubject);
                dateText.setText(newDate);
            } else {
                int titleStart = newTitle.toLowerCase().indexOf(filter.toLowerCase());
                // TODO: Highlight only start of the string for the type
                // TODO: Highlight nly start of the string for the subject
                int typeSubjectStart = newTypeSubject.toLowerCase().indexOf(filter.toLowerCase());
                int dateStart = newDate.toLowerCase().indexOf(filter.toLowerCase());

                if(titleStart>=0) {
                    int titleEnd = titleStart + filterLength;
                    Spannable titleSpannable = new SpannableString(newTitle);
                    titleSpannable.setSpan(backgroundHighlight, titleStart, titleEnd, Spanned.SPAN_COMPOSING);
                    titleSpannable.setSpan(bold, titleStart, titleEnd, Spanned.SPAN_COMPOSING);
                    titleText.setText(titleSpannable);
                }
                else
                    titleText.setText(newTitle);
                if(typeSubjectStart>=0) {
                    int typeSubjectEnd = typeSubjectStart + filterLength;
                    Spannable typeSubjectSpannable = new SpannableString(newTypeSubject);
                    typeSubjectSpannable.setSpan(backgroundHighlight, typeSubjectStart, typeSubjectEnd, Spanned.SPAN_COMPOSING);
                    typeSubjectSpannable.setSpan(bold, typeSubjectStart, typeSubjectEnd, Spanned.SPAN_COMPOSING);
                    typeSubjectText.setText(typeSubjectSpannable);
                }
                else
                    typeSubjectText.setText(newTypeSubject);
                if(dateStart>=0) {
                    int dateEnd = dateStart + filterLength;
                    Spannable dateSpannable = new SpannableString(newDate);
                    dateSpannable.setSpan(backgroundHighlight,dateStart,dateEnd,Spanned.SPAN_COMPOSING);
                    dateSpannable.setSpan(bold,dateStart,dateEnd,Spanned.SPAN_COMPOSING);
                    dateText.setText(dateSpannable);
                }
                else
                    dateText.setText(newDate);
            }
            // Highlight the searched text  {START}

            // Set the color of the border  {START}
            Event.EventType type = Event.EventType.valueOf(updatingEvent.getType());

            if(type == Event.EventType.Homework)
                leftBorder.setColor(res.getColor(R.color.yellow));
            else if(type == Event.EventType.Test)
                leftBorder.setColor(res.getColor(R.color.red));
            else if(type == Event.EventType.Project)
                leftBorder.setColor(res.getColor(R.color.blue));
            else if(type == Event.EventType.Communication)
                leftBorder.setColor(res.getColor(R.color.green));
            else if(type == Event.EventType.Note)
                leftBorder.setColor(res.getColor(R.color.aqua));
            else if(type == Event.EventType.ClassevivaEvent)
                leftBorder.setColor(res.getColor(R.color.orange));
            // Highlight the searched text  {END}

        }
    }
}
