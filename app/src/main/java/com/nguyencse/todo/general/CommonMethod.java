package com.nguyencse.todo.general;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.nguyencse.todo.auth.AuthActivity;
import com.nguyencse.todo.custom.ButtonCSE;
import com.nguyencse.todo.main.MainActivity;
import com.nguyencse.todo.R;

/**
 * Created by Putin on 4/30/2017.
 */

public class CommonMethod {
    public static void setPositionDialog(Dialog dialog, int pos) {
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = null;
        if (window != null) {
            wlp = window.getAttributes();
        }

        if (wlp != null) {
            wlp.gravity = pos;
            wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);
        }
    }

    public static void logout(final Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.custom_layout_modal_logout);
        dialog.show();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        }

        ButtonCSE btnYes = (ButtonCSE) dialog.findViewById(R.id.btn_item_modal_logout_yes);
        ButtonCSE btnNo = (ButtonCSE) dialog.findViewById(R.id.btn_item_modal_logout_no);

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                MainActivity.dbTodo.deleteAllTasks();
                activity.startActivity(new Intent(activity, AuthActivity.class));
                activity.finish();
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
    }
}
