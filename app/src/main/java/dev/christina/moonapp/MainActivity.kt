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
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import dev.christina.moonapp.data.db.MoonDatabase
import dev.christina.moonapp.repository.MoonRepository
import dev.christina.moonapp.repository.NoteRepository
import dev.christina.moonapp.ui.*
import dev.christina.moonapp.ui.theme.MoonAppTheme
import java.time.LocalDate

class MainActivity : ComponentActivity() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAnalytics = Firebase.analytics

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

    NavHost(navController = navController, startDestination = "welcomeScreen") {
        composable("welcomeScreen") {
            WelcomeScreen(navController)
        }
        composable("registerScreen") {
            RegisterScreen(navController, moonViewModel)
        }

        composable("loginScreen") {
            LoginScreen(navController, moonViewModel)
        }
        composable("zodiacScreen") {
            ZodiacScreen(navController, moonViewModel)
        }
        composable("firstScreen/{zodiac}") { backStackEntry ->
            val zodiac = backStackEntry.arguments?.getString("zodiac")
            FirstScreen(navController, moonViewModel, zodiac)
        }
        composable(
            "secondScreen",
        ) {
            SecondScreen(navController, moonViewModel, LocalDate.now().toString(), noteViewModel, null)
        }

        composable(
            route = "secondScreen/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email")
            SecondScreen(navController, moonViewModel, date = LocalDate.now().toString(), noteViewModel, email)
        }

        composable("moonList") {
            MoonListScreen(navController, moonViewModel)
        }
        composable("userZodiacScreen/{zodiacSign}") { backStackEntry ->
            val zodiacSign = backStackEntry.arguments?.getString("zodiacSign")
            zodiacSign?.let {
                moonViewModel.setSelectedZodiac(it) // Save zodiac in ViewModel
                UserZodiacScreen(navController, it)
            }
        }
    }
}