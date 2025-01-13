package dev.christina.moonapp.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.christina.moonapp.data.db.MoonEntity
import dev.christina.moonapp.data.remote.ZodiacResponse
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

    private val _selectedZodiac = MutableStateFlow<String?>(null)
    val selectedZodiac: StateFlow<String?> = _selectedZodiac

    private val _zodiacAdvice = MutableStateFlow<ZodiacResponse?>(null)
    val zodiacAdvice: StateFlow<ZodiacResponse?> = _zodiacAdvice

    private val _isLoadingAdvice = MutableStateFlow(false)
    val isLoadingAdvice: StateFlow<Boolean> = _isLoadingAdvice

    fun fetchMoonPhasesForMonth(yearMonth: YearMonth) {
        viewModelScope.launch {
            repository.fetchAndSaveMoonPhasesForMonth(yearMonth)
            val monthPhases = repository.getMoonPhasesForMonth(
                month = yearMonth.monthValue,
                year = yearMonth.year
            )
            _savedMoonPhases.value = monthPhases
            _allMoonPhases.value = _allMoonPhases.value + monthPhases.associateBy { it.date }
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

    fun fetchZodiacAdvice(sign: String, date: String) {
        viewModelScope.launch {
            _isLoadingAdvice.value = true
            Log.d("ZodiacAdvice", "Fetching advice for sign: $sign, date: $date")
            val advice = repository.getZodiacAdvice(sign = sign, day = date)
            Log.d("ZodiacAdvice", "Fetched advice: $advice")
            _zodiacAdvice.value = advice
            _isLoadingAdvice.value = false
        }
    }

    fun addToMoonList(moonEntity: MoonEntity) {
        if (!isInMoonList(moonEntity)) {
            _moonList.value = _moonList.value + moonEntity
        }
    }
}
