package com.example.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.ui.RoomMateXViewModel
import com.example.ui.achievements.AchievementsScreen
import com.example.ui.analytics.AnalyticsScreen
import com.example.ui.auth.LoginScreen
import com.example.ui.auth.RegisterScreen
import com.example.ui.balances.BalancesScreen
import com.example.ui.chores.ChoresScreen
import com.example.ui.dashboard.DashboardScreen
import com.example.ui.expenses.ExpensesScreen
import com.example.ui.house.HouseManagementScreen
import com.example.ui.leaderboard.LeaderboardScreen
import com.example.ui.onboarding.OnboardingScreen
import com.example.ui.polls.HousePollsScreen
import com.example.ui.profile.ProfileScreen
import com.example.ui.search.GlobalSearchScreen
import com.example.ui.shopping.ShoppingListScreen
import com.example.ui.theme.*

sealed class Screen(val route: String, val title: String = "", val icon: ImageVector? = null) {
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Register : Screen("register")

    // Main Bottom Nav Destinations
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Chores : Screen("chores", "Chores", Icons.Default.CleaningServices)
    object Expenses : Screen("expenses", "Expenses", Icons.Default.ReceiptLong)
    object Leaderboard : Screen("leaderboard", "Ranks", Icons.Default.EmojiEvents)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)

    // Secondary
    object Balances : Screen("balances")
    object Shopping : Screen("shopping")
    object Polls : Screen("polls")
    object Achievements : Screen("achievements")
    object Analytics : Screen("analytics")
    object HouseSettings : Screen("house_settings")
    object GlobalSearch : Screen("global_search")
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.Chores,
    Screen.Expenses,
    Screen.Leaderboard,
    Screen.Profile
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomMateXNavHost(
    viewModel: RoomMateXViewModel
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val toastMessage by viewModel.toastMessage.collectAsState()
    val levelUpDialog by viewModel.levelUpDialog.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(toastMessage) {
        toastMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearToast()
        }
    }

    val showBottomBar = currentRoute in bottomNavItems.map { it.route }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = DarkSurface,
                    contentColor = Color.White,
                    tonalElevation = 8.dp
                ) {
                    bottomNavItems.forEach { item ->
                        val isSelected = currentRoute == item.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = item.icon!!,
                                    contentDescription = item.title,
                                    tint = if (isSelected) ElectricCyan else Color.Gray
                                )
                            },
                            label = {
                                Text(
                                    text = item.title,
                                    color = if (isSelected) ElectricCyan else Color.Gray,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            modifier = Modifier.testTag("nav_item_${item.route}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Onboarding.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onFinishOnboarding = { navController.navigate(Screen.Login.route) }
                )
            }

            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = { navController.navigate(Screen.Home.route) { popUpTo(Screen.Login.route) { inclusive = true } } },
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                    onForgotPassword = {}
                )
            }

            composable(Screen.Register.route) {
                RegisterScreen(
                    onRegisterSuccess = { navController.navigate(Screen.Home.route) { popUpTo(Screen.Register.route) { inclusive = true } } },
                    onNavigateToLogin = { navController.navigate(Screen.Login.route) }
                )
            }

            // Main Screens
            composable(Screen.Home.route) {
                DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToExpenses = { navController.navigate(Screen.Expenses.route) },
                    onNavigateToChores = { navController.navigate(Screen.Chores.route) },
                    onNavigateToBalances = { navController.navigate(Screen.Balances.route) },
                    onNavigateToShopping = { navController.navigate(Screen.Shopping.route) },
                    onNavigateToLeaderboard = { navController.navigate(Screen.Leaderboard.route) },
                    onNavigateToHouseSettings = { navController.navigate(Screen.HouseSettings.route) },
                    onOpenGlobalSearch = { navController.navigate(Screen.GlobalSearch.route) },
                    onOpenNotifications = { navController.navigate(Screen.Polls.route) }
                )
            }

            composable(Screen.Chores.route) {
                ChoresScreen(viewModel = viewModel)
            }

            composable(Screen.Expenses.route) {
                ExpensesScreen(viewModel = viewModel)
            }

            composable(Screen.Leaderboard.route) {
                LeaderboardScreen(viewModel = viewModel)
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    viewModel = viewModel,
                    onNavigateToAchievements = { navController.navigate(Screen.Achievements.route) },
                    onNavigateToAnalytics = { navController.navigate(Screen.Analytics.route) },
                    onNavigateToHouseSettings = { navController.navigate(Screen.HouseSettings.route) },
                    onLogout = { navController.navigate(Screen.Login.route) { popUpTo(0) } }
                )
            }

            // Secondary Screens
            composable(Screen.Balances.route) {
                BalancesScreen(viewModel = viewModel)
            }

            composable(Screen.Shopping.route) {
                ShoppingListScreen(viewModel = viewModel)
            }

            composable(Screen.Polls.route) {
                HousePollsScreen(viewModel = viewModel)
            }

            composable(Screen.Achievements.route) {
                AchievementsScreen(viewModel = viewModel)
            }

            composable(Screen.Analytics.route) {
                AnalyticsScreen(viewModel = viewModel)
            }

            composable(Screen.HouseSettings.route) {
                HouseManagementScreen(viewModel = viewModel, onNavigateBack = { navController.popBackStack() })
            }

            composable(Screen.GlobalSearch.route) {
                GlobalSearchScreen(viewModel = viewModel, onNavigateBack = { navController.popBackStack() })
            }
        }

        levelUpDialog?.let { (lvl, title) ->
            com.example.ui.components.LevelUpCelebrationDialog(
                level = lvl,
                title = title,
                onDismiss = { viewModel.dismissLevelUpDialog() }
            )
        }
    }
}
