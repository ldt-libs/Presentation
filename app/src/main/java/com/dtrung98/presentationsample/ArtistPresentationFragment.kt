package com.dtrung98.presentationsample

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.dtrung98.presentation.PresentationFragment
import com.dtrung98.presentation.fullscreen.FullscreenStyleAttribute

open class ArtistPresentationFragment : PresentationFragment(R.layout.activity_main) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View>(R.id.presentButton)?.setOnClickListener {
            ArtistPresentationFragment().apply {
                drawerStyleAttribute.leftOverMarginDp = 0
                isAdaptivePresentation = false
                preferredPresentationStyle = "drawer"

                //fullscreenStyleAttribute.animation = FullscreenStyleAttribute.ANIMATION_SLIDE_HORIZONTAL
            }.show(childFragmentManager, "artist-fragment")
        }

    }
}