/**  Copyright © 2018 Socket Mobile, Inc. */

package com.socketmobile.stockcount.helper

import android.content.Context
import android.os.Environment
import com.socketmobile.stockcount.model.RMFile
import io.realm.Realm
import io.realm.RealmResults
import java.io.File
import java.util.*

fun createFile(c: Context): String {
    val now = Date()
    val curDate = getFullDateString(now)
    var scanCount = 1
    val lastScanDate = getScanDate(c)
    if (curDate == lastScanDate) {
        scanCount = getScanCount(c) + 1
    } else {
        setScanDate(c, curDate)
    }
    setScanCount(c, scanCount)

    val fileTitle = "Inventory Scan - ${getFullDateString(now)}-$scanCount"
    val fileName = "InventoryScan_${getDateStringWithoutWild(now)}"

    val realm = Realm.getDefaultInstance()
    realm.executeTransaction {
        val obj = realm.createObject(RMFile::class.java, fileName)
        obj.fileTitle = fileTitle
        obj.fileContent = fileTitle + "\n"
    }

    return fileName
}
fun getFiles(): RealmResults<RMFile> {
    val realm = Realm.getDefaultInstance()
    return realm.where(RMFile::class.java).sort("updatedTime").findAll()
}

fun updateFile(file: RMFile) {
    val realm = Realm.getDefaultInstance()
    realm.executeTransaction {
        it.insertOrUpdate(file)
    }
}

fun getFile(fileName: String): RMFile? {
    val realm = Realm.getDefaultInstance()
    return realm.where(RMFile::class.java).equalTo("fileName", fileName).findFirst()
}

fun deleteRMFile(fileName: String) {
    getFile(fileName)?.let { deleteRMFile(it) }
}
fun deleteRMFile(file: RMFile) {
    Realm.getDefaultInstance().executeTransaction {
        file.deleteFromRealm()
    }
}

fun getFileNameWithExt(c: Context, file: RMFile): String {
    return if (isConsolidatingCounts(c)) {
        "${file.fileName}.csv"
    } else {
        "${file.fileName}.txt"
    }
}

fun getCountsAggregatedContent(c: Context, file: RMFile): String {
    var content = file.fileContent
    val lines = content.split("\n")
    if (lines.isNotEmpty()) {
        var contentLines = lines.toMutableList()
        contentLines.removeAt(0)
        var barcodeList = mutableListOf<String>()
        var barcodeCountMap = mutableMapOf<String, Int>()
        contentLines.joinToString(";").split(";").forEach {
            var components = if (it.contains(",")) {
                it.split(",")
            } else {
                it.split(" ")
            }
            if (components.isNotEmpty()) {
                val barcodeIndex = 0
                val countIndex = 1
                val defaultCount = 1
                val barcode = components[barcodeIndex].trim()
                val count : Int = if (components.size > countIndex) {
                    components[countIndex].trim().toIntOrNull() ?: defaultCount
                } else {
                    defaultCount
                }
                if (barcode.isNotEmpty()) {
                    if (barcodeList.contains(barcode)) {
                        barcodeCountMap[barcode] = barcodeCountMap[barcode]!! + count
                    } else {
                        barcodeList.add(barcode)
                        barcodeCountMap[barcode] = count
                    }
                }
            }
        }
        content = "${lines[0]}\n"
        barcodeList.forEach { barcode ->
            val count = barcodeCountMap[barcode]
            content += "$barcode, $count\n"
        }
    }

    return content
}