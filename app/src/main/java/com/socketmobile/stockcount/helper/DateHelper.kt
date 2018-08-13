package com.socketmobile.stockcount.helper

import java.text.SimpleDateFormat
import java.util.*


val FULL_FORMAT = "MM/dd/yyyy"
val WITHOUT_WILDCARD_FORMAT = "yyMMddHHmmss"

fun getDateString(date: Date, format: String): String {
    val sdf = SimpleDateFormat(format)
    return sdf.format(date)
}
fun getFullDateString(date: Date): String {
    return getDateString(date, FULL_FORMAT)
}
fun getDateStringWithoutWild(date: Date): String {
    return getDateString(date, WITHOUT_WILDCARD_FORMAT)
}