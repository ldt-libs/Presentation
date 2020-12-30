package com.dtrung98.presentation;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Like {@link Dialog}, but created from View only
 */
public abstract class PresentationStyle extends ContentViewContainer {

    public static final String PRESENTATION_STYLE_ATTRIBUTE = "PresentationStyle:Attribute:";

    @NonNull
    public PresentationStylesController findPresentationLayersController() {
        return PresentationStylesController.of(getAppRootView());
    }

    @Override
    protected void initContainer() {
        super.initContainer();
        PresentationStylesController controller = findPresentationLayersController();
        controller.addPresentationEntry(this);
    }

    public PresentationStyle(@NonNull ViewGroup appRootView) {
        this(appRootView, new PresentationAttribute());
    }

    @NonNull
    public PresentationAttribute getAttribute() {
        return mAttribute;
    }

    @NonNull
    private final PresentationAttribute mAttribute;

    public PresentationStyle(@NonNull ViewGroup appRootView, @Nullable PresentationAttribute attribute) {
        super(appRootView);

        if (attribute != null) {
            mAttribute = attribute;
        } else {
            mAttribute = new PresentationAttribute();
        }
    }

    /**
     * Unique name used to identify the {@link PresentationStyle}
     */
    @NonNull
    public abstract String getName();

    @Override
    void dismissContainer() {
        super.dismissContainer();
        findPresentationLayersController().removePresentationEntry(getId());
    }

    @Override
    public abstract void setContentView(View view);

    /**
     * Save the presentation state data, which will survive after presentation styles change
     *
     * @param bundle bundle
     */
    public void onSavePresentationState(@NonNull Bundle bundle) {
        bundle.putBundle(PRESENTATION_STYLE_ATTRIBUTE + getName(), mAttribute.onSaveInstanceState());
    }

    /**
     * Restore the presentation state data
     *
     * @param bundle bundle
     */
    public void onRestorePresentationState(@NonNull Bundle bundle) {
        Bundle attributeBundle = bundle.getBundle(PRESENTATION_STYLE_ATTRIBUTE + getName());
        if (attributeBundle != null) {
            mAttribute.onRestoreInstanceState(attributeBundle);
        }
    }
}
