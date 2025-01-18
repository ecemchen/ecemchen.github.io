package dev.christina.moonapp.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.YearMonth

class FirebaseRepository(private val firestore: FirebaseFirestore) {

    // Save user data during registration
    suspend fun saveUserData(uid: String, birthdate: String, email: String, zodiacSign: String) {
        Log.d("FirebaseRepository", "Saving user data: UID=$uid, zodiacSign=$zodiacSign")
        val userData = mapOf(
            "uid" to uid,
            "birthdate" to birthdate,
            "email" to email,
            "zodiacSign" to zodiacSign,
            "savedDays" to emptyList<String>() // Initialize with an empty list
        )
        firestore.collection("users").document(uid).set(userData).await()

        // Add an empty "notes" sub-collection with a placeholder document
        val placeholderNote = mapOf(
            "date" to "2000-01-01", // Use a valid sample date
            "content" to "This is a placeholder note." // Provide default content
        )
        firestore.collection("users").document(uid)
            .collection("notes").document("placeholder").set(placeholderNote).await()
    }

    // Fetch user data after login
    suspend fun getUserData(uid: String): Map<String, Any>? {
        val userDoc = firestore.collection("users").document(uid).get().await()
        Log.d("FirebaseRepository", "Fetched user document: ${userDoc.data}")
        return if (userDoc.exists()) userDoc.data else null
    }

    //Saving days to Firebase

    suspend fun saveDayAsFavorite(uid: String, date: String) {
        val userDocRef = firestore.collection("users").document(uid)
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(userDocRef)
            val savedDays = snapshot.get("savedDays") as? List<String> ?: emptyList()
            if (!savedDays.contains(date)) {
                val updatedDays = savedDays + date
                transaction.update(userDocRef, "savedDays", updatedDays)
            }
        }.await()
    }

    suspend fun removeDayAsFavorite(uid: String, date: String) {
        val userDocRef = firestore.collection("users").document(uid)
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(userDocRef)
            val savedDays = snapshot.get("savedDays") as? List<String> ?: emptyList()
            if (savedDays.contains(date)) {
                val updatedDays = savedDays - date
                transaction.update(userDocRef, "savedDays", updatedDays)
            }
        }.await()
    }

    suspend fun getSavedDays(uid: String): List<String> {
        val userDoc = firestore.collection("users").document(uid).get().await()
        return userDoc.get("savedDays") as? List<String> ?: emptyList()
    }

    // Save a note for a specific date
    suspend fun addNoteForDate(uid: String, date: String, content: String) {
        val notesCollection = firestore.collection("users").document(uid).collection("notes")
        val noteData = mapOf("date" to date, "content" to content)
        notesCollection.add(noteData).await()
    }

    // Fetch all notes for a specific date
    suspend fun getNotesForDate(uid: String, date: String): List<String> {
        val notesCollection = firestore.collection("users").document(uid).collection("notes")
        val notesSnapshot = notesCollection.whereEqualTo("date", date).get().await()
        return notesSnapshot.documents.mapNotNull { it.getString("content") }
    }

    // Delete a specific note
    suspend fun deleteNoteForDate(uid: String, date: String, content: String) {
        val notesCollection = firestore.collection("users").document(uid).collection("notes")
        val notesSnapshot = notesCollection
            .whereEqualTo("date", date)
            .whereEqualTo("content", content)
            .get()
            .await()
        notesSnapshot.documents.forEach { it.reference.delete().await() }
    }

    suspend fun getNotesForMonth(uid: String, yearMonth: YearMonth): List<String> {
        val notesCollection = firestore.collection("users").document(uid).collection("notes")
        val startDate = LocalDate.of(yearMonth.year, yearMonth.monthValue, 1)
        val endDate = startDate.plusMonths(1).minusDays(1)

        val snapshot = notesCollection
            .whereGreaterThanOrEqualTo("date", startDate.toString())
            .whereLessThanOrEqualTo("date", endDate.toString())
            .get()
            .await()

        return snapshot.documents.mapNotNull { it.getString("date") }
    }


}
