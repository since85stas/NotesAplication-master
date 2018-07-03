package com.batura.stas.notesaplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.EditText;

import com.batura.stas.notesaplication.Static.NoteUtils;

/**
 * Created by seeyo on 02.07.2018.
 */
public class LinedEditText extends android.support.v7.widget.AppCompatEditText {
    private Rect mRect;
    private Paint mPaint;

    public void setColorId(int colorId) {
        mColorId = colorId;
    }

    private int mColorId = 665;

    // we need this constructor for LayoutInflater
    public LinedEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        mRect = new Rect();
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);

        int color = NoteUtils.getBackColor(mColorId);
        mPaint.setColor(ContextCompat.getColor(context, color));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int count = getLineCount();

        int color = NoteUtils.getBackColorAccent(mColorId);
        mPaint.setColor(ContextCompat.getColor(getContext(), color));
        Rect r = mRect;
        Paint paint = mPaint;

        for (int i = 0; i < count; i++) {
            int baseline = getLineBounds(i, r);

            canvas.drawLine(r.left, baseline + 1, r.right, baseline + 1, paint);
        }

        super.onDraw(canvas);
    }
}
