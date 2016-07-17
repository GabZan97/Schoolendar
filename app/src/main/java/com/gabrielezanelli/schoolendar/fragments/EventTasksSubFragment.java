package com.gabrielezanelli.schoolendar.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gabrielezanelli.schoolendar.StoreManager;
import com.gabrielezanelli.schoolendar.R;
import com.gabrielezanelli.schoolendar.adapters.TaskAdapter;
import com.gabrielezanelli.schoolendar.database.Task;

public class EventTasksSubFragment extends Fragment {

    // UI References
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private LinearLayout footerViewRecycler;
    private EditText newTaskText;
    private CheckBox taskCheckbox;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View thisFragment = inflater.inflate(R.layout.sub_fragment_event_tasks_list, container, false);

        recyclerView = (RecyclerView) thisFragment.findViewById(R.id.tasks_recycler_view);
        footerViewRecycler = (LinearLayout) thisFragment.findViewById(R.id.recycler_footer);
        newTaskText = (EditText) footerViewRecycler.findViewById(R.id.task_edit_text);
        taskCheckbox = (CheckBox) footerViewRecycler.findViewById(R.id.task_checkbox);

        initTaskList();
        initAddTaskListener();

        return thisFragment;
    }


    private void initTaskList() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        taskAdapter = new TaskAdapter(StoreManager.getInstance().getEventTasks(EventFragment.getSelectedEvent()));

        recyclerView.setAdapter(taskAdapter);




    }
    private void initAddTaskListener() {
        newTaskText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView taskText, int actionId, KeyEvent event) {
                if (event != null) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE
                            || event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                        // If shift key is pressed, the user just want to insert '\n',
                        // otherwise the default action is to submit the text
                        if (!event.isShiftPressed()) {
                            taskAdapter.addTask(new Task(EventFragment.getSelectedEvent(),
                                    taskText.getText().toString(), taskCheckbox.isChecked()));

                            // Clear fields
                            taskText.setText("");
                            taskCheckbox.setChecked(false);
                            return true;
                        }
                    }
                }
                return false;
            }
        });
    }
}
