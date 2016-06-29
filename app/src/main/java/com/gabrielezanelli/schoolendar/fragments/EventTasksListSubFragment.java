package com.gabrielezanelli.schoolendar.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.gabrielezanelli.schoolendar.FirebaseUser;
import com.gabrielezanelli.schoolendar.R;
import com.gabrielezanelli.schoolendar.Task;

public class EventTasksListSubFragment extends Fragment {

    // UI References
    private FirebaseRecyclerAdapter<Task, TaskHolder> taskAdapter;
    private RecyclerView recyclerView;
    private LinearLayout footerViewRecycler;
    private EditText newTaskText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View thisFragment = inflater.inflate(R.layout.sub_fragment_event_tasks_list, container, false);

        recyclerView = (RecyclerView) thisFragment.findViewById(R.id.tasks_recycler_view);
        footerViewRecycler = (LinearLayout) thisFragment.findViewById(R.id.recycler_footer);
        newTaskText = (EditText) footerViewRecycler.findViewById(R.id.task_edit_text);
        initTaskList();

        return thisFragment;
    }

    private void initTaskList() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        taskAdapter = new FirebaseRecyclerAdapter<Task, TaskHolder>
                (Task.class, R.layout.item_checkbox_text, TaskHolder.class, FirebaseUser.getTasksRef(EventFragment.getSelectedEventID())) {
            @Override
            protected void populateViewHolder(TaskHolder viewHolder, Task task, int position) {
                viewHolder.taskText.setText(task.getText());
                viewHolder.taskCheckbox.setChecked(task.isCompleted());
            }
        };
        recyclerView.setAdapter(taskAdapter);

        final CheckBox taskCheckbox = (CheckBox)footerViewRecycler.findViewById(R.id.task_checkbox);
        newTaskText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView taskText, int actionId, KeyEvent event) {
                if (event != null) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE
                            || event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                        // If shift key is pressed, the user just want to insert '\n',
                        // otherwise the default action is to submit the text
                        if (!event.isShiftPressed()) {
                            FirebaseUser.addTask(EventFragment.getSelectedEventID(),new Task(taskText.getText().toString(),taskCheckbox.isChecked()));
                            taskText.setText("");
                            taskCheckbox.setChecked(false);
                            taskAdapter.notifyDataSetChanged();
                            return true;
                        }
                    }
                }
                return false;
            }
        });

        // TODO: Fix recycler view not showing any elements at start, this doesn't notify it
        taskAdapter.notifyDataSetChanged();
    }

    static class TaskHolder extends RecyclerView.ViewHolder {
        private EditText taskText;
        private CheckBox taskCheckbox;

        public TaskHolder(View itemView) {
            super(itemView);
            taskText = (EditText) itemView.findViewById(R.id.task_edit_text);
            taskCheckbox = (CheckBox) itemView.findViewById(R.id.task_checkbox);

            taskCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    FirebaseUser.updateTaskComplete(EventFragment.getSelectedEventID(),new Task(taskText.getText().toString(),isChecked));
                }
            });
        }

    }
}
