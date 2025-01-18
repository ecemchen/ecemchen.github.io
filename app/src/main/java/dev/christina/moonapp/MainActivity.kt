package dev.christina.moonapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dev.christina.moonapp.data.db.MoonDatabase
import dev.christina.moonapp.repository.MoonRepository
import dev.christina.moonapp.repository.NoteRepository
import dev.christina.moonapp.ui.*
import dev.christina.moonapp.ui.theme.MoonAppTheme
import java.time.LocalDate

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = MoonDatabase.getDatabase(this)
        val moonRepository = MoonRepository(database.moonDao())
        val noteRepository = NoteRepository(database.noteDao()) // Initialize NoteRepository

        setContent {
            MoonAppTheme {
                MyApp(moonRepository, noteRepository)
            }
        }
    }
}

@Composable
fun MyApp(moonRepository: MoonRepository, noteRepository: NoteRepository) {
    val navController = rememberNavController()
    val moonViewModel: MoonViewModel = viewModel(factory = ViewModelFactoryProvider(moonRepository))
    val noteViewModel: NoteViewModel = viewModel(factory = NoteViewModelFactoryProvider(noteRepository))

    NavHost(navController = navController, startDestination = "welcomeScreen") {
        composable("welcomeScreen") {
            WelcomeScreen(navController)
        }
        composable("registerScreen") {
            RegisterScreen(navController) // Use the correct function signature
        }
        composable("loginScreen") {
            LoginScreen(navController)
        }
        composable("secondScreen?email={email}&date={date}",
            arguments = listOf(
                navArgument("email") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("date") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = LocalDate.now().toString()
                }
            )
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email")
            val date = backStackEntry.arguments?.getString("date") ?: LocalDate.now().toString()
            SecondScreen(navController, moonViewModel, date, noteViewModel, email)
        }
        composable("moonList") {
            MoonListScreen(navController, moonViewModel)
        }
        composable("profileSettingsScreen") {
            ProfileSettingsScreen(navController, moonViewModel)
        }

        composable(
            "userZodiacScreen/{zodiacSign}",
            arguments = listOf(
                navArgument("zodiacSign") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val zodiacSign = backStackEntry.arguments?.getString("zodiacSign") ?: "Unknown"
            UserZodiacScreen(navController, zodiacSign)
        }
    }
}
