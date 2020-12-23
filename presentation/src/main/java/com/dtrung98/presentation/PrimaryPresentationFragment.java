package com.dtrung98.presentation;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dtrung98.presentation.fullscreen.FullscreenStyle;
import com.dtrung98.presentation.fullscreen.FullscreenStyleAttribute;

public class PrimaryPresentationFragment extends PresentationFragment {
    public PrimaryPresentationFragment() {
        super();
        setAdaptivePresentation(false);
        setPreferredPresentationStyle("fullscreen");
        setBackPressedCallbackEnabled(false);
    }

    @Override
    protected void onCreatePresentationStyleProvider() {
        PresentationStyleProvider provider = getPresentationStyleProvider();
        provider.addStyle(new PrimaryPresentationStyle(getAppRootView(), new FullscreenStyleAttribute()));
    }

    static class PrimaryPresentationStyle extends FullscreenStyle {
        private View mPrimaryHostView;

       /* @Override
        public int getId() {
            return R.id.content_view_container_id_primary;
        }*/

        public PrimaryPresentationStyle(@NonNull ViewGroup appRootView, @Nullable FullscreenStyleAttribute attribute) {
            super(appRootView, attribute);
            final int count = appRootView.getChildCount();
            View primaryHostView = null;
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

            appRootView.setTag(R.id.content_view_container_id_primary, true);
        }

        @Override
        protected View getTransitView() {
            return mPrimaryHostView;
        }

        @Override
        public void dismissImmediately() {
            super.dismissImmediately();
            getAppRootView().setTag(R.id.content_view_container_id_primary, false);
        }

        @Override
        protected void initContainer() {
            super.initContainer();

        }

        @Override
        public ViewGroup onCreateHostView(Context context) {
            return null;
        }
    }
}
