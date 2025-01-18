package dev.christina.moonapp.ui

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dev.christina.moonapp.R
import dev.christina.moonapp.data.db.NoteEntity
import dev.christina.moonapp.repository.FirebaseRepository
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecondScreen(
    navController: NavController,
    viewModel: MoonViewModel,
    date: String?,
    noteViewModel: NoteViewModel,
    email: String?
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Welcome, $email!",
            style = MaterialTheme.typography.titleLarge
        )
    }

    // Initialize or fetch current user ID
    val firebaseRepository = FirebaseRepository(FirebaseFirestore.getInstance())
    val currentUser = FirebaseAuth.getInstance().currentUser

    // Remember the current date state to react to navigation changes
    val currentDate = date ?: LocalDate.now().toString()
    val isCurrentDay = LocalDate.parse(currentDate).isEqual(LocalDate.now())


    LaunchedEffect(Unit) {
        currentUser?.let { user ->
            val uid = user.uid
            if (viewModel.selectedZodiac.value.isNullOrBlank()) {
                viewModel.getZodiacSign(uid, firebaseRepository)
            }
        }
    }
    // Collect states from ViewModel
    val allMoonPhases = viewModel.allMoonPhases.collectAsState(emptyMap()).value
    val moonEntity = allMoonPhases[currentDate]
    val selectedZodiac = viewModel.selectedZodiac.collectAsState().value
    val zodiacAdvice = viewModel.zodiacAdvice.collectAsState().value
    val weeklyZodiacAdvice = viewModel.weeklyZodiacAdvice.collectAsState().value
    val monthlyZodiacAdvice = viewModel.monthlyZodiacAdvice.collectAsState().value
    val isLoadingAdvice = viewModel.isLoadingAdvice.collectAsState().value


    // Track notes
    val notes = noteViewModel.notesForDate.collectAsState().value
    val noteInput = remember { mutableStateOf("") }
    val editingNote = remember { mutableStateOf<NoteEntity?>(null) }
    val isFavorite = viewModel.moonList.collectAsState().value.contains(moonEntity)

    // Option states for Daily, Weekly, Monthly advice
    val selectedOption = remember { mutableStateOf("Daily") }
    val dateRange = remember { mutableStateOf("") }
    val currentMonth = remember { mutableStateOf("") }


    // Calculate Weekly Range or Month Name Dynamically
    LaunchedEffect(selectedOption.value, currentDate) {
        val localDate = LocalDate.parse(currentDate)
        if (selectedOption.value == "Weekly") {
            val startOfWeek = localDate.with(java.time.DayOfWeek.MONDAY)
            val endOfWeek = localDate.with(java.time.DayOfWeek.SUNDAY)
            dateRange.value = "${startOfWeek.dayOfMonth}.${startOfWeek.monthValue}.${startOfWeek.year} - " +
                    "${endOfWeek.dayOfMonth}.${endOfWeek.monthValue}.${endOfWeek.year}"
        } else if (selectedOption.value == "Monthly") {
            currentMonth.value = localDate.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
        }
    }

    LaunchedEffect(date) {
        if (!date.isNullOrBlank()) {
            Log.d("SecondScreen", "Date changed to $date. Fetching new data.")
            viewModel.fetchZodiacAdvice(selectedZodiac ?: "", date)
            noteViewModel.fetchNotesForDate(date)
        }
    }

    // Fetch zodiac advice when selectedZodiac or currentDate changes
    LaunchedEffect(selectedZodiac, currentDate) {
        if (!selectedZodiac.isNullOrBlank()) {
            Log.d("SecondScreen", "Fetching advice for zodiac: $selectedZodiac and date: $currentDate")
            viewModel.fetchZodiacAdvice(selectedZodiac, currentDate)
        } else {
            Log.e("SecondScreen", "Selected Zodiac is null or blank, cannot fetch advice.")
        }
    }


    // Fetch notes when the date changes
    LaunchedEffect(currentDate) {
        noteViewModel.fetchNotesForDate(currentDate)
    }

    // Fetch moon phases for the current month only once
    LaunchedEffect(Unit) {
        val yearMonth = YearMonth.now()
        viewModel.fetchMoonPhasesForMonth(yearMonth)
    }

    // Debug logging
    LaunchedEffect(Unit) {
        viewModel.logAllMoonPhases()
    }

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
                                        .clickable { navController.navigate("zodiacScreen") }
                                )
                            }
                        }
                        Text(
                            text = "YOUR HOROSCOPE",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Normal),
                            color = Color.Black
                        )
                    }
                },
                actions = {
                    Row {
                        IconButton(
                            onClick = {
                                moonEntity?.let {
                                    if (isFavorite) {
                                        viewModel.removeFromMoonList(it)
                                    } else {
                                        viewModel.addToMoonList(it)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Toggle Favorite",
                                tint = Color.Black
                            )
                        }

                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController, currentScreen = "secondScreen")
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (moonEntity != null) {
                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFD3D3D3)),
                    shape = RoundedCornerShape(30.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(30.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val localDate = LocalDate.parse(moonEntity.date)
                        val dayOfWeek =
                            localDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                        val day = localDate.dayOfMonth
                        val month = localDate.month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                        val moonImage = getMoonPhaseImageResource(moonEntity.phase)

                        Image(
                            painter = painterResource(id = moonImage),
                            contentDescription = "Moon Phase Image",
                            modifier = Modifier.size(200.dp).padding(bottom = 16.dp)
                        )

                        Text(
                            text = moonEntity.phase,
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Light,
                                fontSize = 40.sp,
                                color = Color.Black
                            )
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            if (selectedOption.value == "Daily") {
                                Text(
                                    text = dayOfWeek,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 20.sp,
                                        color = Color.Black
                                    )
                                )
                                Text(
                                    text = "$day",
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        fontWeight = FontWeight.Light,
                                        fontSize = 80.sp,
                                        color = Color.Black
                                    )
                                )
                                Text(
                                    text = month.uppercase(),
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Light,
                                        fontSize = 16.sp,
                                        color = Color.Black
                                    )
                                )
                            } else if (selectedOption.value == "Weekly") {
                                Text(
                                    text = "Weekly Range",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 20.sp,
                                        color = Color.Black
                                    )
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = dateRange.value,
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        fontWeight = FontWeight.Light,
                                        fontSize = 25.sp,
                                        color = Color.Black
                                    )
                                )
                            } else if (selectedOption.value == "Monthly") {
                                Text(
                                    text = "Month",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 20.sp,
                                        color = Color.Black
                                    )
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = currentMonth.value.uppercase(),
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        fontWeight = FontWeight.Light,
                                        fontSize = 25.sp,
                                        color = Color.Black
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Underlines for Daily, Weekly, Monthly
                        if (isCurrentDay) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                            ) {
                                Text(
                                    text = "Daily",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        textDecoration = if (selectedOption.value == "Daily") TextDecoration.Underline else TextDecoration.None,
                                        color = if (selectedOption.value == "Daily") Color.Black else Color.Gray
                                    ),
                                    modifier = Modifier.clickable {
                                        selectedOption.value = "Daily"
                                        viewModel.fetchZodiacAdvice(selectedZodiac ?: "", date ?: "")
                                    }
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = "Weekly",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        textDecoration = if (selectedOption.value == "Weekly") TextDecoration.Underline else TextDecoration.None,
                                        color = if (selectedOption.value == "Weekly") Color.Black else Color.Gray
                                    ),
                                    modifier = Modifier.clickable {
                                        selectedOption.value = "Weekly"
                                        viewModel.fetchWeeklyZodiacAdvice(selectedZodiac ?: "", week = "1")
                                    }
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = "Monthly",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        textDecoration = if (selectedOption.value == "Monthly") TextDecoration.Underline else TextDecoration.None,
                                        color = if (selectedOption.value == "Monthly") Color.Black else Color.Gray
                                    ),
                                    modifier = Modifier.clickable {
                                        selectedOption.value = "Monthly"
                                        viewModel.fetchMonthlyZodiacAdvice(selectedZodiac ?: "")
                                    }
                                )
                            }
                        } else {
                            Text(
                                text = "Daily",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    textDecoration = TextDecoration.Underline,
                                    color = Color.Black
                                ),
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Advice Section
                        Text(
                            text = when {
                                isLoadingAdvice -> "Loading..."
                                LocalDate.parse(currentDate).isAfter(LocalDate.now()) -> "Horoscope is not available."
                                zodiacAdvice?.data != null -> "\"${zodiacAdvice.data.horoscope_data}\""
                                weeklyZodiacAdvice?.data != null -> "\"${weeklyZodiacAdvice.data.horoscope_data}\""
                                monthlyZodiacAdvice?.data != null -> "\"${monthlyZodiacAdvice.data.horoscope_data}\""
                                else -> "Horoscope advice unavailable."
                            },
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Normal,
                                fontStyle = FontStyle.Italic,
                                color = if (isLoadingAdvice) Color.Gray else Color.Black
                            ),
                            textAlign = TextAlign.Center
                        )


                        Spacer(modifier = Modifier.height(24.dp))

                        // Notes Section
                        Text(
                            text = "YOUR NOTES",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            ),
                            modifier = Modifier.align(Alignment.Start)
                        )

                        if (notes.isEmpty()) {
                            Text(
                                text = "Currently no saved notes",
                                style = MaterialTheme.typography.bodyLarge.copy(color = Color.Gray),
                                modifier = Modifier.align(Alignment.Start)
                            )
                        }

                        notes.forEach { note ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = note.content,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.weight(1f)
                                )
                                Row {
                                    IconButton(onClick = {
                                        noteInput.value = note.content
                                        editingNote.value = note
                                    }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                                    }
                                    IconButton(onClick = { noteViewModel.deleteNote(note) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "ADD NOTE",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            ),
                            modifier = Modifier.align(Alignment.Start)
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Color.White,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(4.dp)
                        ) {
                            BasicTextField(
                                value = noteInput.value,
                                onValueChange = { noteInput.value = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp),
                                decorationBox = { innerTextField ->
                                    Box(
                                        modifier = Modifier
                                            .padding(8.dp)
                                    ) {
                                        if (noteInput.value.isEmpty()) {
                                            Text(
                                                "Add a note",
                                                color = Color.Gray
                                            )
                                        }
                                        innerTextField()
                                    }
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Button(
                                onClick = {
                                    if (noteInput.value.isNotBlank()) {
                                        if (editingNote.value != null) {
                                            noteViewModel.updateNote(
                                                editingNote.value!!.copy(content = noteInput.value)
                                            )
                                            editingNote.value = null
                                        } else {
                                            noteViewModel.addNote(
                                                NoteEntity(
                                                    date = date ?: "",
                                                    content = noteInput.value
                                                )
                                            )
                                        }
                                        noteInput.value = ""
                                    }
                                },
                                modifier = Modifier.align(Alignment.Center),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                            ) {
                                Text(
                                    "SAVE",
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun getMoonPhaseImageResource(phase: String): Int {
    return when (phase) {
        "Full Moon" -> R.drawable.moon_phases_fullmoon
        "First Quarter" -> R.drawable.moon_phases_firstquarter
        "Third Quarter" -> R.drawable.moon_phases_thirdquarter
        "New Moon" -> R.drawable.moon_phases_newmoon
        "Dark Moon" -> R.drawable.moon_phases_newmoon
        "Waning Gibbous" -> R.drawable.moon_phases_waninggibbous
        "Waning Crescent" -> R.drawable.moon_phases_waningcrescent
        "Waxing Crescent" -> R.drawable.moon_phases_waxingcrescent
        "Waxing Gibbous" -> R.drawable.moon_phases_waxinggibbous
        else -> R.drawable.ic_launcher_foreground
    }
}

@Composable
fun BottomNavigationBar(navController: NavController, currentScreen: String) {
    BottomAppBar(
        containerColor = Color(0xFFD3D3D3),
        modifier = Modifier.height(64.dp)
    ) {
        val items = listOf(
            BottomNavItem("Horoscope", R.drawable.horoscope, "secondScreen"),
            BottomNavItem("Calendar", R.drawable.calendar, "moonList"),
            BottomNavItem("Profile", R.drawable.user, "profileSettingsScreen")
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                IconButton(
                    onClick = {
                        if (currentScreen != item.route) {
                            navController.navigate(item.route)
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = item.label,
                        tint = if (currentScreen == item.route) Color.Black else Color.Gray,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

data class BottomNavItem(val label: String, val icon: Int, val route: String)