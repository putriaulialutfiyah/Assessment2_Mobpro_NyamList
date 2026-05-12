package com.putri0010.nyamlist.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.putri0010.nyamlist.ui.screen.AddEditScreen
import com.putri0010.nyamlist.ui.screen.MainScreen
import com.putri0010.nyamlist.ui.screen.RecycleBinScreen

@Composable
fun SetupNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(route = Screen.Home.route) {
            MainScreen(navController)
        }
        composable (route = Screen.FormBaru.route) {
            AddEditScreen(navController)
        }
        composable (
            route = Screen.FormUbah.route,
            arguments = listOf(
                navArgument(KEY_ID_WISHLIST) { type = NavType.LongType }
            )
        ) { navBackStackEntry ->
            val id = navBackStackEntry.arguments?.getLong(KEY_ID_WISHLIST)
            AddEditScreen(navController, id)
        }
        composable(Screen.RecycleBin.route) {
            RecycleBinScreen(navController)
        }
    }
}