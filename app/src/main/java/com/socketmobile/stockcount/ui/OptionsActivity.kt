/**  Copyright © 2018 Socket Mobile, Inc. */

package com.socketmobile.stockcount.ui

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.socketmobile.stockcount.R
import com.socketmobile.stockcount.helper.ChinaLink
import com.socketmobile.stockcount.helper.getRefersionKey
import com.socketmobile.stockcount.model.Affiliate
import com.socketmobile.stockcount.service.RefersionService
import kotlinx.android.synthetic.main.activity_options.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class OptionsActivity : AppCompatActivity() {
    val TAG = OptionsActivity::class.java.name

    val retrofit = Retrofit.Builder()
            .baseUrl("https://www.refersion.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    val refersionService = retrofit.create(RefersionService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_options)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var packageInfo = applicationContext.packageManager.getPackageInfo(packageName, 0)
        var versionInfo = "${getString(R.string.ver_info)} ${packageInfo.versionName}.${packageInfo.versionCode}"
        verInfoTextView.text = versionInfo
        scanSettingsLayout.setOnClickListener {
            startActivity(Intent(this, ScanSettingsActivity::class.java))
        }
        getStartedLayout.setOnClickListener {
            val i = Intent(this, InstructionActivity::class.java)
            i.putExtra("fromOptions", true)
            startActivity(i)
        }
        captureSdkTextView.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.sdk_github_url)))
            startActivity(i)
        }
        buyScannerButton.setOnClickListener {
            val progressDialog = ProgressDialog(this)
            progressDialog.show()
            val country = Locale.getDefault().country

            if (country == Locale.CHINA.country) { // special case check for china, because there's no china link on refersion
                openLink(ChinaLink)
                return@setOnClickListener
            }

            val keyObj = getRefersionKey(Locale.getDefault().country)
            val param = hashMapOf(
                    "refersion_public_key" to keyObj.pub_key,
                    "refersion_secret_key" to keyObj.sec_key,
                    "affiliate_code" to keyObj.aff_code
            )
            val call = refersionService.getAffiliate(param)
            call.enqueue(object: Callback<Affiliate> {
                override fun onFailure(call: Call<Affiliate>, t: Throwable) {
                    progressDialog.hide()
                    AlertDialog.Builder(this@OptionsActivity)
                            .setMessage(t.message)
                            .show()
                }

                override fun onResponse(call: Call<Affiliate>, response: Response<Affiliate>) {
                    progressDialog.hide()
                    val link = response.body()?.link
                    if (!link.isNullOrEmpty()) {
                        Log.d(TAG, link)
                        openLink(link)
                    }
                }

            })
        }
    }

    fun openLink(link: String) {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(link)
        startActivity(i)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
