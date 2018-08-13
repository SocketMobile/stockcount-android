package com.socketmobile.stockcount.ui

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.*
import android.support.v7.app.AppCompatActivity
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.text.method.TextKeyListener
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.socketmobile.capture.AppKey
import com.socketmobile.capture.Error
import com.socketmobile.capture.Property
import com.socketmobile.capture.client.*
import com.socketmobile.capture.client.callbacks.PropertyCallback
import com.socketmobile.capture.types.DataSourceId
import com.socketmobile.capture.types.Device
import com.socketmobile.stockcount.R
import com.socketmobile.stockcount.model.RMFile
import kotlinx.android.synthetic.main.activity_edit.*
import com.socketmobile.capture.client.DeviceAvailabilityEvent
import android.view.InputDevice.getDevice
import com.socketmobile.capture.android.Capture
import com.socketmobile.stockcount.BuildConfig
import android.view.InputDevice.getDevice
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import com.socketmobile.capture.client.DeviceClient
import com.socketmobile.capture.client.DataEvent
import com.socketmobile.stockcount.helper.*
import io.realm.Realm
import org.greenrobot.eventbus.ThreadMode
import org.greenrobot.eventbus.Subscribe
import java.io.File
import java.io.FileOutputStream

class EditActivity : AppCompatActivity() {
    lateinit var file: RMFile
    val deviceMap = mutableMapOf<String, DeviceClient>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fileEditText.setOnFocusChangeListener { v, hasFocus ->
            editTypeView.visibility = if (hasFocus || true) View.VISIBLE else View.GONE
        }
        Capture.builder(getApplicationContext())
                .enableLogging(BuildConfig.DEBUG)
                .build();

        deviceButton.isEnabled = false
        scanButton.setOnClickListener {
            triggerDevices()
        }
        scanRightButton.setOnClickListener {
            triggerDevices()
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

    fun triggerDevices() {
        if (deviceMap.size > 0) {
            for(deviceClient in deviceMap.values) {
                deviceClient.trigger(object: PropertyCallback() {
                    override fun onComplete(p: Property?, err: Error?) {

                    }
                })
            }
        } else {

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
    fun onCaptureDeviceAvailabilityChanged(event: DeviceAvailabilityEvent) {
        val device = event.device
        val eventType = event.type

        if (eventType == DeviceAvailabilityEvent.TYPE_REMOVAL) {
            // No device
            deviceMap.remove(event.device.deviceGuid)
        } else if (eventType == DeviceAvailabilityEvent.TYPE_ARRIVAL || eventType == DeviceAvailabilityEvent.TYPE_CLOSE) {
            // Device is closed
            deviceMap.remove(event.device.deviceGuid)
        } else if (eventType == DeviceAvailabilityEvent.TYPE_OPEN || eventType == DeviceAvailabilityEvent.TYPE_OWNERSHIP_LOST) {
            // Device open, but no ownership
            deviceMap.remove(event.device.deviceGuid)
        } else if (eventType == DeviceAvailabilityEvent.TYPE_OWNERSHIP_OBTAINED) {
            deviceMap.put(event.device.deviceGuid, event.device)
        }

        updateDeviceButton()
    }

    fun updateDeviceButton() {
        if (deviceMap.size > 0) {
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
}
