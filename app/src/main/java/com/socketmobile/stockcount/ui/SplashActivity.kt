package com.socketmobile.stockcount.ui

import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import com.socketmobile.stockcount.R
import com.socketmobile.stockcount.helper.haveToShowInstruction
import java.util.*

class SplashActivity : AppCompatActivity() {
    lateinit var timer: Timer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        supportActionBar?.hide()
    }

    override fun onResume() {
        super.onResume()

        timer = Timer()
        timer.schedule(object: TimerTask() {
            override fun run() {
                goNext()
            }
        }, 3000)
    }

    override fun onPause() {
        super.onPause()
        timer.cancel()
    }

    fun goNext() {
        if (haveToShowInstruction(this@SplashActivity)) {
            startActivity(Intent(this@SplashActivity, InstructionActivity::class.java))
        } else {
            startActivity(Intent(this@SplashActivity, HomeActivity::class.java))
        }
        finish()
    }
}
