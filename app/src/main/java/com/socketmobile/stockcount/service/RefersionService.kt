package com.socketmobile.stockcount.service

import com.socketmobile.stockcount.model.Affiliate
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface RefersionService {
    @POST("api/get_affiliate")
    fun getAffiliate(@Body affiliate: HashMap<String, String>): Call<Affiliate>
}