package dev.christina.moonapp.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dev.christina.moonapp.R

@Composable
fun WelcomeScreen(navController: NavController) {
    Scaffold(
        topBar = {} // No top bar
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                //  Image
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .size(220.dp)
                        .padding(bottom = 46.dp)
                )
                // Title
                Text(
                    text = "WELCOME TO MOONAPP",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Normal,
                        fontSize = 24.sp,
                        color = Color.Black
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Subtitle
                Text(
                    text = "MoonApp makes it easy to explore moon phases and get daily zodiac insights tailored to you. View a calendar of lunar details, save your favorite days, and add personal notes. Choose your zodiac sign to get advice and mood tips for each day.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 16.sp,
                        color = Color.Black
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                // Register Button
                Button(
                    onClick = { navController.navigate("registerScreen") },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text("REGISTER", color = Color.White, fontWeight = FontWeight.Normal)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Login Button
                Button(
                    onClick = { navController.navigate("loginScreen") },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(48.dp),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                    border = BorderStroke(2.dp, Color.Black)
                ) {
                    Text(
                        text = "LOGIN",
                        color = Color.Black,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }
    }
}