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
import androidx.compose.material.icons.filled.Favorite
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dev.christina.moonapp.R
import dev.christina.moonapp.repository.FirebaseRepository
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoonListScreen(navController: NavController, moonViewModel: MoonViewModel, noteViewModel: NoteViewModel) {
    val savedDays by moonViewModel.savedDaysList.collectAsState()
    val notesDays by noteViewModel.notesDates.collectAsState()
    val today = LocalDate.now()
    var selectedMonth by remember { mutableStateOf(today.monthValue) }
    var selectedYear by remember { mutableStateOf(today.year) }

    // Define a consistent background color
    val backgroundColor = Color(0xFFF8F8F8) // Light gray for consistency
    val todayBackgroundColor = Color(0xFFE0E0E0) // Darker gray for the current day

    fun safeLocalDate(year: Int, month: Int, day: Int): LocalDate {
        val daysInMonth = YearMonth.of(year, month).lengthOfMonth()
        val safeDay = if (day > daysInMonth) daysInMonth else day
        return LocalDate.of(year, month, safeDay)
    }

    LaunchedEffect(selectedMonth, selectedYear) {
        moonViewModel.fetchMoonPhasesForMonth(YearMonth.of(selectedYear, selectedMonth))
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            noteViewModel.fetchNotesForMonth(uid, YearMonth.of(selectedYear, selectedMonth))
        }
    }

    LaunchedEffect(Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.uid?.let { uid ->
            moonViewModel.fetchSavedDays(uid, FirebaseRepository(FirebaseFirestore.getInstance()))
        }
    }

    val monthName = Month.of(selectedMonth).getDisplayName(TextStyle.FULL, Locale.ENGLISH)
    val weekDays = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    Scaffold(
        containerColor = backgroundColor, // Apply background color to the entire screen
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
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = backgroundColor // Ensure top bar matches background color
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController, currentScreen = "moonList")
        }
    ) { padding ->
        val daysInMonth = YearMonth.of(selectedYear, selectedMonth).lengthOfMonth()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(backgroundColor) // Set consistent background for calendar section
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
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor) // Set background for grid
            ) {
                val firstDayOffset =
                    (LocalDate.of(selectedYear, selectedMonth, 1).dayOfWeek.value - 1) % 7

                // Empty cells before the first day of the month
                for (i in 0 until firstDayOffset) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                        )
                    }
                }

                for (day in 1..daysInMonth) {
                    val currentDate = safeLocalDate(selectedYear, selectedMonth, day)
                    val isSaved = savedDays.contains(currentDate.toString())
                    val hasNotes = notesDays.contains(currentDate.toString())
                    val isToday = currentDate == today // Check if the day is today

                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .background(if (isToday) todayBackgroundColor else Color.Transparent), // Highlight current day
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            // Centered day with heart icon
                            Box(
                                modifier = Modifier
                                    .size(80.dp) // Consistent size
                                    .clickable {
                                        val currentUserEmail =
                                            FirebaseAuth.getInstance().currentUser?.email
                                        navController.navigate("secondScreen?email=${currentUserEmail ?: ""}&date=$currentDate")
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSaved) {
                                    Icon(
                                        imageVector = Icons.Filled.Favorite,
                                        contentDescription = "Saved Day",
                                        modifier = Modifier.size(40.dp), // Heart icon size
                                        tint = Color.Black
                                    )
                                }
                                // Day number always centered
                                Text(
                                    text = day.toString(),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = if (isSaved) Color.White else Color.Black,
                                    fontWeight = FontWeight.Normal
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp)) // Spacing between day and note

                            // Note icon appears only if hasNotes is true
                            if (hasNotes) {
                                Icon(
                                    painter = painterResource(id = R.drawable.note),
                                    contentDescription = "Notes Present",
                                    modifier = Modifier.size(24.dp),
                                    tint = Color.Black
                                )
                            } else {
                                Spacer(modifier = Modifier.height(24.dp)) // Placeholder to maintain alignment
                            }
                        }

                        Divider(color = Color.Black, thickness = 0.5.dp) // Add horizontal divider
                    }
                }
            }
        }
    }
}
