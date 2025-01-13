package dev.christina.moonapp.repository

import dev.christina.moonapp.data.db.NoteDao
import dev.christina.moonapp.data.db.NoteEntity
import kotlinx.coroutines.flow.Flow

class NoteRepository(private val noteDao: NoteDao) {
    fun getNotesForDate(date: String): Flow<List<NoteEntity>> = noteDao.getNotesForDate(date)

    suspend fun addNoteForDate(date: String, content: String) {
        noteDao.insertNote(NoteEntity(date = date, content = content))
    }

    suspend fun deleteNoteById(id: Int) {
        noteDao.deleteNoteById(id)
    }

    suspend fun updateNoteById(id: Int, updatedContent: String) {
        noteDao.updateNoteById(id, updatedContent)
    }
}
