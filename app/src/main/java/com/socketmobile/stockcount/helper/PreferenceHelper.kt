/**  Copyright © 2018 Socket Mobile, Inc. */

package com.socketmobile.stockcount.helper

import android.content.Context
import android.preference.PreferenceManager


const val SHOW_INSTRUCTION_KEY = "HaveToShowInstruction"
const val AUTO_ADD_QUANTITY_KEY = "AutoAddQuantity"
const val D600_SUPPORT_KEY = "D600Support"
const val DELINEATOR_COMMA_SET_KEY = "DelineatorCommaSet"
const val DEFAULT_QUANTITY_KEY = "DefaultQuantity"
const val NEW_LINE_KEY = "NewLineForNewScan"
const val VIBRATION_KEY = "VibrationOnScan"
const val SCAN_DATE_KEY = "ScanDate"
const val SCAN_COUNT_KEY = "ScanCount"
const val CONSOLIDATING_COUNTS = "ConsolidatingCounts"

const val DEFAULT_AUTO_ADD_QUANTITY = true
const val DEFAULT_D600_SUPPORT = false
const val DEFAULT_DELINEATOR_COMMA_SET = true
const val DEFAULT_QUANTITY = 1
const val DEFAULT_NEW_LINE = true
const val DEFAULT_VIBRATION_ON_SCAN = false
const val DEFAULT_SCAN_DATE = ""
const val DEFAULT_SCAN_COUNT = 0
const val DEFAULT_CONSOLIDATING_COUNTS = true

fun haveToShowInstruction(c: Context): Boolean {
    val sp = PreferenceManager.getDefaultSharedPreferences(c)
    return sp.getBoolean(SHOW_INSTRUCTION_KEY, true)
}

fun shownInstruction(c: Context) {
    val sp = PreferenceManager.getDefaultSharedPreferences(c)
    val editor = sp.edit()
    editor.putBoolean(SHOW_INSTRUCTION_KEY, false)
    editor.apply()
}

fun autoAddQuantity(c: Context): Boolean {
    val sp = PreferenceManager.getDefaultSharedPreferences(c)
    return sp.getBoolean(AUTO_ADD_QUANTITY_KEY, DEFAULT_AUTO_ADD_QUANTITY)
}

fun setAutoAddQuantity(c: Context, value: Boolean) {
    val editor = PreferenceManager.getDefaultSharedPreferences(c).edit()
    editor.putBoolean(AUTO_ADD_QUANTITY_KEY, value)
    editor.apply()
}

fun isD600Support(c: Context): Boolean {
    return PreferenceManager.getDefaultSharedPreferences(c).getBoolean(D600_SUPPORT_KEY, DEFAULT_D600_SUPPORT)
}

fun setD600Support(c: Context, value: Boolean) {
    val editor = PreferenceManager.getDefaultSharedPreferences(c).edit()
    editor.putBoolean(D600_SUPPORT_KEY, value)
    editor.apply()
}

fun isDelineatorComma(c: Context): Boolean {
    return PreferenceManager.getDefaultSharedPreferences(c).getBoolean(DELINEATOR_COMMA_SET_KEY, DEFAULT_DELINEATOR_COMMA_SET)
}

fun setDelineatorComma(c: Context, value: Boolean) {
    val editor = PreferenceManager.getDefaultSharedPreferences(c).edit()
    editor.putBoolean(DELINEATOR_COMMA_SET_KEY, value)
    editor.apply()
}

fun getDefaultQuantity(c: Context): Int {
    return PreferenceManager.getDefaultSharedPreferences(c).getInt(DEFAULT_QUANTITY_KEY, DEFAULT_QUANTITY)
}

fun setDefaultQuantity(c: Context, value: Int) {
    val editor = PreferenceManager.getDefaultSharedPreferences(c).edit()
    editor.putInt(DEFAULT_QUANTITY_KEY, value)
    editor.apply()
}

fun isAddNewLine(c: Context): Boolean {
    return PreferenceManager.getDefaultSharedPreferences(c).getBoolean(NEW_LINE_KEY, DEFAULT_NEW_LINE)
}

fun setAddNewLine(c: Context, value: Boolean) {
    val editor = PreferenceManager.getDefaultSharedPreferences(c).edit()
    editor.putBoolean(NEW_LINE_KEY, value)
    editor.apply()
}

fun isVibrationOnScan(c: Context): Boolean {
    return PreferenceManager.getDefaultSharedPreferences(c).getBoolean(VIBRATION_KEY, DEFAULT_VIBRATION_ON_SCAN)
}

fun setVibrationOnScan(c: Context, value: Boolean) {
    val editor = PreferenceManager.getDefaultSharedPreferences(c).edit()
    editor.putBoolean(VIBRATION_KEY, value)
    editor.apply()
}

fun isConsolidatingCounts(c: Context): Boolean {
    return PreferenceManager.getDefaultSharedPreferences(c).getBoolean(CONSOLIDATING_COUNTS, DEFAULT_CONSOLIDATING_COUNTS)
}
fun setConsolidatingCounts(c: Context, value: Boolean) {
    val editor = PreferenceManager.getDefaultSharedPreferences(c).edit()
    editor.putBoolean(CONSOLIDATING_COUNTS, value)
    editor.apply()
}

fun getScanDate(c: Context): String {
    return PreferenceManager.getDefaultSharedPreferences(c).getString(SCAN_DATE_KEY, DEFAULT_SCAN_DATE)!!
}

fun setScanDate(c: Context, value: String) {
    val editor = PreferenceManager.getDefaultSharedPreferences(c).edit()
    editor.putString(SCAN_DATE_KEY, value)
    editor.apply()
}

fun getScanCount(c: Context): Int {
    return PreferenceManager.getDefaultSharedPreferences(c).getInt(SCAN_COUNT_KEY, DEFAULT_SCAN_COUNT)
}

fun setScanCount(c: Context, value: Int) {
    val editor = PreferenceManager.getDefaultSharedPreferences(c).edit()
    editor.putInt(SCAN_COUNT_KEY, value)
    editor.apply()
}

fun getLineForBarcode(c: Context, barcode: String? = null): String {
    var retValue = if (barcode.isNullOrEmpty()) "txt_barcode" else barcode
    val defaultQuantity = getDefaultQuantity(c)
    if (autoAddQuantity(c)) {
        retValue += if (isDelineatorComma(c)) {
            ", $defaultQuantity"
        } else {
            " $defaultQuantity"
        }
    }

    val newLineSymbol = if (isAddNewLine(c)) "\n" else ";"
    retValue = if (!barcode.isNullOrEmpty()) {
        newLineSymbol + retValue
    } else {
        retValue + newLineSymbol
    }

    return retValue
}