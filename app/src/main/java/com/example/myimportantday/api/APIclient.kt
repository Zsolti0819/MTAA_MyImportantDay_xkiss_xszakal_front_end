package com.example.myimportantday.api

import android.provider.SyncStateContract
import com.example.myimportantday.api.Constants.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class APIclient {
    private lateinit var apiService: APIservice

    fun getApiService(): APIservice {

        // Initialize ApiService if not initialized yet
        if (!::apiService.isInitialized) {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            apiService = retrofit.create(APIservice::class.java)
        }

        return apiService
    }

}