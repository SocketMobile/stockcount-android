/**  Copyright © 2018 Socket Mobile, Inc. */

package com.socketmobile.stockcount.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.socketmobile.stockcount.R
import com.socketmobile.stockcount.helper.haveToShowInstruction
import java.util.*

class SplashActivity : AppCompatActivity() {
    private lateinit var timer: Timer
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
