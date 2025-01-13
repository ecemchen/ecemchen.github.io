package dev.christina.moonapp.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "moon_phases")
data class MoonEntity(
    @PrimaryKey val date: String, // Format: YYYY-MM-DD
    val phase: String,
    val illumination: Double,
    val isFavorited: Boolean = false,
    val zodiacSign: String? = null,
    val advice: String? = null,
    val mood: String? = null
)
