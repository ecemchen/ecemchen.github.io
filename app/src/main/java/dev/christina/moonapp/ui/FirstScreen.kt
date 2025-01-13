package dev.christina.moonapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
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
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FirstScreen(navController: NavController, viewModel: MoonViewModel, zodiac: String?) {
    val selectedYearMonth = remember { mutableStateOf(YearMonth.now()) }
    val moonPhases by viewModel.savedMoonPhases.collectAsState(emptyList())
    val moonList by viewModel.moonList.collectAsState(emptyList())

    val black = Color(0xFF000000)
    val grey = Color(0xFF808080)
    val white = Color.White

    LaunchedEffect(selectedYearMonth.value) {
        viewModel.fetchMoonPhasesForMonth(selectedYearMonth.value)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (zodiac != null) {
                            val zodiacIcon = when (zodiac) {
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
                            if (zodiacIcon != null) {
                                Image(
                                    painter = painterResource(id = zodiacIcon),
                                    contentDescription = "Selected Zodiac Icon",
                                    modifier = Modifier
                                        .size(45.dp)
                                        .clickable { navController.navigate("zodiacScreen") } // Redirect to ZodiacScreen
                                )
                            }
                        }
                        Text(
                            text = "MOON CALENDAR",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Normal),
                            color = black
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("moonList") }) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "View Moon List",
                            tint = black
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = white)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Month Selector Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                val previousMonth = selectedYearMonth.value.minusMonths(1)
                val nextMonth = selectedYearMonth.value.plusMonths(1)

                Text(
                    text = previousMonth.month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                        .uppercase(),
                    modifier = Modifier
                        .clickable { selectedYearMonth.value = previousMonth }
                        .padding(horizontal = 15.dp),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 20.sp,
                        color = grey
                    )
                )

                Text(
                    text = selectedYearMonth.value.month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                        .uppercase(),
                    modifier = Modifier.padding(horizontal = 15.dp),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontSize = 30.sp,
                        color = black
                    )
                )

                Text(
                    text = nextMonth.month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                        .uppercase(),
                    modifier = Modifier
                        .clickable { selectedYearMonth.value = nextMonth }
                        .padding(horizontal = 15.dp),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 20.sp,
                        color = grey
                    )
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(moonPhases) { index, moonEntity ->
                    val localDate = LocalDate.parse(moonEntity.date)
                    val dayOfWeek = localDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                    val day = localDate.dayOfMonth
                    val month = localDate.month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                    val isFavorited = moonList.contains(moonEntity)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .clickable {
                                navController.navigate("secondScreen/${moonEntity.date}")
                            },
                        colors = CardDefaults.cardColors(containerColor = black),
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
                                        color = white
                                    )
                                )
                                Text(
                                    text = "$day",
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        fontWeight = FontWeight.Light,
                                        fontSize = 80.sp,
                                        color = white
                                    )
                                )
                                Text(
                                    text = month.uppercase(),
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Light,
                                        fontSize = 50.sp,
                                        color = white
                                    )
                                )
                            }

                            IconButton(
                                onClick = { viewModel.toggleMoonList(moonEntity) },
                                modifier = Modifier.align(Alignment.TopEnd)
                            ) {
                                Icon(
                                    imageVector = if (isFavorited) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Add to Moon List",
                                    tint = white
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
