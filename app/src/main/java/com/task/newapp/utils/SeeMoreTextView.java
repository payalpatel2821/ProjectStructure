package com.task.newapp.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.AppCompatTextView;

import com.task.newapp.R;


public class SeeMoreTextView extends AppCompatTextView {

    private Integer textMaxLength = 300;
    private Integer seeMoreTextColor = R.color.colorPrimary;

    private String collapsedTextWithSeeMoreButton;
    private String expandedTextWithSeeMoreButton;
    private String orignalContent;

    private SpannableString collapsedTextSpannable;
    private SpannableString expandedTextSpannable;

    private Boolean isExpanded = false;

    private String seeMore = "see more", seeLess = "see less";

    public SeeMoreTextView(Context context) {
        super(context);
    }

    public SeeMoreTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SeeMoreTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    //set max length of the string text
    public void setTextMaxLength(Integer maxLength) {
        textMaxLength = maxLength;
    }

    public void setSeeMoreTextColor(Integer color) {
        seeMoreTextColor = color;
    }

    public void expandText(Boolean expand) {
        if (expand) {
            isExpanded = true;
            setText(expandedTextSpannable);
        } else {
            isExpanded = false;
            setText(collapsedTextSpannable);
        }

    }

    public void setSeeMoreText(String seeMoreText, String seeLessText) {
        seeMore = seeMoreText;
        seeLess = seeLessText;
    }

    public Boolean isExpanded() {
        return isExpanded;
    }

    //toggle the state
    public void toggle() {
        if (isExpanded) {
            isExpanded = false;
            setText(collapsedTextSpannable);
        } else {
            isExpanded = true;
            setText(expandedTextSpannable);
        }
    }


    public void setContent(String text) {
        orignalContent = text;
        this.setMovementMethod(LinkMovementMethod.getInstance());
        //show see more
        if (orignalContent.length() >= textMaxLength) {
            collapsedTextWithSeeMoreButton = orignalContent.substring(0, textMaxLength) + "... " + seeMore;
            expandedTextWithSeeMoreButton = orignalContent + " " + seeLess;

            //creating spannable strings
            collapsedTextSpannable = new SpannableString(collapsedTextWithSeeMoreButton);
            expandedTextSpannable = new SpannableString(expandedTextWithSeeMoreButton);


            collapsedTextSpannable.setSpan(clickableSpan, textMaxLength + 4, collapsedTextWithSeeMoreButton.length(), 0);
            collapsedTextSpannable.setSpan(new StyleSpan(Typeface.BOLD), textMaxLength + 4, collapsedTextWithSeeMoreButton.length(), 0);
            collapsedTextSpannable.setSpan(new RelativeSizeSpan(.9f), textMaxLength + 4, collapsedTextWithSeeMoreButton.length(), 0);
            expandedTextSpannable.setSpan(clickableSpan, orignalContent.length() + 1, expandedTextWithSeeMoreButton.length(), 0);
            expandedTextSpannable.setSpan(new StyleSpan(Typeface.BOLD), orignalContent.length() + 1, expandedTextWithSeeMoreButton.length(), 0);
            expandedTextSpannable.setSpan(new RelativeSizeSpan(.9f), orignalContent.length() + 1, expandedTextWithSeeMoreButton.length(), 0);

            if (isExpanded)
                setText(expandedTextSpannable);
            else
                setText(collapsedTextSpannable);
        } else {
            //to do: don't show see more
            setText(orignalContent);
        }
    }


    ClickableSpan clickableSpan = new ClickableSpan() {
        @Override
        public void onClick(View widget) {
            toggle();
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
            ds.setColor(getResources().getColor(R.color.colorPrimary));
        }
    };

}