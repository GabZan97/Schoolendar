package com.gabrielezanelli.schoolendar.adapters;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.gabrielezanelli.schoolendar.R;
import com.gabrielezanelli.schoolendar.StoreManager;
import com.gabrielezanelli.schoolendar.database.Subject;
import com.gabrielezanelli.schoolendar.database.Task;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskHolder> {

    private List<Task> tasks;

    public TaskAdapter(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    @Override
    public TaskHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View holderView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskHolder(holderView,this);
    }

    @Override
    public void onBindViewHolder(TaskHolder holder, int position) {
        holder.updateView(tasks.get(position));
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public void addTask(Task task) {
        StoreManager.getInstance().addTask(task);
        tasks.add(task);
        notifyDataSetChanged();
    }

    public void deleteTask(Task task) {
        StoreManager.getInstance().deleteTask(task);
        tasks.remove(task);
        notifyDataSetChanged();

    }

    public void updateTask(Task task){
        StoreManager.getInstance().updateTask(task);
        notifyDataSetChanged();
    }


    protected static class TaskHolder extends RecyclerView.ViewHolder {
        private Task currentTask;
        private EditText taskText;
        private CheckBox taskCheckbox;
        private FloatingActionButton deleteTaskButton;

        public TaskHolder(View itemView,final TaskAdapter taskAdapter) {
            super(itemView);
            taskText = (EditText) itemView.findViewById(R.id.task_edit_text);
            taskCheckbox = (CheckBox) itemView.findViewById(R.id.task_checkbox);


            taskCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    currentTask.setCompleted(isChecked);
                    taskAdapter.updateTask(currentTask);
                }
            });


            deleteTaskButton = (FloatingActionButton) itemView.findViewById(R.id.delete_task_fab);
            deleteTaskButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    taskAdapter.deleteTask(currentTask);
                }
            });

            taskText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus){
                        deleteTaskButton.setVisibility(View.VISIBLE);
                    }else {
                        deleteTaskButton.setVisibility(View.GONE);
                        currentTask.setText(taskText.getText().toString());
                        taskAdapter.updateTask(currentTask);
                    }
                }
            });

        }

        public void updateView(Task updatingTask) {
            currentTask = updatingTask;
            taskText.setText(updatingTask.getText());
            taskCheckbox.setChecked(updatingTask.isCompleted());
        }
    }
}
