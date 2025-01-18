package dev.christina.moonapp.repository

import com.google.firebase.firestore.FirebaseFirestore
import dev.christina.moonapp.data.db.NoteDao
import dev.christina.moonapp.data.db.NoteEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.time.YearMonth

class NoteRepository(private val firebaseRepository: FirebaseRepository) {

    suspend fun getNotesForMonth(uid: String, yearMonth: YearMonth): List<String> {
        return firebaseRepository.getNotesForMonth(uid, yearMonth)
    }

    suspend fun getNotesForDate(uid: String, date: String): List<NoteEntity> {
        val notes = firebaseRepository.getNotesForDate(uid, date)
        return notes.map { content -> NoteEntity(date = date, content = content) }
    }

    suspend fun addNoteForDate(uid: String, date: String, content: String) {
        firebaseRepository.addNoteForDate(uid, date, content)
    }

    suspend fun updateNoteForDate(uid: String, date: String, oldContent: String, updatedContent: String) {
        val firestore = FirebaseFirestore.getInstance() // Initialize Firestore instance
        val notesCollection = firestore.collection("users").document(uid).collection("notes")
        val query = notesCollection
            .whereEqualTo("date", date)
            .whereEqualTo("content", oldContent) // Use the original content to locate the note
            .get()
            .await()

        if (!query.isEmpty) {
            query.documents.first().reference.update("content", updatedContent).await()
        }
    }



    suspend fun deleteNoteForDate(uid: String, date: String, content: String) {
        firebaseRepository.deleteNoteForDate(uid, date, content)
    }
}
