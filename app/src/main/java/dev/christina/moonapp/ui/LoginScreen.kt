package dev.christina.moonapp.ui

import android.util.Log
import android.widget.Toast
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import dev.christina.moonapp.data.db.MoonDatabase
import dev.christina.moonapp.repository.FirebaseRepository
import dev.christina.moonapp.repository.MoonRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val firebaseAuth = FirebaseAuth.getInstance()
    val firebaseRepository = FirebaseRepository(com.google.firebase.firestore.FirebaseFirestore.getInstance())

    // Initialize MoonViewModel
    val moonViewModel: MoonViewModel = viewModel(
        factory = ViewModelFactoryProvider(
            MoonRepository(
                MoonDatabase.getDatabase(context).moonDao()
            )
        )
    )

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "LOGIN",
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
                            .height(60.dp), // Consistent height
                        shape = RoundedCornerShape(24.dp), // Rounded corners
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(60.dp),
                        shape = RoundedCornerShape(24.dp), // Rounded corners for the border
                        visualTransformation = PasswordVisualTransformation(), // Hide password input
                    )

                    Button(
                        onClick = {
                            if (email.isNotEmpty() && password.isNotEmpty()) {
                                isLoading = true
                                firebaseAuth.signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            val user = task.result?.user
                                            user?.let {
                                                coroutineScope.launch {
                                                    try {
                                                        Log.d("LoginScreen", "Fetching data for user: ${it.uid}")
                                                        moonViewModel.clearState()
                                                        // Ensure zodiac sign is retrieved
                                                        val zodiacSign = moonViewModel.getZodiacSign(it.uid, firebaseRepository)

                                                        if (!zodiacSign.isNullOrBlank()) {
                                                            Log.d("LoginScreen", "Fetched Zodiac Sign: $zodiacSign")
                                                            moonViewModel.setSelectedZodiac(zodiacSign)

                                                            // Fetch saved days before navigating
                                                            moonViewModel.fetchSavedDays(it.uid, firebaseRepository)
                                                            delay(100)
                                                            Log.d("LoginScreen", "Fetched savedDaysList before navigation.")

                                                            // Navigate to SecondScreen
                                                            navController.navigate("secondScreen?email=$email&date=${LocalDate.now()}")
                                                        } else {
                                                            throw Exception("Zodiac sign not found.")
                                                        }
                                                    } catch (e: Exception) {
                                                        Log.e("LoginScreen", "Error during login flow: ${e.message}")
                                                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                                    } finally {
                                                        isLoading = false
                                                    }
                                                }
                                            }
                                        } else {
                                            Toast.makeText(context, "Login Failed", Toast.LENGTH_SHORT).show()
                                            isLoading = false
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
                        Text("Log In", color = Color.White)
                    }
                }
            }
        }
    }
}
