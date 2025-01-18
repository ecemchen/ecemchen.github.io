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
import java.time.LocalDate
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

    private val _savedDaysList = MutableStateFlow<List<String>>(emptyList())
    val savedDaysList: StateFlow<List<String>> = _savedDaysList

    fun setSelectedZodiac(sign: String) {
        _selectedZodiac.value = sign
    }

    suspend fun getZodiacSign(uid: String, firebaseRepository: FirebaseRepository): String? {
        return try {
            val userData = firebaseRepository.getUserData(uid)
            val zodiacSign = userData?.get("zodiacSign") as? String
            if (!zodiacSign.isNullOrBlank()) {
                _selectedZodiac.value = zodiacSign
                Log.d("MoonViewModel", "Fetched Zodiac Sign: $zodiacSign")
            } else {
                Log.w("MoonViewModel", "Zodiac sign is null or blank")
            }
            zodiacSign
        } catch (e: Exception) {
            Log.e("MoonViewModel", "Error fetching zodiac sign: ${e.message}")
            null
        }
    }

    fun fetchZodiacAdvice(sign: String, date: String) {
        val parsedDate = runCatching { LocalDate.parse(date) }.getOrNull()
        if (parsedDate == null) {
            Log.e("MoonViewModel", "Invalid date format: $date")
            return
        }
        viewModelScope.launch {
            _isLoadingAdvice.value = true
            try {
                val advice = repository.getZodiacAdvice(sign, date)
                _zodiacAdvice.value = advice
                Log.d("MoonViewModel", "Fetched Zodiac Advice: $advice")
            } catch (e: Exception) {
                Log.e("MoonViewModel", "Error fetching Zodiac Advice: ${e.message}")
                _zodiacAdvice.value = null
            } finally {
                _isLoadingAdvice.value = false
            }
        }
    }

    fun fetchWeeklyZodiacAdvice(sign: String, week: String) {
        viewModelScope.launch {
            _isLoadingAdvice.value = true
            try {
                val advice = repository.getWeeklyZodiacAdvice(sign)
                _weeklyZodiacAdvice.value = advice
                _monthlyZodiacAdvice.value = null
                _zodiacAdvice.value = null
                Log.d("WeeklyZodiacAdvice", "Fetched Weekly Advice: $advice for week: $week")
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
                val advice = repository.getMonthlyZodiacAdvice(sign)
                _monthlyZodiacAdvice.value = advice
                _weeklyZodiacAdvice.value = null
                _zodiacAdvice.value = null
                Log.d("MonthlyZodiacAdvice", "Fetched Monthly Advice: $advice")
            } catch (e: Exception) {
                Log.e("MonthlyZodiacAdvice", "Error fetching monthly advice: ${e.message}")
            } finally {
                _isLoadingAdvice.value = false
            }
        }
    }

    fun fetchMoonPhasesForMonth(yearMonth: YearMonth) {
        viewModelScope.launch {
            try {
                repository.fetchAndSaveMoonPhasesForMonth(yearMonth)
                val monthPhases = repository.getMoonPhasesForMonth(
                    month = yearMonth.monthValue,
                    year = yearMonth.year
                )
                _savedMoonPhases.value = monthPhases
                _allMoonPhases.value = _allMoonPhases.value + monthPhases.associateBy { it.date }
                Log.d("MoonViewModel", "Updated allMoonPhases: ${_allMoonPhases.value}")
            } catch (e: Exception) {
                Log.e("MoonViewModel", "Error fetching moon phases: ${e.message}")
            }
        }
    }

    fun toggleMoonList(moonEntity: MoonEntity) {
        val updatedList = if (_moonList.value.contains(moonEntity)) {
            _moonList.value - moonEntity
        } else {
            _moonList.value + moonEntity
        }
        _moonList.value = updatedList
    }

    fun addToMoonList(moonEntity: MoonEntity) {
        if (!_moonList.value.contains(moonEntity)) {
            _moonList.value = _moonList.value + moonEntity
        }
    }

    fun removeFromMoonList(moonEntity: MoonEntity) {
        _moonList.value = _moonList.value - moonEntity
    }

    fun logAllMoonPhases() {
        viewModelScope.launch {
            val allPhases = repository.getAllMoonPhases()
            Log.d("MoonViewModel", "All Moon Phases in DB: $allPhases")
        }
    }

    fun saveDayAsFavorite(uid: String, date: String, firebaseRepository: FirebaseRepository) {
        viewModelScope.launch {
            try {
                firebaseRepository.saveDayAsFavorite(uid, date)

                // Update state immediately for immediate UI update
                val updatedList = _savedDaysList.value.toMutableList()
                if (!updatedList.contains(date)) {
                    updatedList.add(date)
                }
                _savedDaysList.value = updatedList
            } catch (e: Exception) {
                Log.e("MoonViewModel", "Error saving favorite day: ${e.message}")
            }
        }
    }

    fun removeDayAsFavorite(uid: String, date: String, firebaseRepository: FirebaseRepository) {
        viewModelScope.launch {
            try {
                firebaseRepository.removeDayAsFavorite(uid, date)

                // Update state immediately for immediate UI update
                val updatedList = _savedDaysList.value.toMutableList()
                if (updatedList.contains(date)) {
                    updatedList.remove(date)
                }
                _savedDaysList.value = updatedList
            } catch (e: Exception) {
                Log.e("MoonViewModel", "Error removing favorite day: ${e.message}")
            }
        }
    }


    fun fetchSavedDays(uid: String, firebaseRepository: FirebaseRepository) {
        viewModelScope.launch {
            try {
                val savedDays = firebaseRepository.getSavedDays(uid)
                _savedDaysList.value = savedDays // Triggers recomposition
                Log.d("MoonViewModel", "Updated savedDaysList: $savedDays")
            } catch (e: Exception) {
                Log.e("MoonViewModel", "Error fetching saved days: ${e.message}")
            }
        }
    }


    fun clearState() {
        _savedDaysList.value = emptyList()
        _selectedZodiac.value = null
        _zodiacAdvice.value = null
        _weeklyZodiacAdvice.value = null
        _monthlyZodiacAdvice.value = null
        _allMoonPhases.value = emptyMap()
        _savedMoonPhases.value = emptyList()
        _moonList.value = emptyList()
    }

}
