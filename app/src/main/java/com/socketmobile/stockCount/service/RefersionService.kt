package com.socketmobile.stockCount.service

import com.socketmobile.stockCount.model.Affiliate
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface RefersionService {
    @POST("api/get_affiliate")
    fun getAffiliate(@Body affiliate: HashMap<String, String>): Call<Affiliate>
}