package com.gabrielezanelli.schoolendar.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.gabrielezanelli.schoolendar.StoreManager;
import com.gabrielezanelli.schoolendar.R;
import com.gabrielezanelli.schoolendar.adapters.SubjectAdapter;
import com.gabrielezanelli.schoolendar.database.Subject;

public class ManageSubjectsFragment extends Fragment implements View.OnClickListener {

    private SubjectAdapter subjectAdapter;
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
        switch (v.getId()) {

            // Add a new subject on Fab click
            case (R.id.add_event_fab):
                String subjectName = newSubjectEdit.getText().toString();

                // If the subject field is empty sets an error
                if (subjectName.equals("")) {
                    newSubjectEdit.setError(getResources().getString((R.string.error_insert_subject_name)));
                    break;
                }

                subjectAdapter.addSubject(new Subject(subjectName));

                // Reset the input field
                newSubjectEdit.setText("");

                // Close the Keyboard
                View focussedView = getActivity().getCurrentFocus();
                if (focussedView != null) {
                    InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                break;
        }

    }

    private void initSubjectsList() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        subjectAdapter = new SubjectAdapter(StoreManager.getInstance().getSubjects());
        recyclerView.setAdapter(subjectAdapter);
    }

}


