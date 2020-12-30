package com.dtrung98.presentation;

import android.view.ViewGroup;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;

import java.util.ArrayDeque;
import java.util.Iterator;

public class PresentationStylesController {
    PresentationStylesController(@NonNull ViewGroup appRootView) {
        mAppRootView = appRootView;
    }

    @NonNull
    public static PresentationStylesController of(@NonNull ViewGroup appRootView) {
        Object controller = appRootView.getTag(R.id.presentation_presentation_layers_controller);
        if (!(controller instanceof PresentationStylesController)) {
            controller = new PresentationStylesController(appRootView);
            appRootView.setTag(R.id.presentation_presentation_layers_controller, controller);
        }
        return (PresentationStylesController) controller;
    }

    @NonNull
    private final ViewGroup mAppRootView;
    private ArrayDeque<PresentationEntry> mEntries = new ArrayDeque<>();
    private int mPrimaryPresentationStyleId = 0;

    public boolean hasPrimaryPresentation() {
        return mPrimaryPresentationStyleId != 0;
    }

    @NonNull
    public PresentationEntry addPresentationEntry(PresentationStyle presentationStyle) {
        PresentationEntry entry = new PresentationEntry(presentationStyle.getId());

        if (mPrimaryPresentationStyleId == 0 && presentationStyle instanceof PrimaryPresentationFragment.PrimaryPresentationPresentationStyle) {
            // if the presentationStyle is the primary
            // put it to last
            mPrimaryPresentationStyleId = presentationStyle.getId();
            mEntries.addFirst(entry);
        } else {
            mEntries.add(entry);
        }
        return entry;
    }

    @NonNull
    public void removePresentationEntry(int id) {
        PresentationEntry entry = getPresentationEntry(id);
        if (entry != null) {
            mEntries.remove(entry);
        }
        if (id == mPrimaryPresentationStyleId) {
            mPrimaryPresentationStyleId = 0;
        }
    }

    public int getCurrentPresentationLayerId() {
        PresentationEntry entry = getCurrentPresentationEntry();
        return entry != null ? entry.getId() : 0;
    }

    public PresentationEntry getCurrentPresentationEntry() {
        if (mEntries.isEmpty()) {
            return null;
        } else {
            return mEntries.getLast();
        }
    }

    public PresentationEntry getPreviousPresentationEntry() {
        Iterator<PresentationEntry> iterator = mEntries.descendingIterator();
        // throw the topmost destination away.
        if (iterator.hasNext()) {
            iterator.next();
        }
        return iterator.hasNext() ? iterator.next() : null;
    }

    private PresentationEntry getPreviousPreviousPresentationLayerEntry() {
        Iterator<PresentationEntry> iterator = mEntries.descendingIterator();
        // throw the topmost destination away.
        if (iterator.hasNext()) {
            iterator.next();
        }

        if (iterator.hasNext()) {
            iterator.next();
        }

        return iterator.hasNext() ? iterator.next() : null;
    }

    public PresentationEntry getPresentationEntry(int layerId) {
        PresentationEntry lastFromBackStack = null;
        Iterator<PresentationEntry> iterator = mEntries.descendingIterator();
        while (iterator.hasNext()) {
            PresentationEntry entry = iterator.next();
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
     * Update the presenting animation progress fraction
     * Called by {@link PresentationStyle} which is currently running a presenting or dismissing animation
     *
     * @param value fraction value from 0 to 1
     */
    public void updatePresentingFraction(@FloatRange(from = 0.0, to = 1.0) float value) {

        // update presenting fraction value in current presentation entry
        PresentationEntry currentEntry = getCurrentPresentationEntry();
        if (currentEntry != null) {
            currentEntry.mPresentingFractionLiveData.postValue(value);
        }

        // update underlying fraction value in previous presentation entry
        PresentationEntry prevEntry = getPreviousPresentationEntry();
        if (prevEntry != null) {
            getPreviousPresentationEntry().mUnderlyingFractionLiveData.postValue(value);
        }

        // update underlying underlying fraction value in 3rf presentation entry
        PresentationEntry prevPrevEntry = getPreviousPreviousPresentationLayerEntry();
        if (prevPrevEntry != null) {
            getPreviousPreviousPresentationLayerEntry().mUnderlyingUnderlyingFractionLiveData.postValue(value);
        }
    }
}
