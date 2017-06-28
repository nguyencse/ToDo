package com.nguyencse.todo.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.nguyencse.todo.custom.TextViewCSE;
import com.nguyencse.todo.main.MainActivity;
import com.nguyencse.todo.objects.Task;
import com.nguyencse.todo.R;
import com.nguyencse.todo.general.CommonField;
import com.nguyencse.todo.general.CommonMethod;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Putin on 4/13/2017.
 */

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
    private Context context;
    private List<Task> taskList;
    private SharedPreferences sprfToDo;

    public TaskAdapter(Context context, List<Task> taskList) {
        this.context = context;
        this.taskList = taskList;
        sprfToDo = context.getSharedPreferences(CommonField.SHAREDPREFERENCES, Context.MODE_PRIVATE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_item_task, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Task task = taskList.get(position);
        holder.txtTaskDate.setText(task.getDate());
        holder.txtTaskTime.setText(DateFormat.format("HH:mm", task.getTime()).toString());
        holder.txtTaskName.setText(task.getName());
        holder.txtTaskDetail.setText(task.getDetail());

        holder.txtTaskStatus.setText(task.getStatus());
        if (task.getTime() < Calendar.getInstance().getTime().getTime()) {
            holder.txtTaskStatus.setText(context.getString(R.string.happened));
            holder.txtTaskStatus.setTextColor(ContextCompat.getColor(context, android.R.color.holo_purple));
        } else {
            holder.txtTaskStatus.setText(context.getString(R.string.pending));
            holder.txtTaskStatus.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        }

        holder.lnlItemTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // custom dialog
                Dialog dialogTaskList = new Dialog(context);
                dialogTaskList.setContentView(R.layout.custom_layout_modal_task);

                // set the custom dialog components
                TextViewCSE name = (TextViewCSE) dialogTaskList.findViewById(R.id.txt_item_modal_task_name);
                TextViewCSE detail = (TextViewCSE) dialogTaskList.findViewById(R.id.txt_item_modal_task_detail);
                TextViewCSE date = (TextViewCSE) dialogTaskList.findViewById(R.id.txt_item_modal_task_date);
                TextViewCSE time = (TextViewCSE) dialogTaskList.findViewById(R.id.txt_item_modal_task_time);
                TextViewCSE status = (TextViewCSE) dialogTaskList.findViewById(R.id.txt_item_modal_task_status);

                name.setText(task.getName());
                detail.setText(task.getDetail());
                date.setText(task.getDate());
                time.setText(DateFormat.format("HH:mm", task.getTime()).toString());
                status.setText(task.getStatus());

                if (task.getTime() < Calendar.getInstance().getTime().getTime()) {
                    status.setText(context.getString(R.string.happened));
                    status.setTextColor(ContextCompat.getColor(context, android.R.color.holo_purple));
                }

                dialogTaskList.show();
            }
        });
        holder.btnOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // custom dialog
                final Dialog dialogOptions = new Dialog(context);
                dialogOptions.setContentView(R.layout.custom_layout_modal_task_options);

                CommonMethod.setPositionDialog(dialogOptions, Gravity.BOTTOM);

                TextViewCSE txtEdit = (TextViewCSE) dialogOptions.findViewById(R.id.txt_item_modal_task_edit);
                TextViewCSE txtDelete = (TextViewCSE) dialogOptions.findViewById(R.id.txt_item_modal_task_delete);
                TextViewCSE txtCancel = (TextViewCSE) dialogOptions.findViewById(R.id.txt_item_modal_task_cancel);

                txtEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sprfToDo.edit().putString(CommonField.TASK, new Gson().toJson(task)).apply();

                        Toast.makeText(context, context.getString(R.string.not_yet_supported), Toast.LENGTH_SHORT).show();

//                        MainActivity.vpMain.setCurrentItem(CommonField.FRAGMENT_ADD_TASK_POSTION);
                        dialogOptions.dismiss();
                    }
                });
                txtDelete.setOnClickListener(new View.OnClickListener()

                {
                    @Override
                    public void onClick(View v) {
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                        ref.child("tasks").child(MainActivity.user.getUid()).child(task.getKey()).removeValue();
                        dialogOptions.dismiss();
                    }
                });
                txtCancel.setOnClickListener(new View.OnClickListener()

                {
                    @Override
                    public void onClick(View v) {
                        dialogOptions.dismiss();
                    }
                });

                dialogOptions.show();
                Window window = dialogOptions.getWindow();
                if (window != null) {
                    window.setLayout(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout lnlItemTask;
        public ImageButton btnOption;
        public TextViewCSE txtTaskDate, txtTaskTime, txtTaskName, txtTaskDetail, txtTaskStatus;

        public ViewHolder(View itemView) {
            super(itemView);
            lnlItemTask = (LinearLayout) itemView.findViewById(R.id.lnl_item_task);
            btnOption = (ImageButton) itemView.findViewById(R.id.btn_item_task_options);
            txtTaskDate = (TextViewCSE) itemView.findViewById(R.id.txt_item_task_date);
            txtTaskTime = (TextViewCSE) itemView.findViewById(R.id.txt_item_task_time);
            txtTaskName = (TextViewCSE) itemView.findViewById(R.id.txt_item_task_name);
            txtTaskDetail = (TextViewCSE) itemView.findViewById(R.id.txt_item_task_detail);
            txtTaskStatus = (TextViewCSE) itemView.findViewById(R.id.txt_item_task_status);
        }
    }
}
