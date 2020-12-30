package com.dtrung98.presentation;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dtrung98.presentation.fullscreen.FullscreenPresentationStyle;
import com.dtrung98.presentation.fullscreen.FullscreenStyleAttribute;

public class PrimaryPresentationFragment extends PresentationFragment {
    public PrimaryPresentationFragment() {
        super();
        setAdaptivePresentation(false);
        setPreferredPresentationStyle("fullscreen");
        setBackPressedCallbackEnabled(false);
    }

    @Override
    public ViewGroup getAppRootView() {
        Fragment fragment = requireParentFragment();
        if (!(fragment instanceof PresentationFragment)) {
            throw new IllegalStateException("The PrimaryPresentationFragment requires its ParentFragment being a PresentationFragment");
        }

        return ((PresentationFragment) fragment).getAppRootView();
    }

    @Override
    protected void onCreatePresentationStyleProvider() {
        PresentationStyleProvider provider = getPresentationStyleProvider();
        provider.addStyle(new PrimaryPresentationPresentationStyle(getAppRootView(), new FullscreenStyleAttribute()));
    }

    static final class PrimaryPresentationPresentationStyle extends FullscreenPresentationStyle {
        private final View mPrimaryHostView;

        public PrimaryPresentationPresentationStyle(@NonNull ViewGroup appRootView, @Nullable FullscreenStyleAttribute attribute) {
            super(appRootView, attribute);
            final int count = appRootView.getChildCount();
            View primaryHostView = null;

            // find primary host view in current app root view
            for (int i = 0; i < count; i++) {
                View view = appRootView.getChildAt(i);
                Object id = view.getTag(R.id.content_view_container_id);
                if (id == null || (id instanceof Integer && ((Integer) id) == R.id.content_view_container_id_primary)) {
                    if (id == null) {
                        view.setTag(R.id.content_view_container_id, R.id.content_view_container_id_primary);
                    }

                    primaryHostView = view;
                    break;
                }
            }

            mPrimaryHostView = primaryHostView;

        }

        @Override
        protected View getTransitView() {
            return mPrimaryHostView;
        }

        @Override
        public ViewGroup onCreateHostView(Context context) {
            return null;
        }
    }
}
