package dev.christina.moonapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.christina.moonapp.data.db.MoonDatabase
import dev.christina.moonapp.repository.MoonRepository
import dev.christina.moonapp.repository.NoteRepository
import dev.christina.moonapp.ui.*
import dev.christina.moonapp.ui.theme.MoonAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = MoonDatabase.getDatabase(this)
        val moonRepository = MoonRepository(database.moonDao())
        val noteRepository = NoteRepository(database.noteDao()) // Initialize NoteRepository

        setContent {
            MoonAppTheme {
                MyApp(moonRepository, noteRepository) // Pass both repositories to MyApp
            }
        }
    }
}

@Composable
fun MyApp(moonRepository: MoonRepository, noteRepository: NoteRepository) {
    val navController = rememberNavController()
    val moonViewModel: MoonViewModel = viewModel(factory = ViewModelFactoryProvider(moonRepository))
    val noteViewModel: NoteViewModel = viewModel(factory = NoteViewModelFactoryProvider(noteRepository))

    NavHost(navController = navController, startDestination = "zodiacScreen") {
        composable("zodiacScreen") {
            ZodiacScreen(navController, moonViewModel)
        }

        composable("firstScreen/{zodiac}") { backStackEntry ->
            val zodiac = backStackEntry.arguments?.getString("zodiac")
            FirstScreen(navController, moonViewModel, zodiac)
        }

        composable("secondScreen/{date}") { backStackEntry ->
            val date = backStackEntry.arguments?.getString("date")
            SecondScreen(navController, moonViewModel, date, noteViewModel) // Pass noteViewModel
        }

        composable("moonList") {
            MoonListScreen(navController, moonViewModel)
        }
    }
}
