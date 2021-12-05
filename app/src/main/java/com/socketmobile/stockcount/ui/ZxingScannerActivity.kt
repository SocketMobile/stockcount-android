package com.socketmobile.stockcount.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.ViewfinderView
import com.socketmobile.stockcount.R
import java.util.*

class ZxingScannerActivity: AppCompatActivity(), DecoratedBarcodeView.TorchListener  {

    private var capture: CaptureManager? = null
    private var barcodeScannerView: DecoratedBarcodeView? = null
    private var switchFlashlightButton: ImageView? = null
    private var viewfinderView: ViewfinderView? = null

    private var flashOn: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_zxing_scanner)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "Scan Barcode"

        viewfinderView = findViewById(R.id.zxing_viewfinder_view)

        switchFlashlightButton = findViewById(R.id.switch_flashlight)

        barcodeScannerView = findViewById(R.id.zxing_barcode_scanner)
        barcodeScannerView!!.setTorchListener(this)

        if (!hasFlash()) {
            with(switchFlashlightButton) { this!!.visibility = View.GONE }
        }

        capture = CaptureManager(this, barcodeScannerView)
        capture!!.initializeFromIntent(intent, savedInstanceState)
        capture!!.setShowMissingCameraPermissionDialog(true)
        capture!!.decode()

        changeLaserVisibility(true)
    }

    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {

                } else {
                    requestPermissions(arrayOf(Manifest.permission.CAMERA), 176)
                }
            }
        } else {
            capture!!.onResume()
        }
    }

    override fun onPause() {
        super.onPause()
        capture!!.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        capture!!.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        capture!!.onSaveInstanceState(outState)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return barcodeScannerView!!.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event)
    }

    private fun hasFlash(): Boolean {
        return applicationContext.packageManager
            .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
    }

    fun switchFlashlight(view: View?) {
        if (flashOn) {
            barcodeScannerView!!.setTorchOff()
        } else {
            barcodeScannerView!!.setTorchOn()
        }
    }

    private fun changeMaskColor(view: View?) {
        val rnd = Random()
        val color: Int = Color.argb(100, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
        viewfinderView!!.setMaskColor(color)
    }

    private fun changeLaserVisibility(visible: Boolean) {
        viewfinderView!!.setLaserVisibility(visible)
    }

    override fun onTorchOn() {
        switchFlashlightButton?.setImageResource(R.drawable.flash_on)
        flashOn = true
    }

    override fun onTorchOff() {
        switchFlashlightButton?.setImageResource(R.drawable.flash_off)
        flashOn = false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray) {
        capture!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}