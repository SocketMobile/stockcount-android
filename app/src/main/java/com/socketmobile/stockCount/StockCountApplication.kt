/**  Copyright © 2018 Socket Mobile, Inc. */

package com.socketmobile.stockCount

import android.app.Application
import android.content.ComponentName
import android.content.Intent
import android.os.Environment
import com.crashlytics.android.Crashlytics
import com.socketmobile.capture.android.Capture
import com.socketmobile.stockCount.BuildConfig
import io.fabric.sdk.android.Fabric
import io.realm.Realm
import java.io.File
import java.io.IOException



class StockCountApplication: Application() {
    val captureServicePackage = "com.socketmobile.companion"
    val captureServiceStartCmd = "com.socketmobile.capture.StartService"

    override fun onCreate() {
        super.onCreate()
        Realm.init(applicationContext)
        Fabric.with(this, Crashlytics())

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

        startCaptureService()
    }


    private fun isExternalStorageWritable(): Boolean {
        val state = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == state
    }

    private fun startCaptureService() {
        val intent = Intent("com.socketmobile.capture.START_SERVICE")
        intent.component = ComponentName(captureServicePackage, captureServiceStartCmd)
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND)
        sendBroadcast(intent)
    }
}