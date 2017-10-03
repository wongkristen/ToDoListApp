package com.kristenwong.todolist;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kristenwong on 9/26/17.
 */

class TaskItem implements Parcelable {
    private String taskTitle;

    protected TaskItem(Parcel in) {
        taskTitle = in.readString();
        taskDescription = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(taskTitle);
        dest.writeString(taskDescription);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TaskItem> CREATOR = new Creator<TaskItem>() {
        @Override
        public TaskItem createFromParcel(Parcel in) {
            return new TaskItem(in);
        }

        @Override
        public TaskItem[] newArray(int size) {
            return new TaskItem[size];
        }
    };

    String getTaskTitle() {
        return taskTitle;
    }

    String getTaskDescription() {
        return taskDescription;
    }

    private String taskDescription;

    TaskItem (String title, String description) {
        taskTitle = title;
        taskDescription = description;
    }
}
