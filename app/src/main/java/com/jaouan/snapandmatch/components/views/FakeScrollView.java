package com.jaouan.snapandmatch.components.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Unlimited height view is a view without... height limit. It allows you to have inner view taller than container.
 * This trick is done by extending a ScrollView and bypassing touch event.
 *
 * @author Maxence Jaouan
 */
public class FakeScrollView extends ScrollView {

    public FakeScrollView(Context context) {
        super(context);
    }

    public FakeScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FakeScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FakeScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onTouchEvent(final MotionEvent motionEvent) {
        return true;
    }

}
