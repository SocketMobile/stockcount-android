package com.socketmobile.stockcount

import android.app.Application
import android.os.Environment
import io.realm.Realm
import java.io.File
import java.io.IOException

class StockCountApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(applicationContext)

        if (isExternalStorageWritable()) {
            var appDir: File? = null
            appDir = File(Environment.getExternalStorageDirectory(), "StockCountLog")
            val logDir = File(appDir, "log")
            val logFile = File(logDir, "logcat" + System.currentTimeMillis() + ".txt")

            if (!appDir.exists()) {
                appDir.mkdir()
            }
            if (!logDir.exists()) {
                logDir.mkdir()
            }
            try {
                var proc = Runtime.getRuntime().exec("logcat -c")
                proc = Runtime.getRuntime().exec("logcat -f $logFile")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


    private fun isExternalStorageWritable(): Boolean {
        val state = Environment.getExternalStorageState()
        return if (Environment.MEDIA_MOUNTED == state) {
            true
        } else false
    }
}