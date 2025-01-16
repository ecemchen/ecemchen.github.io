package dev.christina.moonapp.ui

import android.widget.Toast
import androidx.compose.foundation.background
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
import java.time.Month
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController, moonViewModel: MoonViewModel) {
    val context = LocalContext.current
    val firebaseAuth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    var selectedYear by remember { mutableStateOf(Calendar.getInstance().get(Calendar.YEAR)) }
    var selectedMonth by remember { mutableStateOf(Calendar.getInstance().get(Calendar.MONTH) + 1) }
    var selectedDay by remember { mutableStateOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) }

    val daysInMonth = remember(selectedYear, selectedMonth) {
        Month.of(selectedMonth).length(java.time.Year.of(selectedYear).isLeap)
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
                    TextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(56.dp), // Consistent height
                        shape = RoundedCornerShape(24.dp), // Rounded corners
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color(0x8094B9FF), // Blue transparent background
                            focusedIndicatorColor = Color.Transparent, // Remove underline on focus
                            unfocusedIndicatorColor = Color.Transparent, // Remove underline when unfocused
                            cursorColor = Color.Black, // Set cursor color
                            focusedLabelColor = Color.Black, // Label color when focused
                            unfocusedLabelColor = Color.Gray // Label color when unfocused
                        )
                    )

                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(56.dp), // Consistent height
                        shape = RoundedCornerShape(24.dp), // Rounded corners
                        visualTransformation = PasswordVisualTransformation(), // Hide password input
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color(0x8094B9FF), // Blue transparent background
                            focusedIndicatorColor = Color.Transparent, // Remove underline on focus
                            unfocusedIndicatorColor = Color.Transparent, // Remove underline when unfocused
                            cursorColor = Color.Black, // Cursor color
                            focusedLabelColor = Color.Black, // Label color when focused
                            unfocusedLabelColor = Color.Gray // Label color when unfocused
                        )
                    )

                    Text(
                        text = "Select your birthdate",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color.Black,
                            fontSize = 18.sp // Adjust the font size here
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                            .wrapContentWidth(Alignment.CenterHorizontally) // Center align text horizontally
                    )


                    // Styled Year Picker
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        IconButton(
                            onClick = { if (selectedYear > 1900) selectedYear-- },
                            modifier = Modifier.background(Color(0x8094B9FF), shape = RoundedCornerShape(50))
                        ) {
                            Text("<", fontSize = 18.sp, color = Color.Gray)
                        }
                        Text(
                            text = "$selectedYear",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.Black,
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .background(Color(0x8094B9FF), shape = RoundedCornerShape(12.dp))
                                .padding(8.dp)
                        )
                        IconButton(
                            onClick = { selectedYear++ },
                            modifier = Modifier.background(Color(0x8094B9FF), shape = RoundedCornerShape(50))
                        ) {
                            Text(">", fontSize = 18.sp, color = Color.Gray)
                        }
                    }

                    // Styled Month Picker
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        IconButton(
                            onClick = { if (selectedMonth > 1) selectedMonth-- else selectedMonth = 12 },
                            modifier = Modifier.background(Color(0x8094B9FF), shape = RoundedCornerShape(50))
                        ) {
                            Text("<", fontSize = 18.sp, color = Color.Gray)
                        }
                        Text(
                            text = Month.of(selectedMonth).name.capitalize(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.Black,
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .background(Color(0x8094B9FF), shape = RoundedCornerShape(12.dp))
                                .padding(8.dp)
                        )
                        IconButton(
                            onClick = { if (selectedMonth < 12) selectedMonth++ else selectedMonth = 1 },
                            modifier = Modifier.background(Color(0x8094B9FF), shape = RoundedCornerShape(50))
                        ) {
                            Text(">", fontSize = 18.sp, color = Color.Gray)
                        }
                    }

                    // Styled Day Picker
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        IconButton(
                            onClick = { if (selectedDay > 1) selectedDay-- else selectedDay = daysInMonth },
                            modifier = Modifier.background(Color(0x8094B9FF), shape = RoundedCornerShape(50))
                        ) {
                            Text("<", fontSize = 18.sp, color = Color.Gray)
                        }
                        Text(
                            text = "$selectedDay",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.Black,
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .background(Color(0x8094B9FF), shape = RoundedCornerShape(12.dp))
                                .padding(8.dp)
                        )
                        IconButton(
                            onClick = { if (selectedDay < daysInMonth) selectedDay++ else selectedDay = 1 },
                            modifier = Modifier.background(Color(0x8094B9FF), shape = RoundedCornerShape(50))
                        ) {
                            Text(">", fontSize = 18.sp, color = Color.Gray)
                        }
                    }

                    // Register Button
                    Button(
                        onClick = {
                            if (email.isNotEmpty() && password.isNotEmpty()) {
                                isLoading = true
                                val zodiacSign = calculateZodiacSign(selectedYear, selectedMonth, selectedDay)

                                firebaseAuth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            val user = task.result?.user
                                            user?.let {
                                                firestore.collection("users").document(it.uid)
                                                    .set(
                                                        mapOf(
                                                            "email" to email,
                                                            "uid" to it.uid,
                                                            "birthdate" to "$selectedYear-$selectedMonth-$selectedDay",
                                                            "zodiacSign" to zodiacSign
                                                        )
                                                    )
                                                    .addOnSuccessListener {
                                                        Toast.makeText(
                                                            context,
                                                            "Registration Successful!",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                        navController.navigate("userZodiacScreen/$zodiacSign")
                                                    }
                                            }
                                            moonViewModel.setSelectedZodiac(zodiacSign)
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "Registration Failed",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        isLoading = false
                                    }
                            } else {
                                Toast.makeText(
                                    context,
                                    "Please fill all fields",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    ) {
                        Text("Register", color = Color.White)
                    }
                }
            }
        }
    }
}
