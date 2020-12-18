package com.dtrung98.presentation.fullscreen;

import android.animation.Animator;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.Interpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.PathParser;
import androidx.core.view.animation.PathInterpolatorCompat;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import com.dtrung98.presentation.PresentationAttribute;
import com.dtrung98.presentation.PresentationStyle;

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

    public void doShowAnimation() {
        final View hostView = getHostView();
        if (hostView == null || requireAttribute().getAnimation() == FullscreenStyleAttribute.ANIMATION_NONE) {
            return;
        }

        final int width = getAppRootView().getWidth();
        final int height = getAppRootView().getHeight();

        final ViewPropertyAnimator animator = hostView.animate();
        switch (requireAttribute().getAnimation()) {
            case FullscreenStyleAttribute.ANIMATION_SLIDE_HORIZONTAL:
                hostView.setTranslationX(width);
                animator.translationX(0).setInterpolator(new FastOutSlowInInterpolator()).setDuration(450);
                break;
            case FullscreenStyleAttribute.ANIMATION_FADE:
                hostView.setAlpha(0);
                animator.alpha(1).setDuration(350);
                break;
            case FullscreenStyleAttribute.ANIMATION_SLIDE_VERTICAL:
            default:
                hostView.setTranslationY(height);
                hostView.animate().translationY(0).setInterpolator(new FastOutSlowInInterpolator()).setDuration(450);
                break;
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

        final ViewPropertyAnimator animator = hostView.animate();
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
    public String getName() {
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
