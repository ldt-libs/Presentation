package com.dtrung98.presentation;

import android.content.Context;
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
 * Show a fragment that floats over the screen.
 * <br/>This class is expected to replace the existing Dialog Fragment.
 * <br/>It works as same as DialogFragment, but instead of creating a dialog to wrap the layout,
 * PresentationFragment(FloatingViewFragment) adds its layout to android.R.id.content root view so
 * no need dialog anymore.
 * <br/>Dismiss the owner fragment will dismiss its child PresentationFragment (FloatingViewFragment) automatically.
 * <br/> Back pressed events are handled automatically by default, but you have an option to disable it.
 * A {@link PresentationFragment} can be shown in multiple style based on the current configuration.
 */
public class PresentationFragment extends FloatingViewFragment {
    public static final String SAVED_IS_ADAPTIVE_PRESENTATION = "saved-is-adaptive-presentation";
    public static final String SAVED_PREFERRED_PRESENTATION_STYLE = "saved-preferred-presentation-style";
    private final FullscreenStyleAttribute mFullscreenStyleAttribute = new FullscreenStyleAttribute();
    private final DrawerStyleAttribute mDrawerStyleAttribute = new DrawerStyleAttribute();

    public FullscreenStyleAttribute getFullscreenStyleAttribute() {
        return mFullscreenStyleAttribute;
    }

    public DrawerStyleAttribute getDrawerStyleAttribute() {
        return mDrawerStyleAttribute;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Object primaryContainerExists = getAppRootView().getTag(R.id.content_view_container_id_primary);
        if (!(this instanceof PrimaryPresentationFragment) && (primaryContainerExists == null || ((primaryContainerExists instanceof Boolean) && !((Boolean) primaryContainerExists)))) {
            new PrimaryPresentationFragment().show(getChildFragmentManager(), "primary-presentation-fragment");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
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

        // adaptive presentation configuration
        if (!mAdaptivePresentation) {
            outState.putBoolean(SAVED_IS_ADAPTIVE_PRESENTATION, false);
        }

        outState.putString(SAVED_PREFERRED_PRESENTATION_STYLE, mPreferredPresentationStyle);

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
            mAdaptivePresentation = savedInstanceState.getBoolean(SAVED_IS_ADAPTIVE_PRESENTATION, mAdaptivePresentation);
            mPreferredPresentationStyle = savedInstanceState.getString(SAVED_PREFERRED_PRESENTATION_STYLE, mPreferredPresentationStyle);

            HashMap<String, PresentationStyle> styleMap = getPresentationStyleProvider().getHashMap();
            for (PresentationStyle style :
                    styleMap.values()) {
                style.onRestorePresentationState(savedInstanceState);
            }
        }

        // get the current presentation style
        final String presentationStyle;
        final String style = retrievePresentationStyle();

        if (style == null || !isAdaptivePresentation()) {
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
