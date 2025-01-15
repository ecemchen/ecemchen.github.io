package dev.christina.moonapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dev.christina.moonapp.R


@Composable
fun UserZodiacScreen(navController: NavController, zodiacSign: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
            verticalArrangement = Arrangement.SpaceAround, // Distributes items with equal spacing
            modifier = Modifier.fillMaxSize()
        ) {
                text = "Your Zodiac Sign",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = zodiacSign,
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 32.sp),
                modifier = Modifier.padding(bottom = 32.dp)
            )
            // Top content
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Your Zodiac Sign",
                    style = MaterialTheme.typography.headlineLarge,
                )

                val imageResource = getZodiacImageResource(zodiacSign)
                if (imageResource != null) {
                    Image(
                        painter = painterResource(id = imageResource),
                        contentDescription = "Zodiac Sign: $zodiacSign",
                        modifier = Modifier
                            .size(500.dp)
                            .padding(top = 90.dp)
                    )
                }
            }

            // Button at the bottom
            Button(
                onClick = { navController.navigate("secondScreen") },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text(
                    text = "CONTINUE",
                    style = MaterialTheme.typography.bodyLarge.copy(color = Color.White)
                )
            }
        }
    }
}


// Helper function to get the drawable resource for a zodiac sign
private fun getZodiacImageResource(zodiacSign: String): Int? {
    return when (zodiacSign.lowercase()) {
        "aquarius" -> R.drawable.aquarius
        "pisces" -> R.drawable.pisces
        "aries" -> R.drawable.aries
        "taurus" -> R.drawable.taurus
        "gemini" -> R.drawable.gemini
        "cancer" -> R.drawable.cancer
        "leo" -> R.drawable.leo
        "virgo" -> R.drawable.virgo
        "libra" -> R.drawable.libra
        "scorpio" -> R.drawable.scorpio
        "sagittarius" -> R.drawable.sagitarius
        "capricorn" -> R.drawable.capricorn
        else -> null
    }
}

