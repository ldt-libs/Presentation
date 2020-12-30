package com.dtrung98.presentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.HashMap;
import java.util.Map;

public class PresentationEntry {
    public static final String FLAG_REQUIRE_PREVIOUS_LAYER_CARD_PRESENTATION = "FLAG_REQUIRE_PREVIOUS_LAYER_CARD_PRESENTATION";
    public static final String FLAG_REQUIRE_PREVIOUS_LAYER_SLIDE_HORIZONTAL = "FLAG_REQUIRE_PREVIOUS_LAYER_SLIDE_HORIZONTAL";

    public int getId() {
        return mId;
    }

    private final int mId;

    public PresentationEntry(int id) {
        mId = id;
    }

    /**
     * @return the fraction value in (0,1) when this presentation is the top-most one (layer is the top-most item in stack)
     */
    public LiveData<Float> getPresentingFractionLiveData() {
        return mPresentingFractionLiveData;
    }

    /**
     *
     * @return the fraction value in (0,1) if this presentation (fragment) is underlying another (just behind the presenting fragment)
     */
    public LiveData<Float> getUnderlyingFractionLiveData() {
        return mUnderlyingFractionLiveData;
    }

    /**
     * The fraction value in (0,1) if this presentation is underlying 2 others
     * @return live data
     */
    public LiveData<Float> getUnderlyingUnderlyingLiveData() {
        return mUnderlyingUnderlyingFractionLiveData;
    }

    final MutableLiveData<Float> mPresentingFractionLiveData = new MutableLiveData<>(0f);
    final MutableLiveData<Float> mUnderlyingFractionLiveData = new MutableLiveData<>(0f);
    final MutableLiveData<Float> mUnderlyingUnderlyingFractionLiveData = new MutableLiveData<>(0f);
    final Map<String, Boolean> mVisibilityFlags = new HashMap<>();

    public Boolean hasFlag(String flag) {
        return mVisibilityFlags.containsKey(flag);
    }

    public void setFlag(String flag, boolean value) {
        mVisibilityFlags.put(flag, value);
    }
}
