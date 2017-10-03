package com.kristenwong.todolist;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class MainActivity extends Activity {

    private EditText mInputTaskTitle, mInputTaskDescription;
    private Button mAddTaskButton;
    private ListView mTaskList;
    private String stringTaskTitle, stringTaskDescription;
    private ArrayList<TaskItem> taskList = new ArrayList<>();
    private ToDoListAdapter adapter;
    private File mTextFile;
    private static final String FILE_STREAM_IO = "file stream IO tag";
    private static final String MAIN_DEBUG_TAG = "MainActivity";
    private static final String KEY = "key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(MAIN_DEBUG_TAG, "OnCreate called.");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInputTaskTitle = (EditText) findViewById(R.id.input_task_title);
        mInputTaskDescription = (EditText) findViewById(R.id.input_task_description);
        mAddTaskButton = (Button) findViewById(R.id.button_add_task);
        mTaskList = (ListView) findViewById(R.id.listview_todolist);

        adapter = new ToDoListAdapter(taskList, getApplicationContext());
        mTaskList.setAdapter(adapter);

        if (savedInstanceState != null) {
            taskList = savedInstanceState.getParcelableArrayList(KEY);
            adapter.updateList(taskList);

            Log.d(MAIN_DEBUG_TAG, "List successfully updated from last save.");
        }


        try {
            createTextFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mInputTaskTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                stringTaskTitle = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mInputTaskDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                stringTaskDescription = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mAddTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                UPDATE LIST
                taskList.add(new TaskItem(stringTaskTitle, stringTaskDescription));
                adapter.updateList(taskList);

//                CLEAR TEXT FIELDS
                mInputTaskTitle.setText("");
                mInputTaskDescription.setText("");

//                WRITE CONTENTS OF LIST TO TEXT FILE
                updateTextFile();

                readTextFile();
                adapter.updateList(taskList);

            }
        });
        createListViewListener();

    }

    private void createListViewListener() {
//        ON LONG CLICK: delete item from list
        mTaskList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                taskList.remove(i);
                adapter.updateList(taskList);
                updateTextFile();
                return true;
            }
        });
    }

    private boolean createTextFile() throws IOException {

        File fileDir = new File(Environment.getExternalStorageDirectory(), "ToDoListDir");
        mTextFile = new File(fileDir, "to_do_list.txt");

        boolean isFileCreated = mTextFile.mkdirs();

        if (!isFileCreated) Log.d(MAIN_DEBUG_TAG, "Error in creating new file path.");

        Log.d(MAIN_DEBUG_TAG, "External storage state: " + Environment.getExternalStorageState());
        Log.d(MAIN_DEBUG_TAG, "File exists after call to createTextFile? " + mTextFile.exists());

        return isFileCreated;
    }

    void updateTextFile(){
        if (taskList.size() == 0) return;

        try {
            FileOutputStream outputStream = openFileOutput(mTextFile.getName(), Context.MODE_PRIVATE);
            OutputStreamWriter writer = new OutputStreamWriter(outputStream);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);

            for (TaskItem task: taskList){

                bufferedWriter.write(task.getTaskTitle());
                bufferedWriter.newLine();
                bufferedWriter.write(task.getTaskDescription());
                bufferedWriter.newLine();

                Log.d(MAIN_DEBUG_TAG, "Task list text file successfully updated on click.");
                Log.d(MAIN_DEBUG_TAG, "TASK WRITTEN BY WRITER: " + task.getTaskTitle() + ": " + task.getTaskDescription());
            }
            bufferedWriter.close();
            outputStream.flush();
            outputStream.close();

            Log.d(FILE_STREAM_IO, "Text file successfully updated.");
        } catch (FileNotFoundException e) {
            Log.d(FILE_STREAM_IO, "Text file could not be updated: File not found exception.");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(FILE_STREAM_IO, "Text file could not be updated: IO found exception.");
        }
    }

    void readTextFile(){
        if(taskList.size() != 0) return;

        String title, description;
        try {
            FileInputStream fileInputStream = openFileInput(mTextFile.getName());

            if (fileInputStream != null){
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                do {
                    title = bufferedReader.readLine();
                    description = bufferedReader.readLine();

                    if(title != null && description != null) {
                        taskList.add(new TaskItem(title, description));
                        Log.d(MAIN_DEBUG_TAG, "TASK READ FROM READER: " + title + ": " + description);
                    }
                } while (title != null);

                Log.d(MAIN_DEBUG_TAG, "Text file has been read.");
                fileInputStream.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(MAIN_DEBUG_TAG, "Text file could not be read.");
        }


    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelableArrayList(KEY, taskList);

        Log.d(MAIN_DEBUG_TAG, "OnSaveInstanceState called.");
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        taskList = savedInstanceState.getParcelableArrayList(KEY);
        readTextFile();
        adapter.updateList(taskList);

        Log.d(MAIN_DEBUG_TAG, "OnRestoreInstanceState called - List successfully updated from last save.");
    }

//    ACTIVITY STATE CHANGE METHODS
    @Override
    public void onStart(){
        super.onStart();
        adapter.updateList(taskList);
        Log.d(MAIN_DEBUG_TAG, "OnStart called.");
    }

    @Override
    public void onResume(){
        super.onResume();
        readTextFile();
        adapter.updateList(taskList);
        Log.d(MAIN_DEBUG_TAG, "OnResume called.");
    }

    @Override
    public void onPause(){
        super.onPause();
        updateTextFile();
        Log.d(MAIN_DEBUG_TAG, "OnPause called.");
    }

    @Override
    public void onStop(){
        super.onStop();
        updateTextFile();
        Log.d(MAIN_DEBUG_TAG, "OnStop called.");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        updateTextFile();
        Log.d(MAIN_DEBUG_TAG, "OnDestroy called.");
    }
}
