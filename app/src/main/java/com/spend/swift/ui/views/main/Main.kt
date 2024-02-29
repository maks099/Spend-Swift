package com.spend.swift.ui.views.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.spend.swift.ui.theme.SpendSwiftTheme
import com.spend.swift.ui.views.Views
import com.spend.swift.ui.views.custom.BuysListScaffold
import com.spend.swift.ui.views.main.lists.ClosedBuys
import com.spend.swift.ui.views.main.lists.CurrentBuys
import com.spend.swift.ui.views.main.lists.Products
import com.spend.swift.ui.views.main.other.AccountSettings
import com.spend.swift.ui.views.main.other.BasicProducts
import com.spend.swift.ui.views.main.other.Categories

@Preview
@Composable
fun Main(){
    val navController = rememberNavController()
    SpendSwiftTheme {
        NavigationHost(navController = navController)
    }
}

@Composable
private fun NavigationHost(navController: NavHostController) {
    NavHost(navController, startDestination = BottomNavItem.CurrentBuys.path) { // BottomNavItem.CurrentBuys.path
        composable(BottomNavItem.CurrentBuys.path) {
            BuysListScaffold(navController = navController) {
                CurrentBuys(navController)
            }
        }
        composable(BottomNavItem.ClosedBuys.path) {
            BuysListScaffold(navController = navController) {
                ClosedBuys(navController)
            }
        }
        composable(Views.Account.path) {
            AccountSettings(navController)
        }
        composable(Views.Categories.path) { Categories(navController) }
        composable(Views.BasicGoods.path) { BasicProducts(navController) }
        composable(
            Views.Products.path,
            arguments = listOf(navArgument("listId") { type = NavType.StringType })
        ){backStackEntry ->
            backStackEntry.arguments?.getString("listId")?.let {
                Products(listId = it, navController)
            }
        }
    }
}


