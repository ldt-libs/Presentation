package com.dtrung98.presentation;

import android.view.ViewGroup;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;

import java.util.ArrayDeque;
import java.util.Iterator;

public class PresentationStylesController {
    PresentationStylesController(@NonNull ViewGroup appRootView) {
        mAppRootView = appRootView;
        mAppRootView.setTag(R.id.presentation_presentation_layers_controller, this);
    }

    @NonNull
    public static PresentationStylesController of(@NonNull ViewGroup appRootView) {
        Object o = appRootView.getTag(R.id.presentation_presentation_layers_controller);
        if (!(o instanceof PresentationStylesController)) {
            o = new PresentationStylesController(appRootView);
        }
        return (PresentationStylesController) o;
    }

    @NonNull
    private final ViewGroup mAppRootView;
    private ArrayDeque<PresentationLayerEntry> mEntries = new ArrayDeque<>();

    @NonNull
    public PresentationLayerEntry addPresentationLayer(PresentationStyle presentationStyle) {
        PresentationLayerEntry entry = new PresentationLayerEntry(presentationStyle.getId());
        mEntries.add(entry);
        return entry;
    }

    @NonNull
    public void removePresentationLayer(int id) {
        PresentationLayerEntry entry = getPresentationEntry(id);
        if (entry != null) {
            mEntries.remove(entry);
        }
    }

    public int getCurrentPresentationLayerId() {
        PresentationLayerEntry entry = getCurrentPresentationLayerEntry();
        return entry != null ? entry.getId() : 0;
    }

    public PresentationLayerEntry getCurrentPresentationLayerEntry() {
        if (mEntries.isEmpty()) {
            return null;
        } else {
            return mEntries.getLast();
        }
    }

    public PresentationLayerEntry getPreviousPresentationLayerEntry() {
        Iterator<PresentationLayerEntry> iterator = mEntries.descendingIterator();
        // throw the topmost destination away.
        if (iterator.hasNext()) {
            iterator.next();
        }
        return iterator.hasNext() ? iterator.next() : null;
    }

    public PresentationLayerEntry getPresentationEntry(int layerId) {
        PresentationLayerEntry lastFromBackStack = null;
        Iterator<PresentationLayerEntry> iterator = mEntries.descendingIterator();
        while (iterator.hasNext()) {
            PresentationLayerEntry entry = iterator.next();
            int iteratorLayerId = entry.getId();
            if (iteratorLayerId == layerId) {
                lastFromBackStack = entry;
                break;
            }
        }
      /*  if (lastFromBackStack == null) {
            throw new IllegalArgumentException("No layer with ID " + layerId
                    + " is on the PresentationStyleController stack. The current layer is "
                    + getCurrentPresentationLayerId());
        }*/
        return lastFromBackStack;
    }

    /**
     * Update the foreground fraction of a
     *
     * @param value
     */
    public void updateForegroundLayerFraction(@FloatRange(from = 0.0, to = 1.0) float value) {
        PresentationLayerEntry currentEntry = getCurrentPresentationLayerEntry();
        if (currentEntry != null) {
            currentEntry.mForegroundFractionLiveData.postValue(value);
        }
        PresentationLayerEntry prevEntry = getPreviousPresentationLayerEntry();
        if (prevEntry != null) {
            getPreviousPresentationLayerEntry().mBackgroundFractionLiveData.postValue(value);
        }
    }
}
