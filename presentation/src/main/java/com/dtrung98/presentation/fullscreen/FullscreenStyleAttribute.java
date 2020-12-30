package com.dtrung98.presentation.fullscreen;

import android.os.Bundle;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

import com.dtrung98.presentation.PresentationAttribute;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The attribute used to configure {@link FullscreenPresentationStyle}
 */
public class FullscreenStyleAttribute extends PresentationAttribute {
    public static final int ANIMATION_NONE = -1;
    public static final int ANIMATION_SLIDE_VERTICAL = 0;
    public static final int ANIMATION_SLIDE_HORIZONTAL = 1;
    public static final int ANIMATION_FADE = 2;
    public static final String FULLSCREEN_STYLE_ANIMATION = "FullscreenStyle:animation";

    @Animation
    public int getAnimation() {
        return mAnimation;
    }

    public void setAnimation(@Animation int mAnimation) {
        this.mAnimation = mAnimation;
    }

    @IntDef(value = {ANIMATION_NONE, ANIMATION_SLIDE_VERTICAL, ANIMATION_SLIDE_HORIZONTAL, ANIMATION_FADE},
            flag = false)
    @Retention(RetentionPolicy.SOURCE)
    private @interface Animation {
    }

    private @Animation
    int mAnimation = ANIMATION_SLIDE_VERTICAL;

    @NonNull
    @Override
    public Bundle onSaveInstanceState() {
        Bundle bundle = super.onSaveInstanceState();
        bundle.putInt(FULLSCREEN_STYLE_ANIMATION, mAnimation);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle bundle) {
        mAnimation = bundle.getInt(FULLSCREEN_STYLE_ANIMATION, mAnimation);
    }
}
