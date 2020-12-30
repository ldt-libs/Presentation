package com.dtrung98.presentation.slider;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.dtrung98.presentation.PresentationAttribute;

public class SliderStyleAttribute extends PresentationAttribute {
    public static final String LEFT_OVER_MARGIN_DP = "left-over-margin-dp";
    public static final String OPEN_COMPLETELY = "open-completely";
    public static final String CLOSE_COMPLETELY = "close_completely";
    private int mLeftOverMarginDp = 0;
    boolean mOpenCompletely = false;
    boolean mCloseCompletely = true;

    @NonNull
    @Override
    public Bundle onSaveInstanceState() {
        Bundle bundle = super.onSaveInstanceState();
        bundle.putInt(LEFT_OVER_MARGIN_DP, mLeftOverMarginDp);
        bundle.putBoolean(OPEN_COMPLETELY, mOpenCompletely);
        bundle.putBoolean(CLOSE_COMPLETELY, mCloseCompletely);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle bundle) {
        mLeftOverMarginDp = bundle.getInt(LEFT_OVER_MARGIN_DP, mLeftOverMarginDp);
        mOpenCompletely = bundle.getBoolean(OPEN_COMPLETELY, mOpenCompletely);
        mCloseCompletely = bundle.getBoolean(CLOSE_COMPLETELY, mCloseCompletely);
    }

    public int getLeftOverMarginDp() {
        return mLeftOverMarginDp;
    }

    public void setLeftOverMarginDp(int leftOverMarginDp) {
        mLeftOverMarginDp = leftOverMarginDp;
    }
}
