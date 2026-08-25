package com.tugymbro.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tugymbro.app.feature.chat.ChatScreen
import com.tugymbro.app.feature.home.HomeScreen
import com.tugymbro.app.feature.match.MatchScreen
import com.tugymbro.app.feature.onboarding.OnboardingScreen

object Routes {
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val MATCH = "match/{userId}"
    const val CHAT = "chat/{contactName}"

    fun match(userId: String) = "match/$userId"
    fun chat(contactName: String) = "chat/$contactName"
}

@Composable
fun TuGymBroNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Routes.ONBOARDING) {

        composable(Routes.ONBOARDING) {
            OnboardingScreen(
                onFinished = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                onMatchSelected = { match ->
                    navController.navigate(Routes.match(match.profile.id))
                }
            )
        }

        composable(
            route = Routes.MATCH,
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) {
            MatchScreen(
                onDismiss = { navController.popBackStack() },
                onRequestSent = { navController.navigate(Routes.chat("Fede")) }
            )
        }

        composable(
            route = Routes.CHAT,
            arguments = listOf(navArgument("contactName") { type = NavType.StringType })
        ) { backStackEntry ->
            val contactName = backStackEntry.arguments?.getString("contactName") ?: "Match"
            ChatScreen(contactName = contactName)
        }
    }
}
