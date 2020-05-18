package com.prush.justanotherplayer.ui.splash

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.prush.justanotherplayer.R
import com.prush.justanotherplayer.ui.main.MainActivity
import com.prush.justanotherplayer.ui.walkthrough.WalkThroughActivity

const val KEY_FIRST_INSTANCE = "firstInstance"
private const val SPLASH_DURATION = 800L

class SplashActivity : AppCompatActivity(), SplashActivityView {


    override fun gotoMainActivity() {
        Handler().postDelayed({

            val sharedPreferences =
                getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE)

            if (sharedPreferences.getBoolean(KEY_FIRST_INSTANCE, true)) {

                val intent = Intent(this, WalkThroughActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            finish()

        }, SPLASH_DURATION)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val splashActivityPresenter = SplashActivityPresenter(this)
        splashActivityPresenter.gotoMainActivity()

    }
}
