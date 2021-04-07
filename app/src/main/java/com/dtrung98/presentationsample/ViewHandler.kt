package com.dtrung98.presentationsample

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import com.dtrung98.presentation.PresentationFragment

class ViewHandler : View.OnClickListener {
    companion object {
        const val EXTRA_SCREEN = "screen"
        const val EXTRA_ADAPTIVE = "adaptive"
        const val EXTRA_STYLE = "style"
    }
    val screens = mutableListOf<View>()
    val adaptivities = mutableListOf<View>()
    val presentationStyles = mutableListOf<View>()
    lateinit var fragmentManager: FragmentManager

    data class PresentInfo(var screen: Int, var adaptive: Int, var style: Int)

    val presentInfo = PresentInfo(-1, 0, 0)

    fun init(view: View, fragmentManager: FragmentManager, argument: Bundle? = null) {
        this.fragmentManager = fragmentManager
        argument?.also {
            this.presentInfo.screen = it.getInt(EXTRA_SCREEN)
            this.presentInfo.adaptive = it.getInt(EXTRA_ADAPTIVE)
            this.presentInfo.style = it.getInt(EXTRA_STYLE)
        }

        // which screens to display
        screens.add(view.findViewById(R.id.screen1))
        screens.add(view.findViewById(R.id.screen2))

        // adaptive presentation or not
        adaptivities.add(view.findViewById(R.id.adaptiveTextView))
        adaptivities.add(view.findViewById(R.id.nonAdaptiveTextView))

        presentationStyles.add(view.findViewById(R.id.style1))
        presentationStyles.add(view.findViewById(R.id.style2))
        presentationStyles.add(view.findViewById(R.id.style3))
        presentationStyles.add(view.findViewById(R.id.style4))


        if (this.presentInfo.screen == 0 || this.presentInfo.screen == 1) {
            screens[this.presentInfo.screen].isSelected = true
        }

        adaptivities[this.presentInfo.adaptive].isSelected = true
        presentationStyles[this.presentInfo.style].isSelected = true

        screens.forEach { it.setOnClickListener(this) }
        adaptivities.forEach { it.setOnClickListener(this) }
        presentationStyles.forEach { it.setOnClickListener(this) }

        view.findViewById<View>(R.id.presentButton).setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.screen1 -> {
                v.isSelected = !v.isSelected
                screens[1].isSelected = false
                presentInfo.screen = if (v.isSelected) 0 else -1
            }

            R.id.screen2 -> {
                v.isSelected = !v.isSelected
                screens[0].isSelected = false
                presentInfo.screen = if (v.isSelected) 0 else -1
            }

            R.id.adaptiveTextView -> {
                v.isSelected = true
                adaptivities[1].isSelected = false
                presentInfo.adaptive = 0
            }

            R.id.nonAdaptiveTextView -> {
                v.isSelected = true
                adaptivities[0].isSelected = false
                presentInfo.adaptive = 1
            }

            R.id.style1 -> {
                v.isSelected = true
                presentationStyles[1].isSelected = false
                presentationStyles[2].isSelected = false
                presentationStyles[3].isSelected = false
                presentInfo.style = 0
            }

            R.id.style2 -> {
                v.isSelected = true
                presentationStyles[0].isSelected = false
                presentationStyles[2].isSelected = false
                presentationStyles[3].isSelected = false
                presentInfo.style = 1
            }

            R.id.style3 -> {
                v.isSelected = true
                presentationStyles[0].isSelected = false
                presentationStyles[1].isSelected = false
                presentationStyles[3].isSelected = false
                presentInfo.style = 2
            }

            R.id.style4 -> {
                v.isSelected = true
                presentationStyles[0].isSelected = false
                presentationStyles[1].isSelected = false
                presentationStyles[2].isSelected = false
                presentInfo.style = 3
            }

            R.id.presentButton -> present()
        }
    }

    fun present() {
        val fragment: PresentationFragment = if (presentInfo.screen == 0) ArtistPresentationFragment() else ArtistPresentationFragment()

        fragment.isAdaptivePresentation = presentInfo.adaptive == 1
        fragment.apply {
            when (presentInfo.style) {
                0 -> {
                    // fullscreen
                    preferredPresentationStyle = "fullscreen"
                }

                1 -> {
                    preferredPresentationStyle = "drawer"
                }

                2 -> {
                    preferredPresentationStyle = "drawer"
                    drawerStyleAttribute.leftOverMarginDp = 0
                }

                3 -> {
                    preferredPresentationStyle = "slider"
                }
            }
        }

        fragment.arguments = bundleOf(EXTRA_SCREEN to presentInfo.screen, EXTRA_ADAPTIVE to presentInfo.adaptive, EXTRA_STYLE to presentInfo.style)

        fragment.show(fragmentManager, "artist-fragment-${View.generateViewId()}")
    }
}