package dev.christina.moonapp.ui

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dev.christina.moonapp.R
import dev.christina.moonapp.data.db.NoteEntity
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecondScreen(
    navController: NavController,
    viewModel: MoonViewModel,
    date: String?,
    noteViewModel: NoteViewModel
) {
    val allMoonPhases = viewModel.allMoonPhases.collectAsState(emptyMap()).value
    val moonEntity = allMoonPhases[date]
    val selectedZodiac = viewModel.selectedZodiac.collectAsState().value
    val zodiacAdvice = viewModel.zodiacAdvice.collectAsState().value
    val isLoadingAdvice = viewModel.isLoadingAdvice.collectAsState().value

    LaunchedEffect(selectedZodiac, date) {
        if (!selectedZodiac.isNullOrBlank() && !date.isNullOrBlank()) {
            viewModel.fetchZodiacAdvice(sign = selectedZodiac, date = date)
        }
    }

    LaunchedEffect(date) {
        date?.let { noteViewModel.fetchNotesForDate(it) }
    }

    val notes = noteViewModel.notesForDate.collectAsState().value
    val noteInput = remember { mutableStateOf("") }
    val editingNote = remember { mutableStateOf<NoteEntity?>(null) }
    val isFavorite = viewModel.moonList.collectAsState().value.contains(moonEntity)

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
                            text = "MOON DETAILS",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Normal),
                            color = Color.Black
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
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

                        IconButton(onClick = { navController.navigate("moonList") }) {
                            Icon(Icons.Default.List, contentDescription = "Moon List", tint = Color.Black)
                        }
                    }
                }
            )
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
                                    fontSize = 50.sp,
                                    color = Color.Black
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Advice Section
                        Text(
                            text = "ADVICE TODAY",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Light,
                                fontSize = 30.sp,
                                color = Color.Black
                            ),
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isLoadingAdvice) {
                                CircularProgressIndicator()
                            } else if (zodiacAdvice?.data != null) {
                                Text(
                                    text = "\"${zodiacAdvice.data.horoscope_data}\"",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Normal,
                                        fontStyle = FontStyle.Italic,
                                        color = Color.Black
                                    ),
                                    textAlign = TextAlign.Center
                                )
                            } else {
                                Text(
                                    text = "Horoscope advice only available for the current date.",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Normal,
                                        fontStyle = FontStyle.Italic,
                                        color = Color.Gray
                                    ),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

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
