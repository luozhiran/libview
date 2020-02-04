package com.ax.yqview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

import java.lang.reflect.Field;

public class StrokeTextView extends android.support.v7.widget.AppCompatTextView {

    private int outerColor = 0x000000;
    private int innnerColor = 0xffffff;
    private Paint paint;
    private int strokeWidth = 0;

    public StrokeTextView(Context context) {
        this(context, null);
    }

    public StrokeTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint = getPaint();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StrokeTextView);
        outerColor = a.getColor(R.styleable.StrokeTextView_innerColor, 0x000000);
        innnerColor = a.getColor(R.styleable.StrokeTextView_outerColor, 0xffffff);
        strokeWidth = a.getInt(R.styleable.StrokeTextView_borderStrokeWidth, 5);
        a.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        setTextColorUseReflection(outerColor);
        paint.setStrokeWidth(strokeWidth); // 描边宽度
        paint.setStyle(Paint.Style.FILL_AND_STROKE); // 描边种类
        paint.setFakeBoldText(true); // 外层text采用粗体
        paint.setShadowLayer(1, 0, 0, 0); // 字体的阴影效果，可以忽略
        super.onDraw(canvas);
        setTextColorUseReflection(innnerColor);
        paint.setStrokeWidth(0);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setFakeBoldText(false);
        paint.setShadowLayer(0, 0, 0, 0);
        super.onDraw(canvas);

    }

    /**
     * 使用反射的方法进行字体颜色的设置
     *
     * @param color
     */
    private void setTextColorUseReflection(int color) {
        Field textColorField;
        try {
            textColorField = TextView.class.getDeclaredField("mCurTextColor");
            textColorField.setAccessible(true);
            textColorField.set(this, color);
            textColorField.setAccessible(false);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        paint.setColor(color);
    }


}
