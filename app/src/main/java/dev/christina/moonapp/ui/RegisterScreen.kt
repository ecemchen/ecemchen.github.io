package dev.christina.moonapp.ui
import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

@Composable
fun RegisterScreen(navController: NavController) {
    val context = LocalContext.current
    val firebaseAuth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var birthdate by remember { mutableStateOf("") } // Format: YYYY-MM-DD
    var isLoading by remember { mutableStateOf(false) }

    fun calculateZodiacSign(date: String): String {
        val parts = date.split("-").map { it.toInt() }
        val (_, month, day) = parts
        return when {
            (month == 1 && day >= 20) || (month == 2 && day <= 18) -> "Aquarius"
            (month == 2 && day >= 19) || (month == 3 && day <= 20) -> "Pisces"
            (month == 3 && day >= 21) || (month == 4 && day <= 19) -> "Aries"
            (month == 4 && day >= 20) || (month == 5 && day <= 20) -> "Taurus"
            (month == 5 && day >= 21) || (month == 6 && day <= 20) -> "Gemini"
            (month == 6 && day >= 21) || (month == 7 && day <= 22) -> "Cancer"
            (month == 7 && day >= 23) || (month == 8 && day <= 22) -> "Leo"
            (month == 8 && day >= 23) || (month == 9 && day <= 22) -> "Virgo"
            (month == 9 && day >= 23) || (month == 10 && day <= 22) -> "Libra"
            (month == 10 && day >= 23) || (month == 11 && day <= 21) -> "Scorpio"
            (month == 11 && day >= 22) || (month == 12 && day <= 21) -> "Sagittarius"
            else -> "Capricorn"
        }
    }

    // Date Picker Logic
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context,
        android.R.style.Theme_Holo_Light_Dialog_NoActionBar, // Use spinner mode
        { _, selectedYear, selectedMonth, selectedDay ->
            birthdate = "$selectedYear-${(selectedMonth + 1).toString().padStart(2, '0')}-${selectedDay.toString().padStart(2, '0')}"
        },
        year,
        month,
        day
    ).apply {
        datePicker.calendarViewShown = false // Disable calendar view
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = { datePickerDialog.show() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (birthdate.isNotEmpty()) birthdate else "Select Birthdate")
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (email.isNotEmpty() && password.isNotEmpty() && birthdate.isNotEmpty()) {
                        isLoading = true
                        val zodiacSign = calculateZodiacSign(birthdate)

                        firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val user = task.result?.user
                                    if (user != null) {
                                        val userEmail = user.email ?: "NoEmail"
                                        val userData = mapOf(
                                            "email" to userEmail,
                                            "uid" to user.uid,
                                            "birthdate" to birthdate,
                                            "zodiacSign" to zodiacSign
                                        )

                                        firestore.collection("users").document(user.uid)
                                            .set(userData)
                                            .addOnSuccessListener {
                                                Toast.makeText(
                                                    context,
                                                    "Registration Successful!",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                isLoading = false
                                                navController.navigate("secondScreen/$userEmail")
                                            }
                                            .addOnFailureListener { e ->
                                                Toast.makeText(
                                                    context,
                                                    "Failed to save user data: ${e.message}",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                isLoading = false
                                            }
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "User object is null after registration",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        isLoading = false
                                    }
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Registration Failed: ${task.exception?.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    isLoading = false
                                }
                            }
                    } else {
                        Toast.makeText(
                            context,
                            "All fields are required",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Register")
            }
        }
    }
}
