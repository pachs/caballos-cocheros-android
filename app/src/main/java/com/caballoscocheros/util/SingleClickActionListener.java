package com.caballoscocheros.util;

import android.os.SystemClock;
import android.view.View;

/**
 * Created by Alvin on 14/02/2016.
 */
public abstract class SingleClickActionListener implements View.OnClickListener {

    private static final long CLICK_INTERVAL = 600;

    private long mLastClickTime;

    public abstract void onSingleClick(View v);

    @Override
    public void onClick(View v) {
        long currentClickTime = SystemClock.uptimeMillis();
        long elapsedTime = currentClickTime - mLastClickTime;
        mLastClickTime = currentClickTime;

        if (elapsedTime <= CLICK_INTERVAL)
            return;

        onSingleClick(v);
    }
}
