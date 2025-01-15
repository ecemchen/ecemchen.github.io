package dev.christina.moonapp.ui

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.Month
import java.util.*


import androidx.compose.material.icons.filled.Edit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSettingsScreen(navController: NavController, moonViewModel: MoonViewModel) {
    val context = LocalContext.current
    val firebaseAuth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    val user = firebaseAuth.currentUser
    var email by remember { mutableStateOf(user?.email ?: "") }
    var password by remember { mutableStateOf("********") }
    var isPasswordEditing by remember { mutableStateOf(false) }
    var isEmailEditing by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    var selectedYear by remember { mutableStateOf(Calendar.getInstance().get(Calendar.YEAR)) }
    var selectedMonth by remember { mutableStateOf(Calendar.getInstance().get(Calendar.MONTH) + 1) }
    var selectedDay by remember { mutableStateOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) }

    val daysInMonth = remember(selectedYear, selectedMonth) {
        Month.of(selectedMonth).length(java.time.Year.of(selectedYear).isLeap)
    }

    // Load the user's current birthdate from Firestore
    LaunchedEffect(user?.uid) {
        user?.let {
            firestore.collection("users").document(it.uid).get().addOnSuccessListener { document ->
                val birthdate = document.getString("birthdate") ?: "$selectedYear-$selectedMonth-$selectedDay"
                val (year, month, day) = birthdate.split("-").map { it.toInt() }
                selectedYear = year
                selectedMonth = month
                selectedDay = day
            }
        }
    }

    fun calculateZodiacSign(year: Int, month: Int, day: Int): String {
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

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("PROFILE SETTINGS", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Normal, color = Color.Black)) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController, currentScreen = "profileSettingsScreen")
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    successMessage?.let { message ->
                        Text(
                            text = message,
                            color = Color.Black,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    // Email Section
                    Text(
                        text = "Your mail:",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(0.8f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (isEmailEditing) {
                            TextField(
                                value = email,
                                onValueChange = { email = it },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        } else {
                            Text(text = email, modifier = Modifier.weight(1f))
                        }
                        IconButton(
                            onClick = {
                                if (isEmailEditing) {
                                    isLoading = true
                                    user?.updateEmail(email)?.addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            firestore.collection("users").document(user.uid)
                                                .update("email", email)
                                            Toast.makeText(context, "Email updated", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "Error updating email", Toast.LENGTH_SHORT).show()
                                        }
                                        isLoading = false
                                    }
                                }
                                isEmailEditing = !isEmailEditing
                            }
                        ) {
                            Icon(imageVector = if (isEmailEditing) Icons.Default.Check else Icons.Default.Edit, contentDescription = "Edit Email")
                        }
                    }

                    // Password Section
                    Text(
                        text = "Your password:",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(0.8f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (isPasswordEditing) {
                            TextField(
                                value = password,
                                onValueChange = { password = it },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                visualTransformation = PasswordVisualTransformation()
                            )
                        } else {
                            Text("********************", modifier = Modifier.weight(1f))
                        }
                        IconButton(
                            onClick = {
                                if (isPasswordEditing) {
                                    isLoading = true
                                    user?.updatePassword(password)?.addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(context, "Password updated", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "Error updating password", Toast.LENGTH_SHORT).show()
                                        }
                                        isLoading = false
                                    }
                                }
                                isPasswordEditing = !isPasswordEditing
                            }
                        ) {
                            Icon(imageVector = if (isPasswordEditing) Icons.Default.Check else Icons.Default.Edit, contentDescription = "Edit Password")
                        }
                    }

                    // Birthdate Section
                    Text(
                        text = "Update your birthdate:",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                    )

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                        // Year Picker
                        IconButton(onClick = { if (selectedYear > 1900) selectedYear-- }) {
                            Text("<", fontSize = 20.sp)
                        }
                        Text(
                            text = "$selectedYear",
                            fontSize = 18.sp,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        IconButton(onClick = { selectedYear++ }) {
                            Text(">", fontSize = 20.sp)
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                        // Month Picker
                        IconButton(onClick = { if (selectedMonth > 1) selectedMonth-- else selectedMonth = 12 }) {
                            Text("<", fontSize = 20.sp)
                        }
                        Text(
                            text = Month.of(selectedMonth).name,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        IconButton(onClick = { if (selectedMonth < 12) selectedMonth++ else selectedMonth = 1 }) {
                            Text(">", fontSize = 20.sp)
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                        // Day Picker
                        IconButton(onClick = { if (selectedDay > 1) selectedDay-- else selectedDay = daysInMonth }) {
                            Text("<", fontSize = 20.sp)
                        }
                        Text(
                            text = "$selectedDay",
                            fontSize = 18.sp,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        IconButton(onClick = { if (selectedDay < daysInMonth) selectedDay++ else selectedDay = 1 }) {
                            Text(">", fontSize = 20.sp)
                        }
                    }

                    Button(
                        onClick = {
                            isLoading = true
                            val birthdate = "$selectedYear-$selectedMonth-$selectedDay"
                            val newZodiacSign = calculateZodiacSign(selectedYear, selectedMonth, selectedDay)
                            firestore.collection("users").document(user?.uid ?: "")
                                .update(mapOf("birthdate" to birthdate, "zodiacSign" to newZodiacSign))
                                .addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        successMessage = "Birthdate successfully updated"
                                        moonViewModel.setSelectedZodiac(newZodiacSign) // Update in ViewModel
                                    } else {
                                        successMessage = "Error updating birthdate"
                                    }
                                    isLoading = false
                                }
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    ) {
                        Text("Save Changes", color = Color.White)
                    }
                    OutlinedButton(
                        onClick = {
                            firebaseAuth.signOut()
                            navController.navigate("welcomeScreen") {
                                popUpTo("welcomeScreen") { inclusive = true }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(48.dp),
                        border = BorderStroke(2.dp, Color.Black),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        )
                    ) {
                        Text(
                            text = "Logout",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
