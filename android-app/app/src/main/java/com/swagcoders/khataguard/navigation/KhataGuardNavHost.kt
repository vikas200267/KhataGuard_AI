package com.swagcoders.khataguard.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.swagcoders.khataguard.ui.screens.BasicScreen

@Composable
fun KhataGuardNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = NavRoutes.Splash, modifier = modifier) {
        composable(NavRoutes.Splash) { BasicScreen("Welcome to KhataGuard AI", "DEMO MODE") { navController.navigate(NavRoutes.Login) } }
        composable(NavRoutes.Login) { BasicScreen("Login", "Use Firebase Auth in production") { navController.navigate(NavRoutes.Home) } }
        composable(NavRoutes.Home) { BasicScreen("Home Dashboard", "Start a case") { navController.navigate(NavRoutes.CreateCase) } }
        composable(NavRoutes.CreateCase) { BasicScreen("Create Property Case", "Capture core inputs") { navController.navigate(NavRoutes.StateSelection) } }
        composable(NavRoutes.StateSelection) { BasicScreen("State & Document Category", "Karnataka / Maharashtra / Tamil Nadu") { navController.navigate(NavRoutes.Upload) } }
        composable(NavRoutes.Upload) { BasicScreen("Upload Document", "CameraX / Photo Picker / PDF") { navController.navigate(NavRoutes.Processing) } }
        composable(NavRoutes.Processing) { BasicScreen("Processing Status", "Analyzing docs") { navController.navigate(NavRoutes.ExtractedDetails) } }
        composable(NavRoutes.ExtractedDetails) { BasicScreen("Extracted Property Details", "Owner, survey no, area") { navController.navigate(NavRoutes.Graph) } }
        composable(NavRoutes.Graph) { BasicScreen("Property Identity Graph", "Linked records view") { navController.navigate(NavRoutes.Discrepancies) } }
        composable(NavRoutes.Discrepancies) { BasicScreen("Discrepancy Results", "Evidence linked") { navController.navigate(NavRoutes.Report) } }
        composable(NavRoutes.Report) { BasicScreen("Pre-check Report", "PDF-ready summary") { navController.navigate(NavRoutes.History) } }
        composable(NavRoutes.History) { BasicScreen("Case History", "Recent analyses") { navController.navigate(NavRoutes.Settings) } }
        composable(NavRoutes.Settings) { BasicScreen("Settings & Privacy", "Data controls") { navController.navigate(NavRoutes.Home) } }
    }
}
