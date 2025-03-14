package com.example.androidcamera.api

import com.example.androidcamera.models.VisionRequest
import com.example.androidcamera.models.VisionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Retrofit service interface for Google Cloud Vision API
 */
interface VisionApiService {
    
    /**
     * Sends an image to Google Cloud Vision API for text detection
     * 
     * @param apiKey Google Cloud API key as a query parameter
     * @param request Request body containing the image and features
     * @return Response from the Vision API
     */
    @POST("v1/images:annotate")
    suspend fun detectText(
        @Query("key") apiKey: String,
        @Body request: VisionRequest
    ): Response<VisionResponse>
}