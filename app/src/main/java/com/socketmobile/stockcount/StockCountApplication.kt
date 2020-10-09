/**  Copyright © 2018 Socket Mobile, Inc. */

package com.socketmobile.stockcount

import android.app.Application
import android.os.Environment
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import com.socketmobile.capture.android.Capture
import io.realm.Realm
import java.io.File
import java.io.IOException


class StockCountApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        AppCenter.start(this, BuildConfig.APP_CENTER_KEY,
                    Analytics::class.java, Crashes::class.java)

        Realm.init(applicationContext)

        Capture.builder(applicationContext)
                .enableLogging(BuildConfig.DEBUG)
                .build()

        if (isExternalStorageWritable()) {
            val appDir = File(Environment.getExternalStorageDirectory(), "StockCountLog")
            val logDir = File(appDir, "log")
            val logFile = File(logDir, "logcat" + System.currentTimeMillis() + ".txt")

            if (!appDir.exists()) {
                appDir.mkdir()
            }
            if (!logDir.exists()) {
                logDir.mkdir()
            }
            try {
                Runtime.getRuntime().exec("logcat -c")
                Runtime.getRuntime().exec("logcat -f $logFile")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


    private fun isExternalStorageWritable(): Boolean {
        val state = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == state
    }
}