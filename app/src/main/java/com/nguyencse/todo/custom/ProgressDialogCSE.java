package com.nguyencse.todo.custom;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by Putin on 7/4/2017.
 */

public class ProgressDialogCSE extends ProgressDialog {
    public ProgressDialogCSE(Context context) {
        super(context);
    }

    public ProgressDialogCSE(Context context, int theme) {
        super(context, theme);
        setCanceledOnTouchOutside(false);
        setProgressStyle(android.R.style.Widget_ProgressBar_Small);
    }
}
