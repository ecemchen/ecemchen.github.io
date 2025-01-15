package dev.christina.moonapp.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MoonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMoonPhases(moonPhases: List<MoonEntity>)


    @Query("SELECT * FROM moon_phases WHERE date LIKE :month || '%' ORDER BY date ASC")
    fun getMoonPhasesForMonth(month: String): Flow<List<MoonEntity>>

    @Query("SELECT * FROM moon_phases")
    fun getAllMoonPhases(): Flow<List<MoonEntity>>

}

