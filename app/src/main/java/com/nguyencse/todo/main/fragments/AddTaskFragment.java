package com.nguyencse.todo.main.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nguyencse.todo.custom.EditTextCSE;
import com.nguyencse.todo.custom.TextViewCSE;
import com.nguyencse.todo.general.CommonField;
import com.nguyencse.todo.main.MainActivity;
import com.nguyencse.todo.objects.Task;
import com.nguyencse.todo.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Putin on 4/12/2017.
 */

public class AddTaskFragment extends Fragment implements View.OnClickListener, TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {
    private EditTextCSE edtTaskName, edtTaskDetail;
    private LinearLayout lnlTaskDate, lnlTaskTime;
    private TextViewCSE txtTaskDate, txtTaskTime;
    private FloatingActionButton btnUploadToCloud, btnCancelUpload;
    private DatabaseReference database;
    private int year = -1, monthOfYear = -1, dayOfMonth = -1, hourOfDay = -1, minute = -1;

    private long time = Calendar.getInstance().getTime().getTime();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_task, container, false);
        database = FirebaseDatabase.getInstance().getReference();

        edtTaskName = (EditTextCSE) view.findViewById(R.id.edt_adding_task_name);
        edtTaskDetail = (EditTextCSE) view.findViewById(R.id.edt_adding_task_detail);

        lnlTaskDate = (LinearLayout) view.findViewById(R.id.lnl_adding_task_date);
        lnlTaskTime = (LinearLayout) view.findViewById(R.id.lnl_adding_task_time);

        txtTaskDate = (TextViewCSE) view.findViewById(R.id.txt_adding_task_date);
        txtTaskTime = (TextViewCSE) view.findViewById(R.id.txt_adding_task_time);

        btnUploadToCloud = (FloatingActionButton) view.findViewById(R.id.btn_upload_to_cloud);
        btnCancelUpload = (FloatingActionButton) view.findViewById(R.id.btn_cancel_upload_to_cloud);

//        SharedPreferences sprfToDo = getContext().getSharedPreferences(CommonField.SHAREDPREFERENCES, Context.MODE_PRIVATE);
//        if (sprfToDo.getString(CommonField.TASK, null) != null) {
//            Log.e("CSE_TASK", sprfToDo.getString(CommonField.TASK, ""));
//        }

        lnlTaskDate.setOnClickListener(this);
        lnlTaskTime.setOnClickListener(this);
        btnUploadToCloud.setOnClickListener(this);
        btnCancelUpload.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setThemeDark(false);
        dpd.vibrate(true);
        dpd.dismissOnPause(true);
        dpd.setVersion(DatePickerDialog.Version.VERSION_2);
        dpd.setAccentColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
        dpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Log.d("TimePicker", "Dialog was cancelled");
            }
        });

        TimePickerDialog tpd = TimePickerDialog.newInstance(
                this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true
        );
        tpd.setThemeDark(false);
        tpd.vibrate(true);
        tpd.dismissOnPause(true);
        tpd.enableSeconds(false);
        tpd.setVersion(TimePickerDialog.Version.VERSION_2);
        tpd.setAccentColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
        tpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Log.d("TimePicker", "Dialog was cancelled");
            }
        });

        switch (id) {
            case R.id.lnl_adding_task_date:
                dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
                break;
            case R.id.lnl_adding_task_time:
                tpd.show(getActivity().getFragmentManager(), "TimepickerdialogBegin");
                break;
            case R.id.btn_upload_to_cloud:
                if (txtTaskDate.getText().equals("") || txtTaskDate.getText() == null) {
                    Toast.makeText(getContext(), getString(R.string.please_chose_a_date), Toast.LENGTH_SHORT).show();
                } else if (txtTaskTime.getText().equals("") || txtTaskTime.getText() == null) {
                    Toast.makeText(getContext(), getString(R.string.please_chose_a_time), Toast.LENGTH_SHORT).show();
                } else {
                    uploadTask();
                    backToDashboard();
                    resetInfo();
                }
                break;
            case R.id.btn_cancel_upload_to_cloud:
                resetInfo();
                break;
        }
    }

    private void uploadTask() {
        String taskName = edtTaskName.getText().toString();
        String taskDetail = edtTaskDetail.getText().toString();
        String taskDate = txtTaskDate.getText().toString();

        if (taskName.isEmpty()) {
            taskName = getString(R.string.no_title);
        }
        if (taskName.length() > CommonField.TASK_NAME_MAXIMUM_CHARACTER) {
            Toast.makeText(getContext(), getString(R.string.task_detail_too_long), Toast.LENGTH_SHORT).show();
        } else {
            if (taskDetail.isEmpty()) {
                Toast.makeText(getContext(), getString(R.string.task_detail_must_not_be_empty), Toast.LENGTH_SHORT).show();
            } else {
                String key = database.child("tasks").child(MainActivity.user.getUid()).push().getKey();

                setDateTimeFromInput();

                Task newTask = new Task(key, taskDate, this.time, taskName, taskDetail, getString(R.string.pending));
                if (this.time < Calendar.getInstance().getTime().getTime()) {
                    newTask.setStatus(getString(R.string.happened));
                }
                Map<String, Object> newTaskMap = newTask.toMap();
                Map<String, Object> childUpdate = new HashMap<>();
                childUpdate.put("tasks/" + MainActivity.user.getUid() + "/" + key, newTaskMap);
                database.updateChildren(childUpdate);

//                Log.e("CSE_TASK_INSERT", "Inserting ..");
                MainActivity.dbTodo.addTask(newTask);
            }
        }
    }

    private void resetInfo() {
        edtTaskName.setText("");
        edtTaskDetail.setText("");
        txtTaskDate.setText("");
        txtTaskTime.setText("");
    }

    private void backToDashboard() {
        MainActivity.vpMain.setCurrentItem(CommonField.FRAGMENT_DASHBOARD_POSTION);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String day = dayOfMonth < 10 ? "0" + dayOfMonth : "" + dayOfMonth;
        String month = monthOfYear + 1 < 10 ? "0" + (monthOfYear + 1) : "" + (monthOfYear + 1);
        String date = day + "-" + month + "-" + year;
        txtTaskDate.setText(date);

        this.year = year;
        this.monthOfYear = monthOfYear;
        this.dayOfMonth = dayOfMonth;
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        this.hourOfDay = hourOfDay;
        this.minute = minute;

        String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
        String minuteString = minute < 10 ? "0" + minute : "" + minute;
        String timeDisplay = hourString + ":" + minuteString;

        txtTaskTime.setText(timeDisplay);
    }

    private void setDateTimeFromInput() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(this.year, this.monthOfYear, this.dayOfMonth, this.hourOfDay, this.minute, 0);
        this.time = calendar.getTime().getTime();
    }
}
