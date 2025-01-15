package dev.christina.moonapp.ui

import androidx.compose.foundation.background
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
import dev.christina.moonapp.data.db.MoonEntity
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoonListScreen(navController: NavController, viewModel: MoonViewModel) {
    val moonList by viewModel.moonList.collectAsState(emptyList())

    val today = LocalDate.now()
    var selectedMonth by remember { mutableStateOf(today.monthValue) }
    var selectedYear by remember { mutableStateOf(today.year) }

    // Get days in the selected month and start day of the week
    val daysInMonth = YearMonth.of(selectedYear, selectedMonth).lengthOfMonth()
    val firstDayOfMonth = LocalDate.of(selectedYear, selectedMonth, 1).dayOfWeek.value % 7

    // Prepare options for year and month selectors
    val pastYears = (2020..today.year).toList().reversed()
    val pastMonths = Month.values()
        .filter { yearMatches(selectedYear, today) && it.value <= today.monthValue || selectedYear < today.year }
        .map { it.name.toLowerCase(Locale.ENGLISH).capitalize(Locale.ENGLISH) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Select Date",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StyledDropdownMenuField(
                                label = "Year",
                                selectedValue = selectedYear.toString(),
                                options = pastYears.map { it.toString() },
                                onValueSelected = { selectedYear = it.toInt() }
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            StyledDropdownMenuField(
                                label = "Month",
                                selectedValue = Month.of(selectedMonth).name.toLowerCase(Locale.ENGLISH)
                                    .capitalize(Locale.ENGLISH),
                                options = pastMonths,
                                onValueSelected = { selectedMonth = Month.valueOf(it.toUpperCase(Locale.ENGLISH)).value }
                            )
                        }
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.fillMaxWidth(),
                content = {
                    // Empty cells before the first day of the month
                    for (i in 0 until firstDayOfMonth) {
                        item { Spacer(modifier = Modifier.size(40.dp)) }
                    }

                    // Render days of the selected month
                    for (day in 1..daysInMonth) {
                        val currentDate = LocalDate.of(selectedYear, selectedMonth, day)
                        val isSaved = moonList.any { it.date == currentDate.toString() }

                        item {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clickable {
                                        navController.navigate("secondScreen/${currentDate}")
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

@Composable
fun StyledDropdownMenuField(
    label: String,
    selectedValue: String,
    options: List<String>,
    onValueSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Box(
            modifier = Modifier
                .background(Color.LightGray, CircleShape)
                .clickable { expanded = true }
                .padding(12.dp)
        ) {
            Text(
                text = selectedValue,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.wrapContentWidth()
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, fontSize = 16.sp, color = Color.Black) },
                    onClick = {
                        onValueSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

private fun yearMatches(selectedYear: Int, today: LocalDate): Boolean {
    return selectedYear == today.year
}
