package com.dtrung98.presentation.drawer;

import android.animation.TimeInterpolator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.lifecycle.LifecycleOwner;

import com.dtrung98.presentation.PresentationAttribute;
import com.dtrung98.presentation.PresentationEntry;
import com.dtrung98.presentation.PresentationStyle;

public class DrawerPresentationStyle extends PresentationStyle {
    private final static int DURATION = 475;
    private final static int DIM_AMOUNT = 85;
    private TimeInterpolator mTimeInterpolator = new FastOutSlowInInterpolator();
    private final float mDensity;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private float mCardHeight = 0;
    private ViewGroup mWrapContentView;

    private boolean mDismissing = false;


    @NonNull
    @Override
    public String getName() {
        return "drawer";
    }

    public DrawerPresentationStyle(@NonNull ViewGroup appRootView, @Nullable DrawerStyleAttribute attribute) {
        super(appRootView, attribute);
        mDensity = appRootView.getResources().getDisplayMetrics().density;
    }

    public final DrawerStyleAttribute requireAttribute() {
        PresentationAttribute attribute = getAttribute();
        if (!(attribute instanceof DrawerStyleAttribute)) {
            throw new IllegalStateException("The attribute used in this style (" + attribute + ") is not a DrawerStyleAttribute");
        }
        return (DrawerStyleAttribute) attribute;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public ViewGroup onCreateHostView(Context context) {
        WindowInsetsCompat wic = ViewCompat.getRootWindowInsets(getAppRootView());
        int parentHeight = getAppRootView().getHeight();
        int topInset = wic == null ? 0 : wic.getSystemWindowInsetTop();
        topInset += 14f * mDensity;

        mCardHeight = parentHeight - topInset;
        FrameLayout layerView = new FrameLayout(context);
        DrawerLayout.LayoutParams params = new DrawerLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.RIGHT;
        layerView.setLayoutParams(params);

        View drawerContentView = new View(context);
        DrawerLayout.LayoutParams dcvParams = new DrawerLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dcvParams.gravity = Gravity.NO_GRAVITY;
        drawerContentView.setLayoutParams(dcvParams);

        DrawerLayout drawerLayout = new DrawerLayout(context);
        drawerLayout.MIN_DRAWER_MARGIN = requireAttribute().getLeftOverMarginDp();
        FrameLayout.LayoutParams dlParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        drawerLayout.setLayoutParams(dlParams);
        drawerLayout.addView(drawerContentView);
        drawerLayout.addView(layerView);
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {

            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                Log.d("CardStyle", "onDrawerSlide: " + slideOffset);
                findPresentationLayersController().updatePresentingFraction(slideOffset);

                if (slideOffset <= 0 && !requireAttribute().mOpenCompletely) {
                    /* manual call closed method for first time user dismisses the drawer but it's open completely */
                    onDrawerClosed(drawerView);
                } else if (slideOffset >= 1 && !requireAttribute().mOpenCompletely) {
                    requireAttribute().mOpenCompletely = true;
                }
                requireAttribute().mCloseCompletely = slideOffset <= 0;
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                requireAttribute().mOpenCompletely = true;
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                mDismissing = false;
                requireAttribute().mOpenCompletely = false;
                requireAttribute().mCloseCompletely = true;
                DrawerPresentationStyle.super.dismiss();
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        mWrapContentView = layerView;
        return drawerLayout;
    }

    @Override
    protected void initContainer() {
        super.initContainer();
        PresentationEntry entry = findPresentationLayersController().getPresentationEntry(getId());
        entry.setFlag(PresentationEntry.FLAG_REQUIRE_PREVIOUS_LAYER_SLIDE_HORIZONTAL, true);

        entry.getUnderlyingFractionLiveData().observe((LifecycleOwner) getAppRootView().getContext(), fraction -> {
            View host = getHostView();
            if (host != null) {
                host.setTranslationX(-host.getWidth() / 3f * fraction);
            }
        });
    }

    @Override
    public boolean show() {
        if (super.show()) {
            mHandler.post(() -> {
                View hostView = getHostView();
                if (hostView instanceof DrawerLayout) {
                    ((DrawerLayout) hostView).openDrawer(Gravity.RIGHT);
                }
            });
            return true;
        }
        return false;
    }

    @Override
    public void dismiss() {
        if (requireAttribute().mCloseCompletely) {
            super.dismiss();
            return;
        }
        if (mDismissing) {
            return;
        }
        mDismissing = true;
        View hostView = getHostView();
        if (hostView instanceof DrawerLayout) {
            DrawerLayout drawerLayout = (DrawerLayout) hostView;
            drawerLayout.closeDrawer(Gravity.RIGHT);
        } else {
            super.dismiss();
            mDismissing = false;
        }
    }

    @Override
    public void setContentView(View view) {
        mWrapContentView.addView(view);
    }

}
