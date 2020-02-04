package com.ax.yqview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

public class PinyinTextView extends AppCompatTextView {
    public PinyinTextView(Context context) {
        this(context,null);
    }

    public PinyinTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
}
