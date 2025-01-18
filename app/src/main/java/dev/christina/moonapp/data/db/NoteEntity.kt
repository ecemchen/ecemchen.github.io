package dev.christina.moonapp.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
   @PrimaryKey(autoGenerate = true)
   val id: Int = 0, // Auto-generated ID
   val date: String, // Date associated with the note
   val content: String // Content of the note
)
