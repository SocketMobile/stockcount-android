/**  Copyright © 2018 Socket Mobile, Inc. */

package com.socketmobile.stockcount.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.socketmobile.stockcount.helper.*
import com.socketmobile.stockcount.R
import kotlinx.android.synthetic.main.activity_scan_settings.*

class ScanSettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_settings)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        addQuantitySwitch.isChecked = autoAddQuantity(this)
        updateCommaComponents()
        defaultQuantityTextView.text = getDefaultQuantity(this).toString()
        updateNewScanComponents()
        vibrateSwitch.isChecked = isVibrationOnScan(this)
        supportD600Switch.isChecked = isD600Support(this)
        consolidatingCountsSwitch.isChecked = isConsolidatingCounts(this)

        minusQuantityButton.setOnClickListener {
            var quantity = Integer.parseInt(defaultQuantityTextView.text.toString())
            if (quantity > 0) {
                quantity --
                defaultQuantityTextView.text = quantity.toString()
            }
            setDefaultQuantity(this@ScanSettingsActivity, quantity)
            updatePreview()
        }
        plusQuantityButton.setOnClickListener {
            var quantity = Integer.parseInt(defaultQuantityTextView.text.toString())
            if (quantity < 50) {
                quantity ++
                defaultQuantityTextView.text = quantity.toString()
            }
            setDefaultQuantity(this@ScanSettingsActivity, quantity)
            updatePreview()
        }
        supportD600Switch.setOnCheckedChangeListener { _, isChecked ->
            setD600Support(this, isChecked)
            updatePreview()
        }
        vibrateSwitch.setOnCheckedChangeListener { _, isChecked ->
            setVibrationOnScan(this, isChecked)
            updatePreview()
        }
        addQuantitySwitch.setOnCheckedChangeListener { _, isChecked ->
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
        consolidatingCountsSwitch.setOnCheckedChangeListener { _, isChecked ->
            setConsolidatingCounts(this, isChecked)
            updatePreview()
        }
        updatePreview()
    }

    private fun updateCommaComponents() {
        if (isDelineatorComma(this)) {
            commaCheckImageView.setImageResource(R.drawable.icon_check)
            noCommaCheckImageView.setImageResource(0)
        } else {
            commaCheckImageView.setImageResource(0)
            noCommaCheckImageView.setImageResource(R.drawable.icon_check)
        }
    }
    private fun updateNewScanComponents() {
        if (isAddNewLine(this)) {
            newLineCheckImageView.setImageResource(R.drawable.icon_check)
            semiColonCheckImageView.setImageResource(0)
        } else {
            newLineCheckImageView.setImageResource(0)
            semiColonCheckImageView.setImageResource(R.drawable.icon_check)
        }
    }
    private fun updatePreview() {
        var preview = getString(R.string.preview_prefix)
        preview += getLineForBarcode(this, getString(R.string.temp_barcode))
        preview += getLineForBarcode(this, getString(R.string.temp_barcode))
        previewTextView.text = preview
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
