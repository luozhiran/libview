package com.ax.yqview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public class StateLinearLayout extends LinearLayout {
    private boolean touchEffect = true;
    private int linearAlpha = 255;
    private int color;

    public StateLinearLayout(Context context) {
        this(context, null);
    }

    public StateLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.StateLinearLayout);
        linearAlpha = array.getInt(R.styleable.StateLinearLayout_linearAlpha,255);
        color = array.getColor(R.styleable.StateLinearLayout_linearSelectColor,Color.GRAY);
        array.recycle();
    }

    @Override
    public void setPressed(boolean pressed) {
        updateView(pressed);
        super.setPressed(pressed);
    }

    /**
     * 根据是否按下去来刷新bg和src
     * created by minghao.zl at 2014-09-18
     *
     * @param pressed
     */
    private void updateView(boolean pressed) {
        //如果没有点击效果
        if (!touchEffect) {
            return;
        }//end if
        if (pressed) {//点击
            /**
             * 通过设置滤镜来改变图片亮度@minghao
             */
            setFilter();
        } else {//未点击
            removeFilter();
        }
    }


    public void setTouchEffect(boolean touchEffect) {
        this.touchEffect = touchEffect;
    }

    /**
     * 设置滤镜
     */
    public void setFilter() {
        //先获取设置的src图片
        Drawable drawable = getBackground();
        if (drawable != null) {
            //设置滤镜
            drawable.setAlpha(linearAlpha);
            drawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        }
    }

    /**
     * 清除滤镜
     */
    public void removeFilter() {
        //先获取设置的src图片
        Drawable drawable = getBackground();
        if (drawable != null) {
            //清除滤镜
            drawable.setAlpha(255);
            drawable.clearColorFilter();
        }
    }
}
