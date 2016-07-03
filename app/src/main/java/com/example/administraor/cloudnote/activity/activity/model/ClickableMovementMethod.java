package com.example.administraor.cloudnote.activity.activity.model;

import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * Created by zyw on 2016/6/12.
 */
public class ClickableMovementMethod extends LinkMovementMethod {
    private static ClickableMovementMethod sInstance;

    public static ClickableMovementMethod getInstance() {
        if (sInstance == null) {
            sInstance = new ClickableMovementMethod();
        }
        return sInstance;
    }

    public boolean onTouchEvent(TextView widget, Spannable buffer,
                                MotionEvent event) {
        int action = event.getAction();

        if (action == MotionEvent.ACTION_UP ||
                action == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();

            x += widget.getScrollX();
            y += widget.getScrollY();

            Layout layout = widget.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            ClickableImageSpan[] imageSpans = buffer.getSpans(off, off, ClickableImageSpan.class);

            if (imageSpans.length != 0) {
                if (action == MotionEvent.ACTION_UP) {
                    imageSpans[0].onClick(widget);
                }
                return true;
            } else {
                Selection.removeSelection(buffer);
            }
        }
        return false;
    }
}
