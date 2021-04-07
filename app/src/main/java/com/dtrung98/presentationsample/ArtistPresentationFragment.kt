package com.dtrung98.presentationsample

import android.os.Bundle
import android.view.View
import com.dtrung98.presentation.PresentationFragment

open class ArtistPresentationFragment : PresentationFragment(R.layout.activity_main) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ViewHandler().init(view, childFragmentManager, arguments)
    }
}