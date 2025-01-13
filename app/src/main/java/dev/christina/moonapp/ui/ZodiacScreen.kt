package dev.christina.moonapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dev.christina.moonapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZodiacScreen(navController: NavController, viewModel: MoonViewModel) {
    val zodiacSigns = listOf(
        "Aquarius" to R.drawable.aquarius,
        "Aries" to R.drawable.aries,
        "Cancer" to R.drawable.cancer,
        "Capricorn" to R.drawable.capricorn,
        "Gemini" to R.drawable.gemini,
        "Leo" to R.drawable.leo,
        "Libra" to R.drawable.libra,
        "Pisces" to R.drawable.pisces,
        "Sagittarius" to R.drawable.sagitarius,
        "Scorpio" to R.drawable.scorpio,
        "Taurus" to R.drawable.taurus,
        "Virgo" to R.drawable.virgo
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "CHOOSE YOUR ZODIAC SIGN",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Normal),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 40.dp)
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors()
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(top = 100.dp),
            contentAlignment = Alignment.TopStart // Position the grid at the start of the screen
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3), // 3 columns for the zodiac grid
                contentPadding = PaddingValues(0.dp),
                verticalArrangement = Arrangement.Top,
                horizontalArrangement = Arrangement.Start
            ) {
                items(zodiacSigns.size) { index ->
                    val (name, imageRes) = zodiacSigns[index]
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f) // Keep square cards
                            .clickable {
                                viewModel.setSelectedZodiac(name) // Set selected zodiac
                                navController.navigate("firstScreen/$name") // Navigate to FirstScreen
                            },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Image(
                            painter = painterResource(id = imageRes),
                            contentDescription = name,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}
