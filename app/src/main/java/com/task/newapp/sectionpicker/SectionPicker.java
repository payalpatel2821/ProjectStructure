package com.task.newapp.sectionpicker;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.task.newapp.R;
import com.task.newapp.sectionpicker.helper.AttrHelper;


/**
 * Created by EgemenH on 02.06.2017.
 */

public class SectionPicker extends View {

    public String[] sections = {"A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z"};
    private TextView textViewIndicator;
    private int fontSize;
    private ColorStateList color;
    private Typeface typeFace;
    private ColorStateList chosenColor;
    private Typeface chosenTypeFace;
    private boolean indicatorEnabled = true;
    private OnTouchingLetterChangedListener onTouchingLetterChangedListener;
    private int chosenIndex = -1;
    private Paint paint = new Paint();


    public SectionPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    public SectionPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public SectionPicker(Context context) {
        super(context);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            setAttrs(attrs);
        }
    }

    protected void setAttrs(AttributeSet attrs) {
        Context context = getContext();

        int textSize = AttrHelper.getFontSize(context, attrs, R.styleable.SectionPicker, R.styleable.SectionPicker_SectionPicker_textSize);
        setFontSize(textSize);

        ColorStateList color = AttrHelper.getFontColor(context, attrs, R.styleable.SectionPicker, R.styleable.SectionPicker_SectionPicker_textColor);
        setColor(color);

        int textStyle = AttrHelper.getFontStyle(context, attrs, R.styleable.SectionPicker, R.styleable.SectionPicker_SectionPicker_textStyle);
        setTypeFace(Typeface.create(Typeface.DEFAULT, textStyle));

        ColorStateList chosenColor = AttrHelper.getFontColor(context, attrs, R.styleable.SectionPicker, R.styleable.SectionPicker_SectionPicker_chosenColor);
        setChosenColor(chosenColor);

        int chosenStyle = AttrHelper.getFontStyle(context, attrs, R.styleable.SectionPicker, R.styleable.SectionPicker_SectionPicker_chosenStyle);
        setChosenTypeFace(Typeface.create(Typeface.DEFAULT, chosenStyle));

        boolean isIndicatorEnabled = AttrHelper.getIndicatorEnabled(context, attrs, R.styleable.SectionPicker, R.styleable.SectionPicker_SectionPicker_indicatorEnabled);
        setIndicatorEnabled(isIndicatorEnabled);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int height = getHeight();
        int width = getWidth();

        if (sections.length > 0) {
            int singleHeight = height / sections.length;

            for (int i = 0; i < sections.length; i++) {
                paint.setAntiAlias(true);
                paint.setTextSize(fontSize);

                if (i == chosenIndex) {
                    paintText(chosenColor, chosenTypeFace);
                } else {
                    paintText(color, typeFace);
                }

                float xPos = width / 2 - paint.measureText(sections[i]) / 2;
                float yPos = singleHeight * i + singleHeight;
                canvas.drawText(sections[i], xPos, yPos, paint);
                paint.reset();
            }
        }
    }

    private void paintText(ColorStateList colorStateList, Typeface typeFace) {
        paint.setColor((colorStateList != null) ? colorStateList.getDefaultColor() : Color.BLACK);
        paint.setTypeface((typeFace != null) ? typeFace : Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float y = event.getY();
        final int oldChoose = chosenIndex;
        final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
        final int c = (int) (y / getHeight() * sections.length);

        switch (action) {
            case MotionEvent.ACTION_UP: {
                chosenIndex = -1;
                invalidate();
                if (indicatorEnabled && (textViewIndicator != null)) {
                    textViewIndicator.setVisibility(View.INVISIBLE);
                }
                break;
            }

            default: {
                if (oldChoose != c) {
                    if (c >= 0 && c < sections.length) {
                        if (listener != null) {
                            listener.onTouchingLetterChanged(sections[c]);
                        }
                        if (indicatorEnabled && (textViewIndicator != null)) {
                            textViewIndicator.setText(sections[c]);
                            textViewIndicator.setVisibility(View.VISIBLE);
                        }

                        chosenIndex = c;
                        invalidate();
                    }
                }
                break;
            }
        }
        return true;
    }

    public TextView getTextViewIndicator() {
        return textViewIndicator;
    }

    public void setTextViewIndicator(TextView textViewIndicator) {
        this.textViewIndicator = textViewIndicator;
    }

    public Typeface getTypeFace() {
        return typeFace;
    }

    public void setTypeFace(Typeface typeFace) {
        this.typeFace = typeFace;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public String[] getSections() {
        return sections;
    }

    public void setSections(String[] sections) {
        this.sections = sections;
    }

    public ColorStateList getColor() {
        return color;
    }

    public void setColor(ColorStateList color) {
        this.color = color;
    }

    public ColorStateList getChosenColor() {
        return chosenColor;
    }

    public void setChosenColor(ColorStateList chosenColor) {
        this.chosenColor = chosenColor;
    }

    public Typeface getChosenTypeFace() {
        return chosenTypeFace;
    }

    public void setChosenTypeFace(Typeface chosenTypeFace) {
        this.chosenTypeFace = chosenTypeFace;
    }

    public boolean isIndicatorEnabled() {
        return indicatorEnabled;
    }

    public void setIndicatorEnabled(boolean indicatorEnabled) {
        this.indicatorEnabled = indicatorEnabled;
    }

    public void setOnTouchingLetterChangedListener(
            OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
        this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
    }

    public interface OnTouchingLetterChangedListener {
        void onTouchingLetterChanged(String s);
    }
}
