package com.nguyencse.todo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.nguyencse.todo.objects.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Putin on 5/1/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "dbTodo";

    // Contacts table name
    private static final String TABLE_TASKS = "tasks";

    // Contacts Table Columns names
    private static final String KEY_KEY = "key";
    private static final String KEY_DATE = "date";
    private static final String KEY_TIME = "time";
    private static final String KEY_NAME = "name";
    private static final String KEY_DETAIL = "detail";
    private static final String KEY_STATUS = "status";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TAKS_TABLE = "CREATE TABLE " + TABLE_TASKS + "("
                + KEY_KEY + " TEXT PRIMARY KEY," + KEY_DATE + " TEXT,"
                + KEY_TIME + " INTEGER," + KEY_NAME + " TEXT," + KEY_DETAIL + " TEXT," + KEY_STATUS + " TEXT" + ")";
        db.execSQL(CREATE_TAKS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new task
    public void addTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_KEY, task.getKey());
        values.put(KEY_DATE, task.getDate());
        values.put(KEY_TIME, task.getTime());
        values.put(KEY_NAME, task.getName());
        values.put(KEY_DETAIL, task.getDetail());
        values.put(KEY_STATUS, task.getStatus());

        // Inserting Row
        db.insert(TABLE_TASKS, null, values);
        db.close(); // Closing database connection
    }

    // Getting single task
    public Task getTask(String key) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TASKS, new String[]{KEY_KEY, KEY_DATE, KEY_TIME, KEY_NAME, KEY_DETAIL, KEY_STATUS}, KEY_KEY + "=?",
                new String[]{key}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Task task = new Task(cursor.getString(0), cursor.getString(1), cursor.getLong(2), cursor.getString(3), cursor.getString(4), cursor.getString(5));

        cursor.close();

        // return task
        return task;
    }

    // Getting All Tasks
    public List<Task> getAllTasks() {
        List<Task> taskList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_TASKS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Task task = new Task();
                task.setKey(cursor.getString(0));
                task.setDate(cursor.getString(1));
                task.setTime(cursor.getLong(2));
                task.setName(cursor.getString(3));
                task.setDetail(cursor.getString(4));
                task.setStatus(cursor.getString(5));
                // Adding task to list
                taskList.add(task);
            } while (cursor.moveToNext());
        }

        cursor.close();

        // return task list
        return taskList;
    }

    // Updating single task
    public int updateTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_KEY, task.getKey());
        values.put(KEY_DATE, task.getDate());
        values.put(KEY_TIME, task.getTime());
        values.put(KEY_NAME, task.getName());
        values.put(KEY_DETAIL, task.getDetail());
        values.put(KEY_STATUS, task.getStatus());

        // updating row
        return db.update(TABLE_TASKS, values, KEY_KEY + " = ?",
                new String[]{String.valueOf(task.getKey())});
    }

    // Deleting single task
    public void deleteTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TASKS, KEY_KEY + " = ?",
                new String[]{task.getKey()});
        db.close();
    }

    // Deleting all tasks
    public void deleteAllTasks() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TASKS, null, null);
        db.close();
    }

    // Getting tasks Count
    public int getTasksCount() {
        String countQuery = "SELECT  * FROM " + TABLE_TASKS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }
}