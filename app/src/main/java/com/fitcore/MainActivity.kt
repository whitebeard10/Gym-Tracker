package com.fitcore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.fitcore.presentation.home.HomeScreen
import com.fitcore.presentation.log.FoodLogScreen
import com.fitcore.presentation.meals.MealPlanScreen
import com.fitcore.presentation.progress.ProgressScreen
import com.fitcore.presentation.settings.SettingsScreen
import com.fitcore.presentation.theme.DeepDarkBackground
import com.fitcore.presentation.theme.FitCoreTheme
import com.fitcore.presentation.workout.WorkoutScreen
import dagger.hilt.android.AndroidEntryPoint

import androidx.compose.runtime.collectAsState
import com.fitcore.data.repository.UserPreferencesRepository
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userPreferencesRepository: UserPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val isDarkTheme by userPreferencesRepository.darkThemeFlow.collectAsState(initial = true)
            FitCoreTheme(darkTheme = isDarkTheme) {
                FitCoreNavigation()
            }
        }
    }
}

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object MealPlan : Screen("meal_plan", "Meals", Icons.Default.RestaurantMenu)
    object FoodLog : Screen("food_log", "Log", Icons.Default.Timeline)
    object Workout : Screen("workout", "Workout", Icons.Default.FitnessCenter)
    object Progress : Screen("progress", "Progress", Icons.Default.Timeline)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}

@Composable
fun FitCoreNavigation() {
    val navController = rememberNavController()
    val items = listOf(
        Screen.Home,
        Screen.MealPlan,
        Screen.Workout,
        Screen.Progress,
        Screen.Settings
    )

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            
            val isFoodLog = currentDestination?.route?.startsWith(Screen.FoodLog.route) == true
            
            if (!isFoodLog) {
                NavigationBar(
                    containerColor = DeepDarkBackground,
                    contentColor = Color.White
                ) {
                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = null) },
                            label = { Text(screen.title) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { 
                HomeScreen(
                    onNavigateToLog = { navController.navigate(Screen.FoodLog.route) },
                    onNavigateToMealPlan = { navController.navigate(Screen.MealPlan.route) },
                    onNavigateToWorkout = { navController.navigate(Screen.Workout.route) }
                ) 
            }
            composable(Screen.MealPlan.route) { 
                MealPlanScreen(
                    onBackClick = { navController.popBackStack() },
                    onNavigateToLog = { mealId -> navController.navigate("${Screen.FoodLog.route}?mealId=$mealId") }
                ) 
            }
            composable(
                route = "${Screen.FoodLog.route}?mealId={mealId}",
                arguments = listOf(navArgument("mealId") { nullable = true })
            ) { backStackEntry ->
                val mealId = backStackEntry.arguments?.getString("mealId")
                FoodLogScreen(
                    mealId = mealId,
                    onBackClick = { navController.popBackStack() }
                ) 
            }
            composable(Screen.Workout.route) { 
                WorkoutScreen(onBackClick = { navController.popBackStack() }) 
            }
            composable(Screen.Progress.route) { 
                ProgressScreen(onBackClick = { navController.popBackStack() }) 
            }
            composable(Screen.Settings.route) { 
                SettingsScreen(onBackClick = { navController.popBackStack() }) 
            }
        }
    }
}
