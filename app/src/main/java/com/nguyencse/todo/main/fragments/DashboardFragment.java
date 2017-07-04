package com.nguyencse.todo.main.fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.nguyencse.todo.custom.ProgressDialogCSE;
import com.nguyencse.todo.custom.TextViewCSE;
import com.nguyencse.todo.objects.Task;
import com.nguyencse.todo.main.MainActivity;
import com.nguyencse.todo.R;
import com.nguyencse.todo.adapter.TaskAdapter;
import com.nguyencse.todo.general.CommonField;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Putin on 4/12/2017.
 */

public class DashboardFragment extends Fragment implements View.OnClickListener {
    private DatabaseReference database;
    private List<Task> allTaskList;
    private TaskAdapter adapterAllTask;
    private RecyclerView rcvAllTasksToDo;
    private FloatingActionButton fabAddNewTask;
    private CompactCalendarView compactCalendarView;
    private TextViewCSE txtCurrentDate;
    private TextViewCSE txtNumberOfTasks;
    private LinearLayout lnlNoTask;
    private Dialog dialogTasksToday;
    private ProgressDialogCSE progressDialogCSE;

    private List<Task> taskList;
    private TaskAdapter adapterTaskList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        compactCalendarView = (CompactCalendarView) view.findViewById(R.id.compactcalendar_view);
        txtCurrentDate = (TextViewCSE) view.findViewById(R.id.txt_current_month_year);
        txtNumberOfTasks = (TextViewCSE) view.findViewById(R.id.txt_number_of_tasks);
        lnlNoTask = (LinearLayout) view.findViewById(R.id.lnl_no_task);
        rcvAllTasksToDo = (RecyclerView) view.findViewById(R.id.rcv_all_tasks_to_do);
        fabAddNewTask = (FloatingActionButton) view.findViewById(R.id.fab_add_new_task);

        dialogTasksToday = new Dialog(getContext());
        dialogTasksToday.setContentView(R.layout.custom_layout_modal_event_on_date_selected);

        database = FirebaseDatabase.getInstance().getReference();
        progressDialogCSE = new ProgressDialogCSE(getContext(), R.style.CircleLoading);

        allTaskList = new ArrayList<>();
        adapterAllTask = new TaskAdapter(getContext(), allTaskList);
        LinearLayoutManager lnlAllTasksToDo = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }

            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        lnlAllTasksToDo.setStackFromEnd(false);
        rcvAllTasksToDo.setLayoutManager(lnlAllTasksToDo);
        rcvAllTasksToDo.setAdapter(adapterAllTask);

        taskList = new ArrayList<>();
        adapterTaskList = new TaskAdapter(getContext(), taskList);

        getAllTasksToDo();

        compactCalendarView.setFirstDayOfWeek(Calendar.SUNDAY);
        compactCalendarView.setUseThreeLetterAbbreviation(true);
        txtCurrentDate.setText(DateFormat.format("MM-yyyy", compactCalendarView.getFirstDayOfCurrentMonth()).toString());

        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {

                // TODO: Get tasks on date and put on TaskAdapter
                taskList.clear();
                List<Task> taskListOnDateSelected = getAllTaskOnDate(dateClicked);
                for (Task task : taskListOnDateSelected) {
                    taskList.add(task);
                }

                RecyclerView rcvTaskOnDateSelected = (RecyclerView) dialogTasksToday.findViewById(R.id.rcv_task_on_date_selected);
                LinearLayoutManager lnlTasksOnDate = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                lnlTasksOnDate.setStackFromEnd(false);
                rcvTaskOnDateSelected.setLayoutManager(lnlTasksOnDate);
                rcvTaskOnDateSelected.setAdapter(adapterTaskList);
                adapterTaskList.notifyDataSetChanged();

                TextViewCSE txtDateSelected = (TextViewCSE) dialogTasksToday.findViewById(R.id.txt_item_modal_task_today_time);
                txtDateSelected.setText(DateFormat.format("dd-MM-yyyy", dateClicked));

                if (taskListOnDateSelected.size() > 0) {
                    dialogTasksToday.show();
                }
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                txtCurrentDate.setText(DateFormat.format("MM-yyyy", firstDayOfNewMonth).toString());
            }
        });


        fabAddNewTask.setOnClickListener(this);

        return view;
    }

    private List<Task> getAllTaskOnDate(Date date) {
        List<Task> taskList = new ArrayList<>();
        List<Event> eventList = compactCalendarView.getEvents(date);

        int count = eventList.size();
        for (int i = 0; i < count; i++) {
            taskList.add(new Gson().fromJson(eventList.get(i).getData().toString(), Task.class));
        }

        return taskList;
    }

    private void getAllTasksToDo() {
        progressDialogCSE.show();

        database.child("tasks").child(MainActivity.user.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (progressDialogCSE.isShowing()) {
                    progressDialogCSE.dismiss();
                }
                Task task = dataSnapshot.getValue(Task.class);
                allTaskList.add(task);
                adapterAllTask.notifyDataSetChanged();
                txtNumberOfTasks.setText(allTaskList.size() + "");

                Event event = new Event(Color.RED, task.getTime(), new Gson().toJson(task));
                compactCalendarView.addEvent(event);

                refreshCalender();
                updateUIAllTaskToShow(allTaskList.size());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Task taskDeleted = dataSnapshot.getValue(Task.class);

                for (int i = 0; i < allTaskList.size(); i++) {
                    if (allTaskList.get(i).getKey().equals(taskDeleted.getKey())) {
                        allTaskList.remove(i);
                        break;
                    }
                }

                for (int i = 0; i < taskList.size(); i++) {
                    if (taskList.get(i).getKey().equals(taskDeleted.getKey())) {
                        taskList.remove(i);
                        break;
                    }
                }

                MainActivity.dbTodo.deleteTask(taskDeleted);
                refreshCalender();
                adapterAllTask.notifyDataSetChanged();
                adapterTaskList.notifyDataSetChanged();
                txtNumberOfTasks.setText(allTaskList.size() + "");

                if (taskList.size() == 0 && dialogTasksToday.isShowing()) {
                    dialogTasksToday.dismiss();
                }

                updateUIAllTaskToShow(allTaskList.size());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.fab_add_new_task:
                MainActivity.vpMain.setCurrentItem(CommonField.FRAGMENT_ADD_TASK_POSTION);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        refreshCalender();
    }

    private void refreshCalender() {
        compactCalendarView.removeAllEvents();

        //  List<Task> taskList = MainActivity.dbTodo.getAllTasks();
        int n = allTaskList.size();
        for (int i = 0; i < n; i++) {
            Task task = allTaskList.get(i);
            Event event = new Event(Color.RED, task.getTime(), new Gson().toJson(task));
            compactCalendarView.addEvent(event);
        }
    }

    private void updateUIAllTaskToShow(int count) {
        if (count == 0) {
            lnlNoTask.setVisibility(View.VISIBLE);
            rcvAllTasksToDo.setVisibility(View.GONE);
        } else {
            lnlNoTask.setVisibility(View.GONE);
            rcvAllTasksToDo.setVisibility(View.VISIBLE);
        }
    }
}
