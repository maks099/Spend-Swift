package com.spend.swift.ui.views.main.other

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.spend.swift.R
import com.spend.swift.ui.views.custom.SpecialTopAppBar

@Composable
fun BasicGoods(
    navController: NavController = rememberNavController()
){
    Scaffold(
        topBar = {
            SpecialTopAppBar(
                titleIcon = R.drawable.baseline_list_alt_24,
                titleText = R.string.basic_goods
            ){

            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
        ) {

        }
    }
}