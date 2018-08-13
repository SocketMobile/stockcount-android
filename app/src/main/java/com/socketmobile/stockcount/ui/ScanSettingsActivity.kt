package com.socketmobile.stockcount.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import com.socketmobile.stockcount.R
import com.socketmobile.stockcount.helper.*
import kotlinx.android.synthetic.main.activity_scan_settings.*

class ScanSettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_settings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        addQuantitySwitch.isChecked = autoAddQuantity(this)
        updateCommaComponents()
        defaultQuantityTextView.setText(getDefaultQuantity(this).toString())
        updateNewScanComponents()
        vibrateSwitch.isChecked = isVibrationOnScan(this)
        supportD600Switch.isChecked = isD600Support(this)

        minusQuantityButton.setOnClickListener {
            var quantity = Integer.parseInt(defaultQuantityTextView.text.toString())
            if (quantity > 0) {
                quantity --
                defaultQuantityTextView.setText(quantity.toString())
            }
            setDefaultQuantity(this@ScanSettingsActivity, quantity)
            updatePreview()
        }
        plusQuantityButton.setOnClickListener {
            var quantity = Integer.parseInt(defaultQuantityTextView.text.toString())
            if (quantity < 50) {
                quantity ++
                defaultQuantityTextView.setText(quantity.toString())
            }
            setDefaultQuantity(this@ScanSettingsActivity, quantity)
            updatePreview()
        }
        supportD600Switch.setOnCheckedChangeListener { buttonView, isChecked ->
            setD600Support(this, isChecked)
            updatePreview()
        }
        vibrateSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            setVibrationOnScan(this, isChecked)
            updatePreview()
        }
        addQuantitySwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            setAutoAddQuantity(this, isChecked)
            updatePreview()
        }
        commaCheckLayout.setOnClickListener {
            setDelineatorComma(this, true)
            updateCommaComponents()
            updatePreview()
        }
        noCommaCheckLayout.setOnClickListener {
            setDelineatorComma(this, false)
            updateCommaComponents()
            updatePreview()
        }
        newLineCheckLayout.setOnClickListener {
            setAddNewLine(this, true)
            updateNewScanComponents()
            updatePreview()
        }
        semiColonCheckLayout.setOnClickListener {
            setAddNewLine(this, false)
            updateNewScanComponents()
            updatePreview()
        }
        updatePreview()
    }

    fun updateCommaComponents() {
        if (isDelineatorComma(this)) {
            commaCheckImageView.setImageResource(R.drawable.icon_check)
            noCommaCheckImageView.setImageResource(0)
        } else {
            commaCheckImageView.setImageResource(0)
            noCommaCheckImageView.setImageResource(R.drawable.icon_check)
        }
    }
    fun updateNewScanComponents() {
        if (isAddNewLine(this)) {
            newLineCheckImageView.setImageResource(R.drawable.icon_check)
            semiColonCheckImageView.setImageResource(0)
        } else {
            newLineCheckImageView.setImageResource(0)
            semiColonCheckImageView.setImageResource(R.drawable.icon_check)
        }
    }
    fun updatePreview() {
        var preview = "The result will look like:\n"
        preview += getLineForBarcode(this, "[barcode]")
        preview += getLineForBarcode(this, "[barcode]")
        previewTextView.setText(preview)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
