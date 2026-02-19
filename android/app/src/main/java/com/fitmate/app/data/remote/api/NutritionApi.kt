package com.fitmate.app.data.remote.api

import com.fitmate.app.domain.model.ScannedProduct
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NutritionApi {

    @GET("api/nutrition/lookup")
    suspend fun lookupBarcode(
        @Query("barcode") barcode: String
    ): Response<ScannedProduct>
}
