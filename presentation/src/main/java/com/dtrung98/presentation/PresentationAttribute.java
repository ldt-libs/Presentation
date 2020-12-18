package com.dtrung98.presentation;

import android.os.Bundle;

import androidx.annotation.NonNull;

/**
 * {@link PresentationAttribute} associates with its {@link PresentationStyle}, configure and saves its state data.
 * Attribute is used to store necessary data/configurations
 *  which need to survive after configuration changes or presentation style changes
 * <p>An Attribute is saved/restored automatically by {@link PresentationFragment}</p>
 */
public class PresentationAttribute {
    @NonNull
    public Bundle onSaveInstanceState() {
        return new Bundle();
    }

    public void onRestoreInstanceState(@NonNull Bundle bundle) {}
}
