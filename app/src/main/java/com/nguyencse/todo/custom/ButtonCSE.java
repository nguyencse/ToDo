package com.nguyencse.todo.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.Button;

import com.nguyencse.todo.R;

/**
 * Created by Putin on 3/4/2017.
 */

public class ButtonCSE extends Button {
    private String font = "";

    public ButtonCSE(Context context) {
        super(context);
        init(null);
    }

    public ButtonCSE(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ButtonCSE(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ButtonCSE(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    public void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ButtonCSE, 0, 0);
            this.font = typedArray.getString(R.styleable.ButtonCSE_cse_typeface_button);
        }

        String fontName = this.font != null ? this.font : getContext().getString(R.string.font_opensans_light);
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/" + fontName + ".ttf");
        setTypeface(typeface);
    }
}
