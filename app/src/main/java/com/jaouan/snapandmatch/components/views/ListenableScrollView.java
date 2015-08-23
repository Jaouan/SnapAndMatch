package com.jaouan.snapandmatch.components.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ScrollView;

import com.jaouan.snapandmatch.components.utils.Procedure1;

/**
 * Listenable scroll view.
 *
 * @author Maxence Jaouan
 */
public class ListenableScrollView extends ScrollView {

    /**
     * Scroll listener interface.
     */
    public interface ScrollListener {
        void onScrollChanged(int newPosition);
    }

    /**
     * Scroll listener.
     */
    private ScrollListener mOnScrollListener;

    /**
     * No listener runnable.
     */
    private final Procedure1<Integer> noListenerRunnable = (newPosition) -> {
    };

    /**
     * Listener runnable.
     */
    private final Procedure1<Integer> listenerRunnable = (newPosition) -> {
        mOnScrollListener.onScrollChanged(newPosition);
    };

    /**
     * Current runnable.
     */
    private Procedure1<Integer> currentRunnable = noListenerRunnable;

    public ListenableScrollView(Context context) {
        super(context);
    }

    public ListenableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListenableScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ListenableScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        currentRunnable.proceed(t);
    }

    /**
     * Set on scroll listener.
     *
     * @param onScrollListener Listener.
     */
    public synchronized void setOnScrollListener(final ScrollListener onScrollListener) {
        if (onScrollListener == null) {
            currentRunnable = noListenerRunnable;
        } else {
            currentRunnable = listenerRunnable;
        }
        this.mOnScrollListener = onScrollListener;
    }


    /**
     * Remove on scroll listener.
     */
    public void removeOnScrollListener() {
        setOnScrollListener(null);
    }

}
