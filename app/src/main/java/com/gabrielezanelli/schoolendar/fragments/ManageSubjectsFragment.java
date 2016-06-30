package com.gabrielezanelli.schoolendar.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.gabrielezanelli.schoolendar.FirebaseUser;
import com.gabrielezanelli.schoolendar.R;

public class ManageSubjectsFragment extends Fragment implements View.OnClickListener {

    private FirebaseRecyclerAdapter<String, SubjectHolder> subjectAdapter;
    private EditText newSubjectEdit;
    private RecyclerView recyclerView;
    private FloatingActionButton addButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View thisFragment = inflater.inflate(R.layout.fragment_manage_subjects, container, false);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getActivity().setTitle(getString(R.string.fragment_title_manage_subjects));

        recyclerView = (RecyclerView) thisFragment.findViewById(R.id.subject_recycler_view);
        newSubjectEdit = (EditText) thisFragment.findViewById(R.id.new_subject_text);
        addButton = (FloatingActionButton) thisFragment.findViewById(R.id.add_event_fab);
        addButton.setOnClickListener(this);

        initSubjectsList();

        // Inflate the layout for this fragment
        return thisFragment;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){

            // Add a new subject on Fab click
            case(R.id.add_event_fab):
                String newSubject = newSubjectEdit.getText().toString();

                // If the subject field is empty sets an error
                if (newSubject.equals(""))
                    newSubjectEdit.setError(getResources().getString((R.string.error_insert_subject_name)));
                else {
                    // Saves the subject in Firebase Database
                    FirebaseUser.addSubject(newSubject);

                    // Reset the input field
                    newSubjectEdit.setText("");

                    // Close the Keyboard
                    View focussedView = getActivity().getCurrentFocus();
                    if (focussedView != null) {
                        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
                break;
        }
    }

    private void initSubjectsList() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        subjectAdapter = new FirebaseRecyclerAdapter<String, SubjectHolder>
                (String.class, R.layout.card_subject, SubjectHolder.class, FirebaseUser.getSubjectsRef()) {
            @Override
            protected void populateViewHolder(SubjectHolder viewHolder, String subject, int position) {
                viewHolder.subjectText.setText(subject);
                viewHolder.leftBorder.setColor(getResources().getColor(R.color.blue));
            }
        };
        recyclerView.setAdapter(subjectAdapter);
    }

    // Subject view holder for Firebase Recycler View
    private static class SubjectHolder extends RecyclerView.ViewHolder {
        final TextView subjectText;
        final ImageView removeButton;
        final GradientDrawable leftBorder = (GradientDrawable) ((LayerDrawable)itemView.getBackground())
                .findDrawableByLayerId(R.id.left_coloured_border);

        public SubjectHolder(View itemView) {
            super(itemView);

            subjectText = (TextView) itemView.findViewById(R.id.subject_name_text);
            removeButton = (ImageView) itemView.findViewById(R.id.remove_subject_button);

            // Add the Remove Button Listener
            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseUser.removeSubject(subjectText.getText().toString());
                }
            });

            // Add the Modify TextView Listener
            subjectText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();

                    final String oldSubjectName = subjectText.getText().toString();

                    // Setup the Dialog and its Title
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(context.getString(R.string.message_edit_subject));

                    // Set up the Input Field
                    View view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_subject, null);
                    final EditText input = (EditText)view.findViewById(R.id.edit_subject_name);

                    // Set the subject name as text
                    input.setText(oldSubjectName);

                    builder.setView(view);

                    // Set up the buttons
                    builder.setPositiveButton(context.getString(R.string.action_confirm), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Edit the subject in Firebase Database
                            final String newSubjectName = input.getText().toString();
                            FirebaseUser.updateSubject(oldSubjectName,newSubjectName);
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
    }
}


