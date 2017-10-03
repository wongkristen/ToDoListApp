package com.kristenwong.todolist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by kristenwong on 9/26/17.
 */

public class ToDoListAdapter extends BaseAdapter {

    private ArrayList<TaskItem> taskList;
    private LayoutInflater mInflater;

    public ToDoListAdapter (ArrayList<TaskItem> tasks, Context context) {
        taskList = tasks;
        mInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return taskList.size();
    }

    @Override
    public TaskItem getItem(int i) {
        return taskList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TextView taskNumber, taskTitle, taskDescription;
        TaskItem task = getItem(i);

        View v = mInflater.inflate(R.layout.list_item, viewGroup, false);

        taskNumber = (TextView) v.findViewById(R.id.textview_task_number);
        taskTitle = (TextView) v.findViewById(R.id.textview_list_task_title);
        taskDescription = (TextView) v.findViewById(R.id.textview_list_task_description);

        taskNumber.setText(Integer.toString(i + 1));
        taskTitle.setText(task.getTaskTitle());
        taskDescription.setText(task.getTaskDescription());

        return v;
    }

    public void updateList (ArrayList<TaskItem> tasks) {
        taskList = tasks;
        notifyDataSetChanged();
    }
}
