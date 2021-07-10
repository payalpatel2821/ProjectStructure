package com.task.newapp.utils.socialtextview;
import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ClickableSpan;

public abstract class TouchableSpan extends ClickableSpan {
    private final boolean underlineEnabled;
    private final int pressedTextColor;
    private final int normalTextColor;
    private boolean pressed;


    public TouchableSpan(int normalTextColor, int pressedTextColor, boolean underlineEnabled) {
        this.normalTextColor = normalTextColor;
        this.pressedTextColor = pressedTextColor;
        this.underlineEnabled = underlineEnabled;
    }

    @Override
    public void updateDrawState(TextPaint paint) {
        // Determine whether to paint it pressed or normally
        int textColor = pressed ? pressedTextColor : normalTextColor;
        paint.setColor(textColor);
        paint.setUnderlineText(underlineEnabled);
        paint.bgColor = Color.TRANSPARENT;
    }
    void setPressed(boolean value) {
        this.pressed = value;
    }
}