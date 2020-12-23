package com.dtrung98.presentation.fullscreen;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.Interpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.PathParser;
import androidx.core.view.animation.PathInterpolatorCompat;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.lifecycle.LifecycleOwner;

import com.dtrung98.presentation.PresentationAttribute;
import com.dtrung98.presentation.PresentationLayerEntry;
import com.dtrung98.presentation.PresentationStyle;
import com.dtrung98.presentation.widget.RoundRectDrawable;

import static android.os.Build.VERSION_CODES.R;

/**
 * A style which always presents in a fullscreen frame.
 */
public class FullscreenStyle extends PresentationStyle {

    @Override
    public void setContentView(View view) {
        if (getHostView() != null) {
            getHostView().addView(view);
        }
    }

    public FullscreenStyle(@NonNull ViewGroup appRootView, @Nullable FullscreenStyleAttribute attribute) {
        super(appRootView, attribute);
    }

    protected View getTransitView() {
        return getHostView();
    }

    public void doShowAnimation() {
        final View transitView = getHostView();
        if (transitView == null || requireAttribute().getAnimation() == FullscreenStyleAttribute.ANIMATION_NONE) {
            return;
        }

        final int width = getAppRootView().getWidth();
        final int height = getAppRootView().getHeight();

        final ViewPropertyAnimator animator = transitView.animate();
        switch (requireAttribute().getAnimation()) {
            case FullscreenStyleAttribute.ANIMATION_NONE:
                break;
            case FullscreenStyleAttribute.ANIMATION_SLIDE_HORIZONTAL:
                transitView.setTranslationX(width);
                animator.translationX(0).setInterpolator(new FastOutSlowInInterpolator()).setDuration(450);
                break;
            case FullscreenStyleAttribute.ANIMATION_FADE:
                transitView.setAlpha(0);
                animator.alpha(1).setDuration(350);
                break;
            case FullscreenStyleAttribute.ANIMATION_SLIDE_VERTICAL:
            default:
                transitView.setTranslationY(height);
                transitView.animate().translationY(0).setInterpolator(new FastOutSlowInInterpolator()).setDuration(450);
                break;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            animator.setUpdateListener(animation -> findPresentationLayersController().updateForegroundLayerFraction((Float) animation.getAnimatedValue()));
        }
        animator.start();
    }

    @Override
    public boolean show() {
        if (super.show()) {
            doShowAnimation();
            return true;
        }
        return false;
    }

    private float mDensity = 1;

    @Override
    protected void initContainer() {
        super.initContainer();
        PresentationLayerEntry entry = findPresentationLayersController().getPresentationEntry(getId());
        entry.setFlag(PresentationLayerEntry.FLAG_REQUIRE_PREVIOUS_LAYER_SLIDE_HORIZONTAL, true);

        getAppRootView().setBackgroundColor(Color.BLACK);
        final View transitView = getTransitView();
        if (transitView != null) {
            RoundRectDrawable background;
            mDensity = getAppRootView().getResources().getDisplayMetrics().density;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                final Drawable preBackground = transitView.getBackground();
                if (!(preBackground instanceof RoundRectDrawable)) {
                    background = new RoundRectDrawable(ColorStateList.valueOf(Color.WHITE),
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

        entry.getBackgroundFractionLiveData().observe((LifecycleOwner) getAppRootView().getContext(), fraction -> {
            doBackgroundCardPresentationTransit(fraction);
           /* if (transitView != null) {
                transitView.setTranslationX(-transitView.getWidth() / 3f * fraction);
            }*/
        });
    }

    private void doBackgroundCardPresentationTransit(float fraction) {
        View transitView = getTransitView();
        Drawable background = transitView != null ? transitView.getBackground() : null;
        if (transitView != null) {
            transitView.setScaleX(1 - 0.1f * fraction);
            transitView.setScaleY(1 - 0.1f * fraction);
        }

        if (background instanceof RoundRectDrawable && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ((RoundRectDrawable) background).setRadius(16 * mDensity * (fraction < 0.1 ? 0 : fraction > 0.9 ? 1 : fraction));
        }
    }

    private Interpolator fastOutExtraSlowIn() {
        return PathInterpolatorCompat.create(PathParser.createPathFromPathData("M 0,0 C 0.05, 0, 0.133333, 0.06, 0.166666, 0.4 C 0.208333, 0.82, 0.25, 1, 1, 1"));
    }

    private boolean mDismissing = false;

    public void dismissImmediately() {
        super.dismiss();
    }

    @Override
    public void dismiss() {
        if (mDismissing) {
            return;
        }

        final View hostView = getHostView();
        if (hostView == null || requireAttribute().getAnimation() == FullscreenStyleAttribute.ANIMATION_NONE) {
            dismissImmediately();
            return;
        }

        mDismissing = true;

        final int width = getAppRootView().getWidth();
        final int height = getAppRootView().getHeight();
        final View transitView = hostView;

        final ViewPropertyAnimator animator = transitView.animate();
        switch (requireAttribute().getAnimation()) {
            case FullscreenStyleAttribute.ANIMATION_SLIDE_HORIZONTAL:
                animator.translationX(width).setInterpolator(new FastOutSlowInInterpolator()).setDuration(450);
                break;
            case FullscreenStyleAttribute.ANIMATION_FADE:
                animator.alpha(0).setDuration(350);
                break;
            case FullscreenStyleAttribute.ANIMATION_SLIDE_VERTICAL:
            default:
                hostView.animate().translationY(height).setInterpolator(new FastOutSlowInInterpolator()).setDuration(350);
                break;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            animator.setUpdateListener(animation -> findPresentationLayersController().updateForegroundLayerFraction(1 - animation.getAnimatedFraction()));
        }
        animator.setListener(new SimpleAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mDismissing = false;
                dismissImmediately();
            }
        }).start();
    }

    @NonNull
    @Override
    public String getPresentationType() {
        return "fullscreen";
    }

    public final FullscreenStyleAttribute requireAttribute() {
        PresentationAttribute attribute = getAttribute();
        if (!(attribute instanceof FullscreenStyleAttribute)) {
            throw new IllegalStateException("The attribute used in this style is not a DrawerStyleAttribute");
        }
        return (FullscreenStyleAttribute) attribute;
    }
}
