package com.spend.swift.ui.views.main

import androidx.compose.foundation.layout.padding

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.spend.swift.R
import com.spend.swift.ui.theme.SpendSwiftTheme
import com.spend.swift.ui.views.Views
import com.spend.swift.ui.views.custom.BuysListScaffold
import com.spend.swift.ui.views.main.lists.ClosedBuys
import com.spend.swift.ui.views.main.lists.CurrentBuys
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
    NavHost(navController, startDestination = Views.Categories.path) { // BottomNavItem.CurrentBuys.path
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
            androidx.compose.material3.Text(text = "account", )
        }
        composable(Views.Categories.path) { Categories(navController) }
        composable(Views.BasicGoods.path) { androidx.compose.material3.Text(text = "basic goods") }
    }
}


