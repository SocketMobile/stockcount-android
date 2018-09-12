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
    val fileName = "InventoryScan_${getDateStringWithoutWild(now)}.txt"

    val realm = Realm.getDefaultInstance()
    realm.executeTransaction {
        val obj = realm.createObject(RMFile::class.java, fileName)
        obj.fileTitle = fileTitle
        obj.fileContent = fileTitle
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
    clearStockCountDir()
}

fun getStockCountDir(): File {
    val tempDir = File(Environment.getExternalStorageDirectory(), "StockCount")
    if (!tempDir.exists()) {
        tempDir.mkdir()
    }
    return tempDir
}

fun clearStockCountDir() {
    getStockCountDir().deleteOnExit()
}