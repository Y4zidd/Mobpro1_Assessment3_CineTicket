package com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.ui.auth.LoginScreen
import com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.ui.auth.LoginViewModel
import com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.ui.ticket.AddTicketScreen
import com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.ui.ticket.EditTicketScreen
import com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.ui.ticket.TicketListScreen
import com.yazidistiqlaladhyfadhillah607062430005.mobpro1_assessment3_cineticket.ui.ticket.TicketViewModel

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object AddTicket : Screen("add_ticket")
    object EditTicket : Screen("edit_ticket/{ticketId}") {
        fun createRoute(ticketId: String) = "edit_ticket/$ticketId"
    }
}

@Composable
fun NavGraph(
    navController: NavHostController,
    loginViewModel: LoginViewModel,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Home.route) {
            val ticketViewModel: TicketViewModel = viewModel()
            TicketListScreen(
                viewModel = ticketViewModel,
                onAddTicketClick = {
                    navController.navigate(Screen.AddTicket.route)
                },
                onLogout = {
                    loginViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onEditClick = { ticketId ->
                    navController.navigate(Screen.EditTicket.createRoute(ticketId))
                }
            )
        }
        composable(Screen.AddTicket.route) {
            val ticketViewModel: TicketViewModel = viewModel()
            AddTicketScreen(
                viewModel = ticketViewModel,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = Screen.EditTicket.route,
            arguments = listOf(navArgument("ticketId") { type = NavType.StringType })
        ) { backStackEntry ->
            val ticketId = backStackEntry.arguments?.getString("ticketId") ?: ""
            val ticketViewModel: TicketViewModel = viewModel()
            EditTicketScreen(
                ticketId = ticketId,
                viewModel = ticketViewModel,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
