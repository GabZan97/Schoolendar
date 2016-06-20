package com.gabrielezanelli.schoolendar;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gabrielezanelli.schoolendar.activities.MainActivity;
import com.gabrielezanelli.schoolendar.fragments.FragmentEvent;

import org.apache.commons.lang.WordUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventHolder> {
    private final List<Event> allEvents;

    public EventAdapter(List<Event> allEvents) {
        this.allEvents = new ArrayList<>(allEvents);
    }

    // When the View Holder is created inflates the layout
    @Override
    public EventHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View holderView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_event, parent, false);
        return new EventHolder(holderView);
    }

    // When the View Holder get scrolled or updated, updates the view holder's content
    @Override
    public void onBindViewHolder(EventHolder eventHolder, int position) {
        eventHolder.update(allEvents.get(position));
    }

    @Override
    public int getItemCount() {
        return allEvents.size();
    }

    public void animateTo(List<Event> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<Event> newModels) {
        for (int i = allEvents.size() - 1; i >= 0; i--) {
            final Event model = allEvents.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<Event> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final Event model = newModels.get(i);
            if (!allEvents.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<Event> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final Event model = newModels.get(toPosition);
            final int fromPosition = allEvents.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public Event removeItem(int position) {
        final Event model = allEvents.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, Event model) {
        allEvents.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final Event model = allEvents.remove(fromPosition);
        allEvents.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }


    static class EventHolder extends RecyclerView.ViewHolder {
        private TextView titleText;
        private TextView dateText;
        private TextView typeSubjectText;
        private String dateFormat;

        private EventHolder(View itemView) {
            super(itemView);
            titleText = (TextView) itemView.findViewById(R.id.event_card_title);
            dateText = (TextView) itemView.findViewById(R.id.event_card_date);
            dateFormat = itemView.getResources().getString(R.string.event_date_format);
            typeSubjectText = (TextView) itemView.findViewById(R.id.event_card_type_sujbject);
        }

        private void update(final Event updatingEvent) {
            titleText.setText(updatingEvent.getTitle());

            String date = WordUtils.capitalize(new SimpleDateFormat(dateFormat).format(updatingEvent.getDate()));
            dateText.setText(date);

            typeSubjectText.setText(updatingEvent.getTypePlusSubject());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentEvent event = new FragmentEvent();
                    Bundle extras = new Bundle();
                    extras.putLong(v.getContext().getString(R.string.EXTRA_LONG_EVENT_ID),updatingEvent.getId());
                    event.setArguments(extras);
                    Log.d("All Events", "Opening event with ID: "+updatingEvent.getId());
                    ((MainActivity)v.getContext()).fragmentTransaction(event,true,-666);
                }
            });


        }
    }
}
