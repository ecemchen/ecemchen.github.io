package dev.christina.moonapp.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

data class ZodiacResponse(
    val data: HoroscopeData
)

data class HoroscopeData(
    val date: String,
    val horoscope_data: String
)


interface ZodiacApi {
    @GET("get-horoscope/daily")
    suspend fun getZodiacAdvice(
        @Query("sign") sign: String,
        @Query("day") day: String
    ): ZodiacResponse

    @GET("get-horoscope/weekly")
    suspend fun getWeeklyZodiacAdvice(
        @Query("sign") sign: String,
    ): ZodiacResponse

    @GET("get-horoscope/monthly")
    suspend fun getMonthlyZodiacAdvice(
        @Query("sign") sign: String,
    ): ZodiacResponse
}
