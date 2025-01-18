package dev.christina.moonapp.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
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
import com.google.firebase.firestore.FirebaseFirestore
import dev.christina.moonapp.data.db.MoonEntity
import dev.christina.moonapp.repository.FirebaseRepository
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*
import java.time.Month
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.res.painterResource
import dev.christina.moonapp.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoonListScreen(navController: NavController, moonViewModel: MoonViewModel, noteViewModel: NoteViewModel) {
    val savedDays by moonViewModel.savedDaysList.collectAsState() // Collect saved days
    val notesDays by noteViewModel.notesDates.collectAsState() // Collect note days
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

            // Adjust grid height to take up the full screen height
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.fillMaxSize()
            ) {
                // Calculate the first day offset (aligns with weekday headers)
                val firstDayOffset = (LocalDate.of(selectedYear, selectedMonth, 1).dayOfWeek.value - 1) % 7

                // Empty cells before the first day of the month
                for (i in 0 until firstDayOffset) {
                    item {
                        Spacer(modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)) // Match cell height
                    }
                }

                // Render days of the selected month
                for (day in 1..daysInMonth) {
                    val currentDate = safeLocalDate(selectedYear, selectedMonth, day)
                    val isSaved = savedDays.contains(currentDate.toString())
                    val hasNotes = notesDays.contains(currentDate.toString())

                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp) // Ensure consistent height
                                .clickable {
                                    val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
                                    navController.navigate("secondScreen?email=${currentUserEmail ?: ""}&date=$currentDate")
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceEvenly, // Ensure even spacing
                                modifier = Modifier.fillMaxHeight()
                            ) {
                                // Placeholder for the heart icon (Saved Day)
                                if (isSaved) {
                                    Icon(
                                        imageVector = Icons.Filled.Favorite,
                                        contentDescription = "Saved Day",
                                        modifier = Modifier.size(20.dp),
                                        tint = Color.Black
                                    )
                                } else {
                                    Spacer(modifier = Modifier.size(20.dp)) // Placeholder to keep alignment
                                }

                                // Day number
                                Text(
                                    text = day.toString(),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.Black
                                )

                                // Placeholder for the note icon
                                if (hasNotes) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.note),
                                        contentDescription = "Notes Present",
                                        modifier = Modifier.size(20.dp),
                                        tint = Color.Black
                                    )
                                } else {
                                    Spacer(modifier = Modifier.size(20.dp)) // Placeholder to keep alignment
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
