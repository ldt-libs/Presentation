package com.dtrung98.presentationsample

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

import com.dtrung98.insetsview.ext.setUpLightSystemUIVisibility
import com.dtrung98.presentation.fullscreen.FullscreenStyleAttribute

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setUpLightSystemUIVisibility()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }

        findViewById<View>(R.id.presentButton)?.setOnClickListener {
            ArtistPresentationFragment().apply {
                drawerStyleAttribute.leftOverMarginDp = 64
                //isAdaptivePresentation = false
               // preferredPresentationStyle = "fullscreen"

                fullscreenStyleAttribute.animation = FullscreenStyleAttribute.ANIMATION_SLIDE_HORIZONTAL
            }.show(supportFragmentManager, "artist-fragment")
        }
    }
}