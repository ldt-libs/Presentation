package com.dtrung98.presentationsample

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import com.dtrung98.insetsview.ext.setUpLightSystemUIVisibility

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setUpLightSystemUIVisibility()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }

        //PrimaryPresentationFragment().show(supportFragmentManager, "primary-presentation-fragment")
        ViewHandler().init(findViewById(android.R.id.content), supportFragmentManager)
    }
}