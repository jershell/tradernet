package ru.tradernet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.Navigation

class MainActivity : AppCompatActivity() {
    lateinit var navController: NavController
    lateinit var loadingOverlay: View
    lateinit var actionToolBar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        loadingOverlay = findViewById<View>(R.id.loading_overlay)
        actionToolBar = findViewById(R.id.action_bar)
    }

    fun showLoadingOverlay() {
        if (loadingOverlay.visibility == View.GONE) {
            loadingOverlay.visibility = View.VISIBLE
            loadingOverlay.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in))
        }
    }

    fun hideLoadingOverlay() {
        if (loadingOverlay.visibility == View.VISIBLE) {
            loadingOverlay.visibility = View.GONE
            loadingOverlay.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out))
        }
    }
}
