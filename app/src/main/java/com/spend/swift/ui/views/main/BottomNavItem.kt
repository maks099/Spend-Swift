package com.spend.swift.ui.views.main

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.spend.swift.R
import com.spend.swift.ui.views.Views
import com.spend.swift.ui.views.custom.BuysListScaffold
import com.spend.swift.ui.views.main.lists.ClosedBuys
import com.spend.swift.ui.views.main.lists.CurrentBuys

sealed class BottomNavItem(val path: String, @DrawableRes val icon: Int, @StringRes val label: Int) {
     data object CurrentBuys : BottomNavItem("current_buys", R.drawable.baseline_shopping_cart_checkout_24, R.string.current_buys)
     data object ClosedBuys : BottomNavItem("closed_buys", R.drawable.baseline_shopping_cart_24, R.string.closed_buys)
}