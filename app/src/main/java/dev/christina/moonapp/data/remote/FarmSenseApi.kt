package dev.christina.moonapp.remote

import retrofit2.http.GET
import retrofit2.http.Query

data class MoonResponse(
    val Error: Int,
    val ErrorMsg: String,
    val TargetDate: String,
    val Moon: List<String>,
    val Index: Int,
    val Age: Double,
    val Phase: String,
    val Distance: Double,
    val Illumination: Double,
    val AngularDiameter: Double,
    val DistanceToSun: Double,
    val SunAngularDiameter: Double
)

interface FarmSenseApi {
    @GET("moonphases/")
    suspend fun getMoonDetails(
        @Query("d") timestamp: Long,
        @Query("lat") latitude: Double = 0.0,
        @Query("lon") longitude: Double = 0.0
    ): List<MoonResponse>
}
