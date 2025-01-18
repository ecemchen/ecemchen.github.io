package dev.christina.moonapp.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import dev.christina.moonapp.data.db.MoonEntity
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*
import java.time.Month

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoonListScreen(navController: NavController, viewModel: MoonViewModel) {
    val moonList by viewModel.moonList.collectAsState(emptyList())
    val today = LocalDate.now()
    var selectedMonth by remember { mutableStateOf(today.monthValue) }
    var selectedYear by remember { mutableStateOf(today.year) }

    // Ensure valid days for the selected month and year
    fun safeLocalDate(year: Int, month: Int, day: Int): LocalDate {
        val daysInMonth = YearMonth.of(year, month).lengthOfMonth()
        val safeDay = if (day > daysInMonth) daysInMonth else day
        return LocalDate.of(year, month, safeDay)
    }

    // Trigger data fetch for the selected month
    LaunchedEffect(selectedMonth, selectedYear) {
        viewModel.fetchMoonPhasesForMonth(YearMonth.of(selectedYear, selectedMonth))
    }

    // Get weekday headers and month name
    val monthName = Month.of(selectedMonth).getDisplayName(TextStyle.FULL, Locale.ENGLISH)
    val weekDays = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(
                            onClick = {
                                if (selectedMonth > 1) {
                                    selectedMonth--
                                } else {
                                    selectedMonth = 12
                                    selectedYear--
                                }
                            },
                            modifier = Modifier.border(BorderStroke(1.dp, Color.Black), CircleShape)
                        ) {
                            Text("<", color = Color.Black)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "$monthName $selectedYear",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        IconButton(
                            onClick = {
                                if (selectedMonth < 12) {
                                    selectedMonth++
                                } else {
                                    selectedMonth = 1
                                    selectedYear++
                                }
                            },
                            modifier = Modifier.border(BorderStroke(1.dp, Color.Black), CircleShape)
                        ) {
                            Text(">", color = Color.Black)
                        }
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController, currentScreen = "moonList")
        }
    ) { padding ->
        val daysInMonth = YearMonth.of(selectedYear, selectedMonth).lengthOfMonth()
        val firstDayOfMonth = LocalDate.of(selectedYear, selectedMonth, 1).dayOfWeek.value

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Weekday headers
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                weekDays.forEach { day ->
                    Text(
                        text = day,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.fillMaxWidth(),
                content = {
                    // Empty cells before the first day of the month
                    for (i in 1 until firstDayOfMonth) {
                        item { Spacer(modifier = Modifier.size(40.dp)) }
                    }

                    // Render days of the selected month
                    for (day in 1..daysInMonth) {
                        val currentDate = safeLocalDate(selectedYear, selectedMonth, day)
                        val isSaved = moonList.any { it.date == currentDate.toString() }

                        item {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clickable {
                                        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
                                        navController.navigate("secondScreen?email=${currentUserEmail ?: ""}&date=$currentDate")
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = day.toString(),
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Color.Black
                                    )
                                    if (isSaved) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .background(Color.Red, CircleShape)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            )
        }
    }
}
