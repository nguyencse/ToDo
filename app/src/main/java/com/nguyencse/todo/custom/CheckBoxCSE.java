package com.nguyencse.todo.custom;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.CheckBox;

import com.nguyencse.todo.R;

/**
 * Created by Putin on 3/4/2017.
 */

public class CheckBoxCSE extends CheckBox {
    private String font = "";

    public CheckBoxCSE(Context context) {
        super(context);
        init(null);
    }

    public CheckBoxCSE(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CheckBoxCSE(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CheckBoxCSE(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CheckBoxCSE, 0, 0);
            this.font = typedArray.getString(R.styleable.CheckBoxCSE_cse_tyface_checkbox);
        }

        String fontName = this.font != null ? this.font : getContext().getString(R.string.font_opensans_light);

        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/" + fontName + ".ttf");
        setTypeface(typeface);
    }
}
