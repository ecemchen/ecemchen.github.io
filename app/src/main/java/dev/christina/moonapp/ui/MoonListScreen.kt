package dev.christina.moonapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dev.christina.moonapp.R
import dev.christina.moonapp.data.db.MoonEntity
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoonListScreen(navController: NavController, viewModel: MoonViewModel) {
    val moonList by viewModel.moonList.collectAsState(emptyList())
    val selectedZodiac = viewModel.selectedZodiac.collectAsState().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        if (!selectedZodiac.isNullOrBlank()) {
                            val zodiacIcon = when (selectedZodiac) {
                                "Aquarius" -> R.drawable.aquarius
                                "Aries" -> R.drawable.aries
                                "Cancer" -> R.drawable.cancer
                                "Capricorn" -> R.drawable.capricorn
                                "Gemini" -> R.drawable.gemini
                                "Leo" -> R.drawable.leo
                                "Libra" -> R.drawable.libra
                                "Pisces" -> R.drawable.pisces
                                "Sagittarius" -> R.drawable.sagitarius
                                "Scorpio" -> R.drawable.scorpio
                                "Taurus" -> R.drawable.taurus
                                "Virgo" -> R.drawable.virgo
                                else -> null
                            }
                            zodiacIcon?.let {
                                Image(
                                    painter = painterResource(id = it),
                                    contentDescription = "Selected Zodiac Icon",
                                    modifier = Modifier
                                        .size(45.dp)
                                        .clickable { navController.navigate("zodiacScreen") } // Navigate to ZodiacScreen
                                )
                            }
                        }
                        Text(
                            text = "MOON LIST",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Normal),
                            color = Color.Black
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(moonList) { _, moonEntity ->
                val localDate = LocalDate.parse(moonEntity.date)
                val dayOfWeek = localDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                val day = localDate.dayOfMonth
                val month = localDate.month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .padding(horizontal = 16.dp)
                        .clickable {
                            navController.navigate("secondScreen/${moonEntity.date}")
                        },
                    colors = CardDefaults.cardColors(containerColor = Color.Black),
                    shape = RoundedCornerShape(30.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp)
                    ) {
                        Column(
                            modifier = Modifier.align(Alignment.CenterStart),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = dayOfWeek,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 20.sp,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "$day",
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontWeight = FontWeight.Light,
                                    fontSize = 80.sp,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = month.uppercase(),
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Light,
                                    fontSize = 50.sp,
                                    color = Color.White
                                )
                            )
                        }

                        IconButton(
                            onClick = { viewModel.removeFromMoonList(moonEntity) },
                            modifier = Modifier.align(Alignment.TopEnd)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Remove from Moon List",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
