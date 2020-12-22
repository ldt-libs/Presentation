package com.dtrung98.presentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.HashMap;
import java.util.Map;

public class PresentationLayerEntry {
    public static final String FLAG_REQUIRE_PREVIOUS_LAYER_CARD_PRESENTATION = "FLAG_REQUIRE_PREVIOUS_LAYER_CARD_PRESENTATION";
    public static final String FLAG_REQUIRE_PREVIOUS_LAYER_SLIDE_HORIZONTAL = "FLAG_REQUIRE_PREVIOUS_LAYER_SLIDE_HORIZONTAL";

    public int getId() {
        return mId;
    }

    private final int mId;

    public PresentationLayerEntry(int id) {
        mId = id;
    }

    /**
     * @return the fraction value in (0,1) when the layer is in foreground mode (layer is the top-most item in stack)
     */
    public LiveData<Float> getForegroundFractionLiveData() {
        return mForegroundFractionLiveData;
    }

    /**
     *
     * @return the fraction value in (0,1) when the layer is in background mode (layer is behind others in stack)
     */
    public LiveData<Float> getBackgroundFractionLiveData() {
        return mBackgroundFractionLiveData;
    }

    final MutableLiveData<Float> mForegroundFractionLiveData = new MutableLiveData<>(0f);
    final MutableLiveData<Float> mBackgroundFractionLiveData = new MutableLiveData<>(0f);
    final Map<String, Boolean> mVisibilityFlags = new HashMap<>();

    public Boolean isContainFlag(String flag) {
        return mVisibilityFlags.containsKey(flag);
    }

    public void setFlag(String flag, boolean value) {
        mVisibilityFlags.put(flag, value);
    }
}
