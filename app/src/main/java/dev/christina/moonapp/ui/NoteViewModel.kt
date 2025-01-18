package dev.christina.moonapp.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.christina.moonapp.data.db.NoteEntity
import dev.christina.moonapp.repository.NoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.YearMonth

class NoteViewModel(private val noteRepository: NoteRepository) : ViewModel() {
    private val _notesForDate = MutableStateFlow<List<NoteEntity>>(emptyList())
    val notesForDate: StateFlow<List<NoteEntity>> get() = _notesForDate

    private val _editingNote = MutableStateFlow<NoteEntity?>(null)
    val editingNote: StateFlow<NoteEntity?> get() = _editingNote

    private val _notesDates = MutableStateFlow<List<String>>(emptyList())
    val notesDates: StateFlow<List<String>> = _notesDates

    fun fetchNotesForMonth(uid: String, yearMonth: YearMonth) {
        viewModelScope.launch {
            try {
                val notes = noteRepository.getNotesForMonth(uid, yearMonth)
                _notesDates.value = notes
            } catch (e: Exception) {
                Log.e("NoteViewModel", "Error fetching notes for month: ${e.message}")
            }
        }
    }

    // Fetch notes for a specific date
    fun fetchNotesForDate(uid: String, date: String) {
        viewModelScope.launch {
            try {
                Log.d("NoteViewModel", "Fetching notes for date: $date")
                val notes = noteRepository.getNotesForDate(uid, date)
                _notesForDate.value = notes
                Log.d("NoteViewModel", "Fetched notes: $notes")
            } catch (e: Exception) {
                Log.e("NoteViewModel", "Error fetching notes: ${e.message}")
            }
        }
    }

    // Add a new note for a specific date
    fun addNoteForDate(uid: String, date: String, content: String) {
        viewModelScope.launch {
            try {
                noteRepository.addNoteForDate(uid, date, content)
                fetchNotesForDate(uid, date) // Refresh notes after adding
            } catch (e: Exception) {
                Log.e("NoteViewModel", "Error adding note: ${e.message}")
            }
        }
    }

    // Update an existing note
    fun updateNoteForDate(uid: String, date: String, oldContent: String, updatedContent: String) {
        viewModelScope.launch {
            try {
                noteRepository.updateNoteForDate(uid, date, oldContent, updatedContent)
                fetchNotesForDate(uid, date) // Refresh the notes list
            } catch (e: Exception) {
                Log.e("NoteViewModel", "Error updating note: ${e.message}")
            }
        }
    }


    // Delete a note
    fun deleteNoteForDate(uid: String, date: String, content: String) {
        viewModelScope.launch {
            try {
                noteRepository.deleteNoteForDate(uid, date, content)
                fetchNotesForDate(uid, date) // Refresh notes after deletion
            } catch (e: Exception) {
                Log.e("NoteViewModel", "Error deleting note: ${e.message}")
            }
        }
    }

}
