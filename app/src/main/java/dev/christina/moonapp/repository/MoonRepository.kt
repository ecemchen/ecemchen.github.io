package dev.christina.moonapp.repository

import android.util.Log
import dev.christina.moonapp.data.db.MoonDao
import dev.christina.moonapp.data.db.MoonEntity
import dev.christina.moonapp.data.remote.ZodiacResponse
import dev.christina.moonapp.remote.RetrofitInstance
import java.time.YearMonth
import java.time.ZoneId
import kotlinx.coroutines.flow.firstOrNull

class MoonRepository(private val moonDao: MoonDao) {

    // Fetch moon phases for a specific month from the database
    suspend fun getMoonPhasesForMonth(month: Int, year: Int): List<MoonEntity> {
        val monthStr = if (month < 10) "0$month" else "$month"
        val yearMonth = "$year-$monthStr"
        return moonDao.getMoonPhasesForMonth(yearMonth).firstOrNull() ?: emptyList()
    }


    // Fetch moon phases from the API and save them to the database
    suspend fun fetchAndSaveMoonPhasesForMonth(yearMonth: YearMonth) {
        val existingData = moonDao.getMoonPhasesForMonth(yearMonth.toString()).firstOrNull()

        if (!existingData.isNullOrEmpty()) {
            return // Data already exists; no need to fetch
        }

        val daysInMonth = yearMonth.lengthOfMonth()
        val moonPhases = (1..daysInMonth).map { day ->
            val timestamp =
                yearMonth.atDay(day).atStartOfDay(ZoneId.systemDefault()).toEpochSecond()
            val apiResponse = RetrofitInstance.api.getMoonDetails(timestamp)
            val moon = apiResponse.first()
            MoonEntity(
                date = yearMonth.atDay(day).toString(),
                phase = moon.Phase,
                illumination = moon.Illumination
            )
        }
        moonDao.insertMoonPhases(moonPhases)
    }


    // Fetch Zodiac Advice
    suspend fun getZodiacAdvice(sign: String, day: String): ZodiacResponse? {
        return try {
            Log.d("ZodiacAPI", "Fetching advice for sign: $sign, day: $day")
            val response = RetrofitInstance.zodiacApi.getZodiacAdvice(sign, day)
            Log.d("ZodiacAPI", "API Response: $response")
            response
        } catch (e: Exception) {
            Log.e("ZodiacAPI", "Error fetching advice: ${e.message}")
            null
        }
    }
}


