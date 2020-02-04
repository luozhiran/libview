package com.ax.yqview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

public class PinyinTextView extends android.support.v7.widget.AppCompatTextView{
    public PinyinTextView(Context context) {
        this(context,null);
    }

    public PinyinTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
}
