package dev.christina.moonapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.christina.moonapp.data.db.NoteEntity
import dev.christina.moonapp.repository.NoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NoteViewModel(private val noteRepository: NoteRepository) : ViewModel() {
    private val _notesForDate = MutableStateFlow<List<NoteEntity>>(emptyList())
    val notesForDate: StateFlow<List<NoteEntity>> get() = _notesForDate

    fun fetchNotesForDate(date: String) {
        viewModelScope.launch {
            noteRepository.getNotesForDate(date).collect { notes ->
                _notesForDate.value = notes
            }
        }
    }

    fun addNote(note: NoteEntity) {
        viewModelScope.launch {
            noteRepository.addNoteForDate(note.date, note.content)
            fetchNotesForDate(note.date) // Refresh notes
        }
    }

    fun deleteNote(note: NoteEntity) {
        viewModelScope.launch {
            noteRepository.deleteNoteById(note.id)
            fetchNotesForDate(note.date) // Refresh notes
        }
    }

    fun updateNote(note: NoteEntity) {
        viewModelScope.launch {
            noteRepository.updateNoteById(note.id, note.content)
            fetchNotesForDate(note.date) // Refresh notes
        }
    }

}
