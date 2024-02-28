package com.spend.swift.ui.views.main.lists

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.spend.swift.R

@Composable
fun CurrentBuys(
    navController: NavController = rememberNavController()
){
    var showEditListDialog by remember { mutableStateOf(true) }
    Scaffold(
       floatingActionButton = {
           FloatingActionButton(onClick = { showEditListDialog = true }) {
               Icon(imageVector = Icons.Filled.Add, contentDescription = null)
           }
       }
    ) { paddingValues ->
       LazyColumn(
           modifier = Modifier
               .padding(paddingValues)
               .fillMaxSize()
       ){

       }
    }
    if(showEditListDialog){
        EditAlertDialog(onSuccess = { /*TODO*/ }) {
            showEditListDialog = false
        }
    }
}

@Composable
private fun EditAlertDialog(
    onSuccess: () -> Unit,
    onEnd: () -> Unit
){
    AlertDialog(
        onDismissRequest = onEnd,
        title = {
            Text(
                text = stringResource(id = R.string.edit),
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
            ) {

            }
        },
        confirmButton = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(imageVector = Icons.Filled.Check, contentDescription = null)
            }
        },
        dismissButton = {
            IconButton(onClick = onEnd) {
                Icon(imageVector = Icons.Filled.Close, contentDescription = null)
            }
        }
    )
}