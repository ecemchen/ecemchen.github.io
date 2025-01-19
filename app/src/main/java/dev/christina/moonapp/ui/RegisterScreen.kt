package dev.christina.moonapp.ui

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import dev.christina.moonapp.repository.FirebaseRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.Month
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController) {
    val context = LocalContext.current
    val firebaseAuth = FirebaseAuth.getInstance()
    val firebaseRepository = FirebaseRepository(FirebaseFirestore.getInstance())

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    var selectedYear by remember { mutableStateOf(Calendar.getInstance().get(Calendar.YEAR)) }
    var selectedMonth by remember { mutableStateOf(Calendar.getInstance().get(Calendar.MONTH) + 1) }
    var selectedDay by remember { mutableStateOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) }

    val daysInMonth = remember(selectedYear, selectedMonth) {
        Month.of(selectedMonth).length(java.time.Year.of(selectedYear).isLeap)
    }

    fun calculateZodiacSign(month: Int, day: Int): String {
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
                title = {
                    Text(
                        text = "REGISTER",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Normal,
                            color = Color.Black
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("welcomeScreen") }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
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
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(60.dp),
                        shape = RoundedCornerShape(24.dp)
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(60.dp),
                        shape = RoundedCornerShape(24.dp),
                        visualTransformation = PasswordVisualTransformation()
                    )

                    Text(
                        text = "Select your birthdate",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color.Black,
                            fontSize = 18.sp
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        IconButton(
                            onClick = { if (selectedYear > 1900) selectedYear-- },
                            modifier = Modifier.border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(50))
                        ) {
                            Text("<")
                        }
                        Text("$selectedYear", Modifier.padding(horizontal = 8.dp))
                        IconButton(
                            onClick = { selectedYear++ },
                            modifier = Modifier.border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(50))
                        ) {
                            Text(">")
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        IconButton(
                            onClick = { if (selectedMonth > 1) selectedMonth-- else selectedMonth = 12 },
                            modifier = Modifier.border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(50))
                        ) {
                            Text("<")
                        }
                        Text(Month.of(selectedMonth).name.capitalize(), Modifier.padding(horizontal = 8.dp))
                        IconButton(
                            onClick = { if (selectedMonth < 12) selectedMonth++ else selectedMonth = 1 },
                            modifier = Modifier.border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(50))
                        ) {
                            Text(">")
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        IconButton(
                            onClick = { if (selectedDay > 1) selectedDay-- else selectedDay = daysInMonth },
                            modifier = Modifier.border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(50))
                        ) {
                            Text("<")
                        }
                        Text("$selectedDay", Modifier.padding(horizontal = 8.dp))
                        IconButton(
                            onClick = { if (selectedDay < daysInMonth) selectedDay++ else selectedDay = 1 },
                            modifier = Modifier.border(BorderStroke(1.dp, Color.Black), RoundedCornerShape(50))
                        ) {
                            Text(">")
                        }
                    }

                    Button(
                        onClick = {
                            if (email.isNotBlank() && password.isNotBlank()) {
                                isLoading = true
                                val birthdate = "$selectedYear-$selectedMonth-$selectedDay"
                                val zodiacSign = calculateZodiacSign(selectedMonth, selectedDay)
                                CoroutineScope(Dispatchers.IO).launch {
                                    try {
                                        val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                                        val user = result.user
                                        user?.let {
                                            firebaseRepository.saveUserData(
                                                uid = it.uid,
                                                birthdate = birthdate,
                                                email = email,
                                                zodiacSign = zodiacSign
                                            )
                                            withContext(Dispatchers.Main) {
                                                navController.navigate("userZodiacScreen/$zodiacSign")
                                            }
                                        }
                                    } catch (e: Exception) {
                                        isLoading = false
                                        withContext(Dispatchers.Main) {
                                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            } else {
                                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    ) {
                        Text("REGISTER", color = Color.White)
                    }

                }
            }
        }
    }
}
