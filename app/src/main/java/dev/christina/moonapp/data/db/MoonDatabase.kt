package dev.christina.moonapp.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [MoonEntity::class, NoteEntity::class], version = 5, exportSchema = false)
abstract class MoonDatabase : RoomDatabase() {
    abstract fun moonDao(): MoonDao
    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile
        private var INSTANCE: MoonDatabase? = null

        fun getDatabase(context: Context): MoonDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MoonDatabase::class.java,
                    "moon_database"
                )
                    .addMigrations(MIGRATION_4_5, MIGRATION_3_4) // Include both migrations
                    .build()
                INSTANCE = instance
                instance
            }
        }

        // Migration from version 3 to 4
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE moon_phases ADD COLUMN zodiacSign TEXT")
                database.execSQL("ALTER TABLE moon_phases ADD COLUMN advice TEXT")
                database.execSQL("ALTER TABLE moon_phases ADD COLUMN mood TEXT")
            }
        }

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE notes (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, date TEXT NOT NULL, content TEXT NOT NULL)")
            }
        }
    }
}
