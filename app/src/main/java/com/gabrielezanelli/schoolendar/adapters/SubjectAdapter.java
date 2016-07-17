package com.gabrielezanelli.schoolendar.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.gabrielezanelli.schoolendar.R;
import com.gabrielezanelli.schoolendar.StoreManager;
import com.gabrielezanelli.schoolendar.database.Subject;

import java.util.ArrayList;
import java.util.List;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.SubjectHolder> {

    private List<Subject> subjects;

    public SubjectAdapter(List<Subject> subjects) {
        this.subjects = new ArrayList<>(subjects);
    }

    @Override
    public SubjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View holderView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_subject, parent, false);
        return new SubjectHolder(holderView,this);
    }

    @Override
    public void onBindViewHolder(SubjectHolder holder, int position) {
        holder.updateView(subjects.get(position));
    }

    @Override
    public int getItemCount() {
        return subjects.size();
    }

    public void addSubject(Subject subject) {
        StoreManager.getInstance().addSubject(subject);
        subjects.add(subject);
        notifyDataSetChanged();
    }

    public void deleteSubject(Subject subject) {
        StoreManager.getInstance().deleteSubject(subject);
        subjects.remove(subject);
        notifyDataSetChanged();

    }

    public void updateSubject(Subject subject){
        StoreManager.getInstance().updateSubject(subject);
        notifyDataSetChanged();
    }


    protected static class SubjectHolder extends RecyclerView.ViewHolder {
        private Subject currentSubject;
        private final TextView subjectText;
        private final ImageView removeButton;
        private final GradientDrawable leftBorder;

        public SubjectHolder(View itemView, final SubjectAdapter subjectAdapter) {
            super(itemView);

            currentSubject = new Subject();
            subjectText = (TextView) itemView.findViewById(R.id.subject_name_text);
            removeButton = (ImageView) itemView.findViewById(R.id.remove_subject_button);

            leftBorder = (GradientDrawable) ((LayerDrawable) itemView.getBackground())
                    .findDrawableByLayerId(R.id.left_coloured_border);
            leftBorder.setColor(itemView.getResources().getColor(R.color.blue));

            // Add the Remove Button Listener
            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();

                    // Setup the alert dialog and its title
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(context.getString(R.string.message_delete_subject));
                    builder.setMessage(context.getString(R.string.message_delete_subject_confirm));

                    builder.setPositiveButton(context.getString(R.string.action_confirm), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            subjectAdapter.deleteSubject(currentSubject);
                        }
                    });
                    builder.setNegativeButton(context.getString(R.string.action_cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });

            // Add the Modify TextView Listener
            subjectText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();

                    // Setup the alert dialog and its title
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(context.getString(R.string.message_edit_subject));

                    // Set up the Input Field
                    View view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_subject, null);
                    final EditText input = (EditText) view.findViewById(R.id.edit_subject_name);

                    // Set the currentSubject name as text
                    input.setText(subjectText.getText().toString());

                    builder.setView(view);

                    // Set up the buttons
                    builder.setPositiveButton(context.getString(R.string.action_confirm), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Edit the currentSubject in Firebase Database
                            currentSubject.setName(input.getText().toString());
                            subjectAdapter.updateSubject(currentSubject);
                        }
                    });
                    builder.setNegativeButton(context.getString(R.string.action_cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
        }

        public void updateView(Subject updatingSubject) {
            currentSubject = updatingSubject;
            subjectText.setText(updatingSubject.getName());
        }
    }

}
