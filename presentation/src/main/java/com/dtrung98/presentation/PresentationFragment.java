package com.dtrung98.presentation;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.ContentView;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dtrung98.presentation.drawer.DrawerStyle;
import com.dtrung98.presentation.drawer.DrawerStyleAttribute;
import com.dtrung98.presentation.fullscreen.FullscreenStyle;
import com.dtrung98.presentation.fullscreen.FullscreenStyleAttribute;

import java.util.HashMap;

/**
 * A {@link FloatingViewFragment} which can be shown in multiple style based on the current configuration.
 */
public class PresentationFragment extends FloatingViewFragment {
    private final FullscreenStyleAttribute mFullscreenStyleAttribute = new FullscreenStyleAttribute();
    private final DrawerStyleAttribute mDrawerStyleAttribute = new DrawerStyleAttribute();

    public FullscreenStyleAttribute getFullscreenStyleAttribute() {
        return mFullscreenStyleAttribute;
    }

    public DrawerStyleAttribute getDrawerStyleAttribute() {
        return mDrawerStyleAttribute;
    }


    public final PresentationStyleProvider getPresentationStyleProvider() {
        if (mPresentationStyleProvider == null) {
            mPresentationStyleProvider = new PresentationStyleProvider();
        }
        return mPresentationStyleProvider;
    }

    private PresentationStyleProvider mPresentationStyleProvider;

    public final PresentationStyle getPresentationStyle(String name) {
        return getPresentationStyleProvider().get(name);
    }

    public String getCurrentPresentationStyle() {
        return mCurrentPresentationStyle;
    }

    private void setCurrentPresentationStyle(String currentPresentationStyle) {
        if (currentPresentationStyle == null) {
            mCurrentPresentationStyle = "";
        } else {
            mCurrentPresentationStyle = currentPresentationStyle;
        }
    }

    private String mCurrentPresentationStyle = "";

    /**
     * Called when creating the {@link PresentationStyleProvider}
     * Override this method to add custom {@link PresentationStyle} to the provider
     */
    protected void onCreatePresentationStyleProvider() {
        PresentationStyleProvider provider = getPresentationStyleProvider();
        provider.addStyle(new FullscreenStyle(getAppRootView(), mFullscreenStyleAttribute));
        provider.addStyle(new DrawerStyle(getAppRootView(), mDrawerStyleAttribute));
        //provider.addStyle("", new FullscreenStyle());
    }

    /**
     * return the current presentation style based on current configuration
     *
     * @return the presentation style used for this presentation
     */
    protected String retrievePresentationStyle() {

        Configuration configuration = requireContext().getResources().getConfiguration();
        int wQualifier = configuration.screenWidthDp;
        int hQualifier = configuration.screenHeightDp;

        if (wQualifier >= 448 && hQualifier >= 448) {
            /* tablet */
            return "drawer";//"dialog";
        } else if (hQualifier >= 300 && (float) hQualifier / wQualifier >= 4f / 3) {
            /* mobile-portrait */
            return "drawer";//"bottomsheet";
        } else {
            /* mobile-landscape, mobile small screen */
            /* fullscreen */
            return "fullscreen";
        }
    }

    private boolean mAdaptivePresentation = true;
    @NonNull
    private String mPreferredPresentationStyle = "fullscreen";

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save all presentation states
        HashMap<String, PresentationStyle> styleMap = getPresentationStyleProvider().getHashMap();
        for (PresentationStyle style : styleMap.values()) {
            style.onSavePresentationState(outState);
        }
    }

    public PresentationFragment() {
        super();
    }

    @ContentView
    public PresentationFragment(@LayoutRes int contentLayoutId) {
        super(contentLayoutId);
    }

    @Override
    @NonNull
    public final ContentViewContainer onCreateContainer(@Nullable Bundle savedInstanceState) {
        onCreatePresentationStyleProvider();

        // Restore all presentation states
        if (savedInstanceState != null) {
            HashMap<String, PresentationStyle> styleMap = getPresentationStyleProvider().getHashMap();
            for (PresentationStyle style :
                    styleMap.values()) {
                style.onRestorePresentationState(savedInstanceState);
            }
        }

        // get the current presentation style
        final String presentationStyle;
        final String style = retrievePresentationStyle();

        if(style == null || !isAdaptivePresentation()) {
            presentationStyle = getPreferredPresentationStyle();
        } else {
            presentationStyle = style;
        }

        setCurrentPresentationStyle(presentationStyle);

        final ContentViewContainer container;

        container = getPresentationStyleProvider().get(presentationStyle);

        if (container == null) {
            throw new IllegalArgumentException("The container with name [" + presentationStyle + "] isn't existed in Provider. Custom presentation style need to be added to provider to use");
        }

        return container;
    }

    public boolean isAdaptivePresentation() {
        return mAdaptivePresentation;
    }

    public void setAdaptivePresentation(boolean adaptive) {
        this.mAdaptivePresentation = adaptive;
    }

    @NonNull
    public String getPreferredPresentationStyle() {
        return mPreferredPresentationStyle;
    }

    @NonNull
    public void setPreferredPresentationStyle(@NonNull String style) {
        mPreferredPresentationStyle = style;
    }
}
