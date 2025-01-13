package dev.christina.moonapp.remote

import dev.christina.moonapp.data.remote.ZodiacApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://api.farmsense.net/v1/"
    private const val ZODIAC_BASE_URL = "https://horoscope-app-api.vercel.app/api/v1/"

    val api: FarmSenseApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FarmSenseApi::class.java)
    }

    val zodiacApi: ZodiacApi by lazy {
        Retrofit.Builder()
            .baseUrl(ZODIAC_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ZodiacApi::class.java)
    }
}