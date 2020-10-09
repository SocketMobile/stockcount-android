package com.socketmobile.stockcount.helper

import com.socketmobile.stockcount.model.GetAffParam

val ChinaLink = "https://www.amazon.cn/dp/B01N8SQ8MU/ref=sr_1_7?__mk_zh_CN=%E4%BA%9A%E9%A9%AC%E9%80%8A%E7%BD%91%E7%AB%99&keywords=socket+mobile&qid=1555606674&s=gateway&sr=8-7"
val EuroCodes = arrayOf("AL", "AD", "AM", "AT", "BY", "BE", "BA", "BG", "CH", "CY", "CZ", "DE", "DK", "EE", "ES", "FO", "FI", "FR", "GE", "GI", "GR", "HU", "HR", "IE", "IS", "IT", "LT", "LU", "LV", "MC", "MK", "MT", "NO", "NL", "PO", "PT")
val DefaultKey = GetAffParam("pub_9a42760dc57269d2f616", "sec_86be30771c1af32a57df", "7daad6")
val Keys = mapOf(
        Pair("JP", GetAffParam("pub_0fa9f9c60fb103ec2c71", "sec_e11ef85cb83bc2b24d31", "5413c4")),
        Pair("AU", GetAffParam("pub_9c09ee1b0154d56d2cdd", "sec_68c0cf8ad5cc3059befe", "b922b8")),
        Pair("GB", GetAffParam("pub_d76b55562cc316faec74", "sec_8ef31932ec22f34bc943", "5462fa")),
        Pair("EMEA", GetAffParam("pub_81784e9af147411559cb", "sec_a367fe786e0811230d5a", "4fc514"))
)

fun getRefersionKey(regionCode: String): GetAffParam {
    var ret: GetAffParam? = null
    if (EuroCodes.indexOf(regionCode) != -1) {
        ret = Keys["EMEA"]
    } else {
        ret = Keys[regionCode]
    }
    if (ret != null) {
        return ret
    }
    return DefaultKey
}