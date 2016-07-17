package com.gabrielezanelli.schoolendar.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gabrielezanelli.schoolendar.database.Subject;

import java.util.List;

public class SubjectSpinnerAdapter extends ArrayAdapter {
    private List<Subject> subjects;

    public SubjectSpinnerAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
        subjects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) super.getView(position, convertView, parent);
        view.setText(subjects.get(position).getName());
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) super.getDropDownView(position, convertView, parent);
        view.setText(subjects.get(position).getName());
        return view;
    }


    public Subject getSubjectByPosition(int position) {
        return subjects.get(position);
    }
}
