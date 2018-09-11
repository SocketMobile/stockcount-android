/**  Copyright © 2018 Socket Mobile, Inc. */
package com.socketmobile.stockcount.ui

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.socketmobile.stockcount.R
import kotlinx.android.synthetic.main.activity_options.*

class OptionsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_options)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        scanSettingsLayout.setOnClickListener {
            startActivity(Intent(this, ScanSettingsActivity::class.java))
        }
        getStartedLayout.setOnClickListener {
            val i = Intent(this, InstructionActivity::class.java)
            i.putExtra("fromOptions", true)
            startActivity(i)
        }
        captureSdkTextView.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/SocketMobiles"))
            startActivity(i)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
