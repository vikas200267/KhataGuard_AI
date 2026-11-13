package com.swagcoders.khataguard.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.swagcoders.khataguard.ui.screens.CreateCaseScreen
import com.swagcoders.khataguard.ui.screens.DiscrepancyScreen
import com.swagcoders.khataguard.ui.screens.EvidenceViewerScreen
import com.swagcoders.khataguard.ui.screens.ExtractedDetailsScreen
import com.swagcoders.khataguard.ui.screens.GraphScreen
import com.swagcoders.khataguard.ui.screens.HistoryScreen
import com.swagcoders.khataguard.ui.screens.HomeScreen
import com.swagcoders.khataguard.ui.screens.LoginScreen
import com.swagcoders.khataguard.ui.screens.ProcessingScreen
import com.swagcoders.khataguard.ui.screens.ReportScreen
import com.swagcoders.khataguard.ui.screens.SettingsScreen
import com.swagcoders.khataguard.ui.screens.StateSelectionScreen
import com.swagcoders.khataguard.ui.screens.UploadScreen
import com.swagcoders.khataguard.ui.screens.WelcomeScreen

@Composable
fun KhataGuardNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavRoutes.Splash,
        modifier = modifier,
    ) {
        composable(NavRoutes.Splash) {
            WelcomeScreen(
                onStartDemo = { navController.navigate(NavRoutes.Home) },
                onLogin = { navController.navigate(NavRoutes.Login) },
            )
        }

        composable(NavRoutes.Login) {
            LoginScreen(
                onLoginSuccess = { navController.navigate(NavRoutes.Home) },
                onSkipToDemo = { navController.navigate(NavRoutes.Home) },
            )
        }

        composable(NavRoutes.Home) {
            HomeScreen(
                onCreateCase = { navController.navigate(NavRoutes.CreateCase) },
                onOpenCase = { caseId -> navController.navigate(NavRoutes.discrepancies(caseId)) },
                onOpenHistory = { navController.navigate(NavRoutes.History) },
                onOpenSettings = { navController.navigate(NavRoutes.Settings) },
            )
        }

        composable(NavRoutes.CreateCase) {
            CreateCaseScreen(
                onCreated = { caseId ->
                    navController.navigate(NavRoutes.upload(caseId))
                },
                onBack = { navController.popBackStack() },
            )
        }

        composable(NavRoutes.StateSelection) {
            StateSelectionScreen(
                onSelectState = { _ -> navController.navigate(NavRoutes.CreateCase) },
                onBack = { navController.popBackStack() },
            )
        }

        composable(
            route = "${NavRoutes.Upload}/{caseId}",
            arguments = listOf(navArgument("caseId") { type = NavType.StringType }),
        ) { backStackEntry ->
            val caseId = backStackEntry.arguments?.getString("caseId") ?: "demo-case-001"
            UploadScreen(
                caseId = caseId,
                onProceedToAnalysis = { id -> navController.navigate(NavRoutes.processing(id)) },
            )
        }

        composable(
            route = "${NavRoutes.Processing}/{caseId}",
            arguments = listOf(navArgument("caseId") { type = NavType.StringType }),
        ) { backStackEntry ->
            val caseId = backStackEntry.arguments?.getString("caseId") ?: "demo-case-001"
            ProcessingScreen(
                caseId = caseId,
                onViewExtractedDetails = { id -> navController.navigate(NavRoutes.extractedDetails(id)) },
            )
        }

        composable(
            route = "${NavRoutes.ExtractedDetails}/{caseId}",
            arguments = listOf(navArgument("caseId") { type = NavType.StringType }),
        ) { backStackEntry ->
            val caseId = backStackEntry.arguments?.getString("caseId") ?: "demo-case-001"
            ExtractedDetailsScreen(
                caseId = caseId,
                onViewGraph = { id -> navController.navigate(NavRoutes.graph(id)) },
                onViewDiscrepancies = { id -> navController.navigate(NavRoutes.discrepancies(id)) },
            )
        }

        composable(
            route = "${NavRoutes.Graph}/{caseId}",
            arguments = listOf(navArgument("caseId") { type = NavType.StringType }),
        ) { backStackEntry ->
            val caseId = backStackEntry.arguments?.getString("caseId") ?: "demo-case-001"
            GraphScreen(
                caseId = caseId,
                onViewDiscrepancies = { id -> navController.navigate(NavRoutes.discrepancies(id)) },
            )
        }

        composable(
            route = "${NavRoutes.Discrepancies}/{caseId}",
            arguments = listOf(navArgument("caseId") { type = NavType.StringType }),
        ) { backStackEntry ->
            val caseId = backStackEntry.arguments?.getString("caseId") ?: "demo-case-001"
            DiscrepancyScreen(
                caseId = caseId,
                onViewEvidence = { id -> navController.navigate(NavRoutes.evidence(id)) },
                onViewReport = { id -> navController.navigate(NavRoutes.report(id)) },
            )
        }

        composable(
            route = "${NavRoutes.Evidence}/{caseId}",
            arguments = listOf(navArgument("caseId") { type = NavType.StringType }),
        ) { backStackEntry ->
            EvidenceViewerScreen(
                caseId = backStackEntry.arguments?.getString("caseId") ?: "demo-case-001",
                onBackToDiscrepancies = { navController.popBackStack() },
            )
        }

        composable(
            route = "${NavRoutes.Report}/{caseId}",
            arguments = listOf(navArgument("caseId") { type = NavType.StringType }),
        ) { backStackEntry ->
            val caseId = backStackEntry.arguments?.getString("caseId") ?: "demo-case-001"
            ReportScreen(
                caseId = caseId,
                onBackHome = {
                    navController.navigate(NavRoutes.Home) {
                        popUpTo(NavRoutes.Home) { inclusive = true }
                    }
                },
            )
        }

        composable(NavRoutes.History) {
            HistoryScreen(
                onSelectCase = { caseId -> navController.navigate(NavRoutes.discrepancies(caseId)) },
                onBackHome = { navController.popBackStack() },
            )
        }

        composable(NavRoutes.Settings) {
            SettingsScreen(
                onBackHome = { navController.popBackStack() },
            )
        }
    }
}
