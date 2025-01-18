package dev.christina.moonapp.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.christina.moonapp.data.db.MoonEntity
import dev.christina.moonapp.data.remote.ZodiacResponse
import dev.christina.moonapp.repository.FirebaseRepository
import dev.christina.moonapp.repository.MoonRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.YearMonth

class MoonViewModel(private val repository: MoonRepository) : ViewModel() {
    private val _savedMoonPhases = MutableStateFlow<List<MoonEntity>>(emptyList())
    val savedMoonPhases: StateFlow<List<MoonEntity>> = _savedMoonPhases

    private val _allMoonPhases = MutableStateFlow<Map<String, MoonEntity>>(emptyMap())
    val allMoonPhases: StateFlow<Map<String, MoonEntity>> = _allMoonPhases

    private val _moonList = MutableStateFlow<List<MoonEntity>>(emptyList())
    val moonList: StateFlow<List<MoonEntity>> = _moonList

    private val _isLoadingAdvice = MutableStateFlow(false)
    val isLoadingAdvice: StateFlow<Boolean> = _isLoadingAdvice

    private val _weeklyZodiacAdvice = MutableStateFlow<ZodiacResponse?>(null)
    val weeklyZodiacAdvice: StateFlow<ZodiacResponse?> = _weeklyZodiacAdvice

    private val _monthlyZodiacAdvice = MutableStateFlow<ZodiacResponse?>(null)
    val monthlyZodiacAdvice: StateFlow<ZodiacResponse?> = _monthlyZodiacAdvice

    private val _selectedZodiac = MutableStateFlow<String?>(null)
    val selectedZodiac: StateFlow<String?> = _selectedZodiac

    private val _zodiacAdvice = MutableStateFlow<ZodiacResponse?>(null)
    val zodiacAdvice: StateFlow<ZodiacResponse?> = _zodiacAdvice

    fun fetchZodiacAdvice(sign: String, date: String) {
        viewModelScope.launch {
            try {
                _zodiacAdvice.value = repository.getZodiacAdvice(sign, date)
                Log.d("MoonViewModel", "Fetched Zodiac Advice: ${_zodiacAdvice.value}")
            } catch (e: Exception) {
                Log.e("MoonViewModel", "Error fetching Zodiac Advice: ${e.message}")
                _zodiacAdvice.value = null
            }
        }
    }

    suspend fun getZodiacSign(uid: String, firebaseRepository: FirebaseRepository): String? {
        return try {
            val userData = firebaseRepository.getUserData(uid)
            val zodiacSign = userData?.get("zodiacSign") as? String
            if (!zodiacSign.isNullOrBlank()) {
                _selectedZodiac.value = zodiacSign
                Log.d("MoonViewModel", "Zodiac Sign set to: $zodiacSign")
            } else {
                Log.w("MoonViewModel", "Zodiac sign is null or blank")
            }
            zodiacSign
        } catch (e: Exception) {
            Log.e("MoonViewModel", "Error fetching zodiac sign: ${e.message}")
            null
        }
    }


    fun fetchMoonPhasesForMonth(yearMonth: YearMonth) {
        viewModelScope.launch {
            repository.fetchAndSaveMoonPhasesForMonth(yearMonth)
            val monthPhases = repository.getMoonPhasesForMonth(
                month = yearMonth.monthValue,
                year = yearMonth.year
            )
            _savedMoonPhases.value = monthPhases
            _allMoonPhases.value = _allMoonPhases.value + monthPhases.associateBy { it.date }
            Log.d("MoonViewModel", "Updated allMoonPhases: ${_allMoonPhases.value}")
        }
    }



    fun toggleMoonList(moonEntity: MoonEntity) {
        val updatedList = if (isInMoonList(moonEntity)) {
            _moonList.value - moonEntity
        } else {
            _moonList.value + moonEntity
        }
        _moonList.value = updatedList
    }

    fun removeFromMoonList(moonEntity: MoonEntity) {
        _moonList.value = _moonList.value - moonEntity
    }

    fun isInMoonList(moonEntity: MoonEntity): Boolean {
        return _moonList.value.contains(moonEntity)
    }

    fun setSelectedZodiac(sign: String) {
        _selectedZodiac.value = sign
    }


    fun addToMoonList(moonEntity: MoonEntity) {
        if (!isInMoonList(moonEntity)) {
            _moonList.value = _moonList.value + moonEntity
        }
    }

    fun fetchWeeklyZodiacAdvice(sign: String, week: String) {
        viewModelScope.launch {
            _isLoadingAdvice.value = true
            try {
                Log.d("WeeklyZodiacAdvice", "Fetching weekly horoscope for sign: $sign")
                val advice = repository.getWeeklyZodiacAdvice(sign = sign)
                Log.d("WeeklyZodiacAdvice", "Fetched advice: $advice")
                _weeklyZodiacAdvice.value = advice
                _monthlyZodiacAdvice.value = null
                _zodiacAdvice.value = null
            } catch (e: Exception) {
                Log.e("WeeklyZodiacAdvice", "Error fetching weekly advice: ${e.message}")
            } finally {
                _isLoadingAdvice.value = false
            }
        }
    }


    fun fetchMonthlyZodiacAdvice(sign: String) {
        viewModelScope.launch {
            _isLoadingAdvice.value = true
            try {
                Log.d("MonthlyZodiacAdvice", "Fetching monthly horoscope for sign: $sign")
                val advice = repository.getMonthlyZodiacAdvice(sign = sign)
                Log.d("MonthlyZodiacAdvice", "Fetched advice: $advice")
                _monthlyZodiacAdvice.value = advice
                _weeklyZodiacAdvice.value = null
                _zodiacAdvice.value = null
            } catch (e: Exception) {
                Log.e("MonthlyZodiacAdvice", "Error fetching monthly advice: ${e.message}")
            } finally {
                _isLoadingAdvice.value = false
            }
        }
    }

    fun logAllMoonPhases() {
        viewModelScope.launch {
            val allPhases = repository.getAllMoonPhases()
            Log.d("MoonViewModel", "All Moon Phases in DB: $allPhases")
        }
    }
}
