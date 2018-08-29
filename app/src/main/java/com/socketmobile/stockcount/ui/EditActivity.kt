package com.socketmobile.stockcount.ui

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.method.TextKeyListener
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.socketmobile.capture.CaptureError
import com.socketmobile.capture.Property
import com.socketmobile.capture.android.Capture
import com.socketmobile.capture.android.events.ConnectionStateEvent
import com.socketmobile.capture.client.*
import com.socketmobile.capture.client.callbacks.PropertyCallback
import com.socketmobile.stockcount.R
import com.socketmobile.stockcount.helper.*
import com.socketmobile.stockcount.model.RMFile
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.android.synthetic.main.activity_options.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.io.FileOutputStream

class EditActivity : AppCompatActivity() {
    lateinit var file: RMFile
    var captureClient: CaptureClient? = null
    var scannerStatus = DeviceState.GONE
    var serviceStatus = ConnectionState.DISCONNECTED
    val TAG = EditActivity::class.java.name
    var isSoftScan = false
        set(value) {
            setSoftScanStatus(if (isSoftScan) 0 else 1)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fileEditText.setOnFocusChangeListener { v, hasFocus ->
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
                        .setPositiveButton("OK", object: DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface?, which: Int) {
                                deleteRMFile(file)
                                finish()
                            }

                        }).setNegativeButton("Cancel", object: DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface?, which: Int) {
                                dialog?.dismiss()
                            }
                        }).setMessage("Remove file '${file.fileName}'?")
                        .create()
                dialog.show()
            }
            R.id.menuSave -> {
                Realm.getDefaultInstance().executeTransaction {
                    file.fileContent = fileEditText.text.toString()
                    val lines = file.fileContent.split("\n")
                    if (lines.size > 0) {
                        file.fileTitle = lines[0].trim()
                    }
                    if (lines.size > 1) {
                        file.firstScan = lines[1].trim()
                    }
                }
                hideKeyboard()
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

    fun showCompanionDialog() {
        val dialogFrag = CompanionDialogFragment()
        dialogFrag.companionDialogListener = object: OnCompanionDialogListener {
            override fun onUseCamera() {
                isSoftScan = true
            }
        }
        dialogFrag.show(supportFragmentManager, "Companion Dialog")
    }
    fun onScanClicked() {
        if (scannerStatus == DeviceState.READY && serviceStatus == ConnectionState.CONNECTED) {
            triggerDevices(captureClient!!)
        }else {
            showCompanionDialog()
        }
    }
    fun toggleKeyboardNumeric() {
        if (abcButton.text.toString() == "123") {
            useNumericKeyboard()
        } else {
            useTextKeyboard()
        }
    }
    fun useTextKeyboard() {
        fileEditText.keyListener = TextKeyListener.getInstance()
        abcButton.setText("123")
    }
    fun useNumericKeyboard() {
        fileEditText.keyListener = android.text.method.DigitsKeyListener.getInstance()
        abcButton.setText("abc")
    }
    fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = currentFocus
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun shareContent() {
        clearStockCountDir()
        val tempFile = File(getStockCountDir(), file.fileName)
        val fos = FileOutputStream(tempFile)
        fos.write(file.fileContent.toByteArray())
        fos.close()
        tempFile.setReadable(true, false)

        val i = Intent(Intent.ACTION_SEND)
        i.setType("text/plain")
        i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(tempFile))
        val chooser = Intent.createChooser(i, "Share via")
        startActivity(chooser)
    }

    fun triggerDevices(captureClient: CaptureClient) {
        for(device in captureClient.devices) {
            device.trigger(object: PropertyCallback {
                override fun onComplete(p0: CaptureError?, p1: Property?) {
                    //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            })
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onData(event: DataEvent) {
        val device = event.device
        val data = event.data.string.trim()
        addScanData(getLineForBarcode(this, data))
    }

    fun addScanData(data: String) {
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
    fun goToEnd() {
        fileEditText.setSelection(fileEditText.text.toString().length)
    }
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onCaptureDeviceStateChange(event: DeviceStateEvent) {
        val device = event.device
        scannerStatus = event.state.intValue()

        when(scannerStatus) {
            DeviceState.AVAILABLE -> {
                Log.d(TAG, "Scanner State Available.")
                if (isSoftScan) {
                    isSoftScan = false
                }
            }
            DeviceState.OPEN -> {
                Log.d(TAG, "Scanner State Open.")
            }
            DeviceState.READY -> {
                Log.d(TAG, "Scanner State Ready.")
            }
            DeviceState.GONE -> {
                Log.d(TAG, "Scanner State Gone.")
            }
            else -> {
                Log.d(TAG, "Scanner State ${scannerStatus}")
            }
        }
        updateDeviceButton()
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onCaptureServiceConnectionStateChange(event: ConnectionStateEvent) {
        val state = event.state
        captureClient = event.client

        if (state.hasError()) {
            val error = state.error
            Log.d(TAG, "Error on service connection. Error: ${error.code}, ${error.message}")
            when(error.code) {
                CaptureError.COMPANION_NOT_INSTALLED -> {
                    var alert = AlertDialog.Builder(this)
                            .setMessage("Please install companion app.")
                            .setPositiveButton("OK") { dialog, which ->
                                dialog.dismiss()

                                val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.socketmobile.companion"))
                                startActivity(i)
                            }.setNegativeButton("Cancel") { dialog, which ->
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
            serviceStatus = state.intValue()
            Log.d(TAG, "Service Status is changed to ${serviceStatus}(${state.toString()})")
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


    fun updateDeviceButton() {
        if (scannerStatus == DeviceState.READY && serviceStatus == ConnectionState.CONNECTED) {
            enableDeviceButton()
        } else {
            disableDeviceButton()
        }
    }
    fun disableDeviceButton() {
        enableDeviceButton(false)
    }
    fun enableDeviceButton() {
        enableDeviceButton(true)
    }
    fun enableDeviceButton(enabled: Boolean) {
        deviceButton.isEnabled = enabled
    }
    fun setSoftScanStatus(status: Byte) {
        captureClient.setSoftScanStatus(status)
        //captureClient?.setSoftScanStatus(status)
    }
}
