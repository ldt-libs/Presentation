package com.dtrung98.presentation.slider;

import android.animation.TimeInterpolator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
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
import com.dtrung98.presentation.widget.RoundRectDrawable;

public class SliderPresentationStyle extends PresentationStyle {
    private final static int DURATION = 475;
    private final static int DIM_AMOUNT = 85;
    private TimeInterpolator mTimeInterpolator = new FastOutSlowInInterpolator();
    private final float mDensity;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private ViewGroup mWrapContentView;
    private boolean mDismissing = false;


    @NonNull
    @Override
    public String getName() {
        return "slider";
    }

    public SliderPresentationStyle(@NonNull ViewGroup appRootView, @Nullable SliderStyleAttribute attribute) {
        super(appRootView, attribute);
        mDensity = appRootView.getResources().getDisplayMetrics().density;
    }

    public final SliderStyleAttribute requireAttribute() {
        PresentationAttribute attribute = getAttribute();
        if (!(attribute instanceof SliderStyleAttribute)) {
            throw new IllegalStateException("The attribute used in this style (" + attribute + ") is not a SlideStyleAttribute");
        }
        return (SliderStyleAttribute) attribute;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public ViewGroup onCreateHostView(Context context) {
        WindowInsetsCompat wic = ViewCompat.getRootWindowInsets(getAppRootView());
        int parentHeight = getAppRootView().getHeight();
        int topInset = wic == null ? 0 : wic.getSystemWindowInsetTop();
        topInset += 14f * mDensity;

        FrameLayout layerView = new FrameLayout(context);
        SliderLayout.LayoutParams params = new SliderLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.BOTTOM;
        layerView.setLayoutParams(params);

        View drawerContentView = new View(context);
        SliderLayout.LayoutParams dcvParams = new SliderLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dcvParams.gravity = Gravity.NO_GRAVITY;
        drawerContentView.setLayoutParams(dcvParams);

        SliderLayout drawerLayout = new SliderLayout(context);
        drawerLayout.MIN_DRAWER_MARGIN = requireAttribute().getLeftOverMarginDp();
        FrameLayout.LayoutParams dlParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        drawerLayout.setLayoutParams(dlParams);
        drawerLayout.addView(drawerContentView);
        drawerLayout.addView(layerView);
        drawerLayout.addDrawerListener(new SliderLayout.DrawerListener() {

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
                SliderPresentationStyle.super.dismiss();
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

        getAppRootView().setBackgroundColor(Color.BLACK);
        final View transitView = getHostView();
        if (transitView != null) {
            RoundRectDrawable background;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                final Drawable preBackground = transitView.getBackground();
                if (!(preBackground instanceof RoundRectDrawable)) {
                    background = new RoundRectDrawable(ColorStateList.valueOf(Color.TRANSPARENT),
                            0);
                    transitView.setBackground(background);
                } else {
                    background = (RoundRectDrawable) preBackground;
                }

                transitView.setClipToOutline(true);
                transitView.setElevation(0);//4 * viewBehind.getResources().getDimension(R.dimen.oneDP));

                final RoundRectDrawable bg = background;
            }
        }

        entry.getUnderlyingFractionLiveData().observe((LifecycleOwner) getAppRootView().getContext(), fraction -> {
            /*View host = getHostView();
            if (host != null) {
                host.setTranslationX(-host.getWidth() / 3f * fraction);
            }*/
            doBackgroundCardPresentationTransit(fraction);
        });
    }

    private void doBackgroundCardPresentationTransit(float fraction) {
        View transitView = getHostView();
        Drawable background = transitView != null ? transitView.getBackground() : null;
        if (transitView != null) {
            transitView.setScaleX(1 - 0.1f * fraction);
            transitView.setScaleY(1 - 0.1f * fraction);
        }

        if (background instanceof RoundRectDrawable && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ((RoundRectDrawable) background).setRadius(16 * mDensity * (fraction < 0.1 ? 0 : fraction > 0.9 ? 1 : fraction));
        }
    }

    @Override
    public boolean show() {
        if (super.show()) {
            mHandler.post(() -> {
                View hostView = getHostView();
                if (hostView instanceof SliderLayout) {
                    ((SliderLayout) hostView).openDrawer();
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
        if (hostView instanceof SliderLayout) {
            SliderLayout drawerLayout = (SliderLayout) hostView;
            drawerLayout.closeDrawer();
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
