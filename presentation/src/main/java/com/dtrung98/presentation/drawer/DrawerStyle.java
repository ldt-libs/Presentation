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
import androidx.lifecycle.Observer;

import com.dtrung98.presentation.PresentationAttribute;
import com.dtrung98.presentation.PresentationLayerEntry;
import com.dtrung98.presentation.PresentationStyle;

public class DrawerStyle extends PresentationStyle {
    private final static int DURATION = 475;
    private final static int DIM_AMOUNT = 85;
    private TimeInterpolator mTimeInterpolator = new FastOutSlowInInterpolator();
    private final float mDensity;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private float mCardHeight = 0;
    private ViewGroup mWrapContentView;

    private boolean mDismissing = false;


    @Override
    public String getName() {
        return "drawer";
    }

    public DrawerStyle(@NonNull ViewGroup appRootView, @Nullable DrawerStyleAttribute attribute) {
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
                findPresentationLayersController().updateForegroundLayerFraction(slideOffset);

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
                DrawerStyle.super.dismiss();
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
        PresentationLayerEntry entry = findPresentationLayersController().getCurrentPresentationLayerEntry();
        entry.setFlag(PresentationLayerEntry.FLAG_REQUIRE_PREVIOUS_LAYER_SLIDE_HORIZONTAL, true);

        entry.getBackgroundFractionLiveData().observe((LifecycleOwner) getAppRootView().getContext(), fraction -> {
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

  /*  @Override
    public void hide() {
        super.hide();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                View hostView = getHostView();
                if (hostView instanceof DrawerLayout) {
                    ((DrawerLayout) hostView).closeDrawer(Gravity.RIGHT);
                }
            }
        });
    }*/


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

   /* @Override
    public ViewGroup onCreateViewContainer(@Nullable Bundle savedInstanceState) {
        ViewGroup appRootView = getAppRootView();

        WindowInsetsCompat wic = ViewCompat.getRootWindowInsets(appRootView);
        int parentHeight = appRootView.getHeight();
        int topInset = wic == null ? 0 : wic.getSystemWindowInsetTop();
        topInset += 14f * appRootView.getResources().getDimension(R.dimen.oneDP);

        CardLayerContainerView containerView = new CardLayerContainerView(appRootView.getContext());
        containerView.setId(mContainerWrapperId);
        containerView.setRadius(12 * getResources().getDimension(R.dimen.oneDP));

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, parentHeight - topInset);
        params.topMargin = topInset;

        appRootView.addView(containerView, params);
        int count = appRootView.getChildCount();
        View viewBehind = appRootView.getChildAt(count - 2);

        RoundRectDrawable background;
        final float radius = 16 * viewBehind.getResources().getDimension(R.dimen.oneDP);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final Drawable preBackground = viewBehind.getBackground();
            if (!(preBackground instanceof RoundRectDrawable)) {
                background = new RoundRectDrawable(ColorStateList.valueOf(0),
                        0);
                viewBehind.setBackground(background);
            } else {
                background = (RoundRectDrawable) preBackground;
            }

            viewBehind.setClipToOutline(true);
            viewBehind.setElevation(0);//4 * viewBehind.getResources().getDimension(R.dimen.oneDP));

            final RoundRectDrawable bg = background;
            ValueAnimator roundVA = ValueAnimator.ofFloat(0, 1).setDuration(DURATION);
            roundVA.setInterpolator(mTimeInterpolator);
            roundVA.addUpdateListener(animation -> bg.setRadius((float) animation.getAnimatedValue() * radius));

            roundVA.start();
        }
        viewBehind.animate().scaleX(0.92f).scaleY(0.92f).setDuration(DURATION).setInterpolator(mTimeInterpolator).start();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 && mDimDrawable == null) {
            final ColorDrawable drawable = new ColorDrawable(Color.BLACK);
            mDimDrawable = drawable;
            drawable.setAlpha(0);
            drawable.setBounds(0, 0, viewBehind.getWidth(), viewBehind.getHeight());
            viewBehind.getOverlay().add(drawable);
            ValueAnimator overlayVA = ValueAnimator.ofInt(0, DIM_AMOUNT);
            overlayVA.setDuration(DURATION);
            overlayVA.addUpdateListener(animation -> drawable.setAlpha((int) animation.getAnimatedValue()));
            overlayVA.start();
        }
        containerView.setTranslationY(parentHeight - topInset);

        containerView.animate().translationY(0).setDuration(DURATION).setInterpolator(mTimeInterpolator).start();
        return containerView.getSubContainerView();
    }

    private boolean mOnDismissing = false;
    private ColorDrawable mDimDrawable = null;

    @Override
    protected void onDismissContentViewContainer() {
        if (mOnDismissing) {
            return;
        }
        mOnDismissing = true;
        ViewGroup appRootView = getAppRootView();

        View containerWrapper = appRootView.findViewById(mContainerWrapperId);
        containerWrapper.animate()
                .translationY(containerWrapper.getHeight())
                .setInterpolator(new FastOutSlowInInterpolator())
                .setDuration(DURATION)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        appRootView.removeView(containerWrapper);
                        mOnDismissing = false;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                })
                .start();

        int count = appRootView.getChildCount();
        View viewBehind = appRootView.getChildAt(0);
        RoundRectDrawable background = null;
        final float radius = 16 * viewBehind.getResources().getDimension(R.dimen.oneDP);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            final Drawable preBackground = viewBehind.getBackground();
            if (!(preBackground instanceof RoundRectDrawable)) {
                background = new RoundRectDrawable(ColorStateList.valueOf(0),
                        0);
                viewBehind.setBackground(background);
            } else {
                background = (RoundRectDrawable) preBackground;
            }

            viewBehind.setClipToOutline(true);
            viewBehind.setElevation(0);//4 * viewBehind.getResources().getDimension(R.dimen.oneDP));

            final RoundRectDrawable bg = background;
            ValueAnimator roundVA = ValueAnimator.ofFloat(1, 0).setDuration(DURATION);
            roundVA.setInterpolator(mTimeInterpolator);
            roundVA.addUpdateListener(animation -> bg.setRadius((float) animation.getAnimatedValue() * radius));

            roundVA.start();
        }


        viewBehind.animate().scaleX(1f).scaleY(1f).setDuration(DURATION).setInterpolator(mTimeInterpolator).start();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 && mDimDrawable != null) {
            final ColorDrawable drawable = mDimDrawable;
            mDimDrawable = null;
            drawable.setAlpha(DIM_AMOUNT);
            ValueAnimator overlayVA = ValueAnimator.ofInt(DIM_AMOUNT, 0);
            overlayVA.setDuration(DURATION);
            overlayVA.addUpdateListener(animation -> drawable.setAlpha((int) animation.getAnimatedValue()));
            overlayVA.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    viewBehind.getOverlay().remove(drawable);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            overlayVA.start();
        }

    }*/


}
