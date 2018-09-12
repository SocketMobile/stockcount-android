package com.socketmobile.stockcount.ui

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.method.TextKeyListener
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.socketmobile.capture.CaptureError
import com.socketmobile.capture.android.Capture
import com.socketmobile.capture.android.events.ConnectionStateEvent
import com.socketmobile.capture.client.*
import com.socketmobile.stockcount.R
import com.socketmobile.stockcount.helper.*
import com.socketmobile.stockcount.model.RMFile
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_edit.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.io.FileOutputStream
import java.util.*

class EditActivity : AppCompatActivity() {
    lateinit var file: RMFile
    private var captureClient: CaptureClient? = null
    private var scannerStatus = DeviceState.GONE
    private var serviceStatus = ConnectionState.DISCONNECTED
    private val tag = EditActivity::class.java.name!!
    var isSoftScan = false
        set(value) {
            setSoftScanStatus(if (isSoftScan) 0 else 1)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fileEditText.setOnFocusChangeListener { _, hasFocus ->
            editTypeView.visibility = if (hasFocus || true) View.VISIBLE else View.GONE
        }

        deviceButton.isEnabled = false
        scanButton.setOnClickListener {
            onScanClicked()
        }
        scanRightButton.setOnClickListener {
            onScanClicked()
        }
        abcButton.setOnClickListener {
            toggleKeyboardNumeric()
        }
        useTextKeyboard()

        if (intent != null) {
            val fileName = intent.getStringExtra("fileName")
            if (fileName != null) {
                val file = getFile(fileName)
                if (file != null) {
                    this.file = file
                    supportActionBar?.title = this.file.fileTitle
                    fileEditText.setText(file.fileContent)
                    goToEnd()
                    return
                }
            }
        }
        finish()
    }

    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                } else {
                    requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 100)
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.menuDelete -> {
                val dialog = AlertDialog.Builder(this)
                        .setPositiveButton(R.string.ok) { _, _ ->
                            deleteRMFile(file)
                            finish()
                        }.setNegativeButton(R.string.cancel) { dialog, _ ->
                            dialog?.dismiss()
                        }.setMessage("Remove file '${file.fileName}'?")
                        .create()
                dialog.show()
            }
            R.id.menuSave -> {
                Realm.getDefaultInstance().executeTransaction {
                    file.fileContent = fileEditText.text.toString()
                    val lines = file.fileContent.split("\n")
                    if (lines.isNotEmpty()) {
                        file.fileTitle = lines[0].trim()
                    }
                    if (lines.size > 1) {
                        file.firstScan = lines[1].trim()
                    }
                }
                hideKeyboard()
                finish()
            }
            R.id.menuShare -> {
                Realm.getDefaultInstance().executeTransaction {
                    file.fileContent = fileEditText.text.toString()
                }
                shareContent()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit, menu)
        return true
    }


    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun showCompanionDialog() {
        val dialogFrag = CompanionDialogFragment()
        dialogFrag.companionDialogListener = object: OnCompanionDialogListener {
            override fun onUseCamera() {
                isSoftScan = true
            }
        }
        dialogFrag.show(supportFragmentManager, getString(R.string.title_companion_dialog))
    }
    private fun onScanClicked() {
        if (scannerStatus == DeviceState.READY && serviceStatus == ConnectionState.CONNECTED) {
            triggerDevices(captureClient!!)
        }else {
            showCompanionDialog()
        }
    }
    private fun toggleKeyboardNumeric() {
        if (abcButton.text.toString() == getString(R.string.number_title)) {
            useNumericKeyboard()
        } else {
            useTextKeyboard()
        }
    }
    private fun useTextKeyboard() {
        fileEditText.keyListener = TextKeyListener.getInstance()
        abcButton.text = getString(R.string.number_title)
    }
    private fun useNumericKeyboard() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            fileEditText.keyListener = android.text.method.DigitsKeyListener.getInstance(Locale.US)
        } else {
            fileEditText.keyListener = android.text.method.DigitsKeyListener.getInstance()
        }
        abcButton.text = getString(R.string.alpha_title)
    }
    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = currentFocus
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun shareContent() {
        clearStockCountDir()
        val tempFile = File(getStockCountDir(), file.fileName)
        val fos = FileOutputStream(tempFile)
        fos.write(file.fileContent.toByteArray())
        fos.close()

        val i = Intent(Intent.ACTION_SEND)
        i.type = getString(R.string.mime_type_text)
        i.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this, applicationContext.packageName + ".fileProvider", tempFile))
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        val chooser = Intent.createChooser(i, getString(R.string.share_via))
        startActivity(chooser)
    }

    private fun triggerDevices(captureClient: CaptureClient) {
        for(device in captureClient.devices) {
            device.trigger { error, property ->
                Log.d(tag, "$error, $property")
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onData(event: DataEvent) {
        val data = event.data.string.trim()
        addScanData(getLineForBarcode(this, data))
    }

    private fun addScanData(data: String) {
        val newContent = fileEditText.text.toString() + data
        fileEditText.setText(newContent)
        goToEnd()
        if (isVibrationOnScan(this)) {
            val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vibrator.vibrate(500)
            }
        }
    }
    private fun goToEnd() {
        fileEditText.setSelection(fileEditText.text.toString().length)
    }
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onCaptureDeviceStateChange(event: DeviceStateEvent) {
        scannerStatus = event.state.intValue()

        when(scannerStatus) {
            DeviceState.AVAILABLE -> {
                Log.d(tag, "Scanner State Available.")
                if (isSoftScan) {
                    isSoftScan = false
                }
            }
            DeviceState.OPEN -> {
                Log.d(tag, "Scanner State Open.")
            }
            DeviceState.READY -> {
                Log.d(tag, "Scanner State Ready.")
            }
            DeviceState.GONE -> {
                Log.d(tag, "Scanner State Gone.")
            }
            else -> {
                Log.d(tag, "Scanner State $scannerStatus")
            }
        }
        updateDeviceButton()
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onCaptureServiceConnectionStateChange(event: ConnectionStateEvent) {
        val state = event.state

        if (state.hasError()) {
            val error = state.error
            Log.d(tag, "Error on service connection. Error: ${error.code}, ${error.message}")
            when(error.code) {
                CaptureError.COMPANION_NOT_INSTALLED -> {
                    val alert = AlertDialog.Builder(this)
                            .setMessage(R.string.prompt_install_companion)
                            .setPositiveButton(R.string.ok) { dialog, _ ->
                                dialog.dismiss()

                                val i = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.companion_store_url)))
                                startActivity(i)
                            }.setNegativeButton(R.string.cancel) { dialog, _ ->
                                dialog.dismiss()
                            }.create()
                    alert.show()
                }
                CaptureError.SERVICE_NOT_RUNNING -> {
                    if (state.isDisconnected) {
                        if (Capture.notRestartedRecently()) {
                            Capture.restart(this)
                        }
                    }
                }
                CaptureError.BLUETOOTH_NOT_ENABLED -> {
                    val i = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    startActivity(i)
                }
                else -> {

                }
            }
        } else {
            captureClient = event.client

            serviceStatus = state.intValue()
            Log.d(tag, "Service Status is changed to $serviceStatus($state)")
            when(serviceStatus) {
                ConnectionState.CONNECTING -> {
                }
                ConnectionState.CONNECTED -> {
                }
                ConnectionState.READY -> {

                }
                ConnectionState.DISCONNECTING -> {

                }
                ConnectionState.DISCONNECTED -> {

                }
            }
        }
        updateDeviceButton()
    }


    private fun updateDeviceButton() {
        if (scannerStatus == DeviceState.READY && serviceStatus == ConnectionState.CONNECTED) {
            enableDeviceButton()
        } else {
            disableDeviceButton()
        }
    }
    private fun disableDeviceButton() {
        enableDeviceButton(false)
    }
    private fun enableDeviceButton() {
        enableDeviceButton(true)
    }
    private fun enableDeviceButton(enabled: Boolean) {
        deviceButton.isEnabled = enabled
    }
    private fun setSoftScanStatus(status: Byte) {
        //captureClient?.setSoftScanStatus(status)
    }
}
