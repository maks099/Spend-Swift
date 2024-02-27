package com.spend.swift.ui.views.main.lists

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun CurrentBuys(
    navController: NavController = rememberNavController()
){
    Text(text = "current")
}