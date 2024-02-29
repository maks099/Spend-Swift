package com.spend.swift.ui.views.custom

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.spend.swift.R
import com.spend.swift.ui.views.Views
import com.spend.swift.ui.views.main.BottomNavItem

@Composable
fun BuysListScaffold(
    navController: NavController,
    content: @Composable()() -> Unit
){
    Scaffold(
        topBar = {
            val actions = listOf(
                ActionItem(R.drawable.baseline_account_circle_24, R.string.account) { navController.navigate(
                    Views.Account.path) },
                ActionItem(R.drawable.baseline_category_24, R.string.categories) { navController.navigate(
                    Views.Categories.path) },
                ActionItem(R.drawable.baseline_list_alt_24, R.string.basic_goods) { navController.navigate(
                    Views.BasicGoods.path) },
            )
            MainTopAppBar(actions)
        },
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
        ) {
            content()
        }
    }
}

@Composable
private fun MainTopAppBar(
    actions: List<ActionItem>
) {
    var showMenu by remember { mutableStateOf(false) }
    TopAppBar(
        backgroundColor = MaterialTheme.colorScheme.primaryContainer,
        title = {
            Icon(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .padding(end = 8.dp)
            )
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        },
        actions = {
            IconButton(onClick = actions[0].onClick) {
                Icon(
                    painterResource(id = actions[0].icon),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = {
                showMenu = true
            }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                actions.drop(1).forEachIndexed{ index, actionItem ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = stringResource(id = actionItem.text),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        },
                        trailingIcon = {
                            Icon(
                                painter = painterResource(id = actionItem.icon),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        onClick = {
                            showMenu = false
                            actionItem.onClick()
                        }
                    )
                    if(index < actions.size - 2){
                        Spacer(modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(
                                MaterialTheme.colorScheme.primary
                            ))
                    }
                }


            }
        }
    )
}

@Composable
private fun BottomNavigationBar(navController: NavController) {
    BottomNavigation(
        backgroundColor = MaterialTheme.colorScheme.primaryContainer
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        listOf(
            BottomNavItem.CurrentBuys,
            BottomNavItem.ClosedBuys,
        ).forEach { item ->
            BottomNavigationItem(
                selected = currentRoute == item.path,
                onClick = {
                    navController.navigate(item.path) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                unselectedContentColor = Color.Gray,
                icon = { Icon(
                    painterResource(id = item.icon),
                    tint = if(currentRoute == item.path) MaterialTheme.colorScheme.primary else Color.Gray,
                    contentDescription = null
                ) },
                label = { Text(stringResource(id = item.label)) }
            )
        }
    }
}


private data class ActionItem(
    @DrawableRes val icon: Int,
    @StringRes val text: Int,
    val onClick: () -> Unit
)
