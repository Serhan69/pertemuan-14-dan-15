package com.industri.fleettrack.data.remote.api

import com.google.gson.annotations.SerializedName
import com.industri.fleettrack.data.local.entity.DeliveryOrderEntity
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

data class StatusUpdateResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String
)

interface CourierApiService {

    @GET("api/v1/courier/manifest/today")
    suspend fun getTodayManifest(): Response<List<DeliveryOrderEntity>>

    @POST("api/v1/courier/deliveries/{order_id}/status")
    suspend fun updateDeliveryStatus(
        @Path("order_id") orderId: String,
        @Query("status") status: String
    ): Response<StatusUpdateResponse>

    companion object {
        const val BASE_URL = "https://YOUR-API-BASE-URL/"

        fun create(): CourierApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(CourierApiService::class.java)
        }
    }
}
