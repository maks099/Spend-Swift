package com.spend.swift.ui.views.main.lists

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.spend.swift.DEFAULT_ICON_ID
import com.spend.swift.R
import com.spend.swift.SpendSwiftApp
import com.spend.swift.helpers.SharedKeys
import com.spend.swift.helpers.SharedPrefsHelper
import com.spend.swift.helpers.asDate
import com.spend.swift.helpers.getTimeMillisNextDay
import com.spend.swift.helpers.toast
import com.spend.swift.model.Category
import com.spend.swift.model.ShoppingList
import com.spend.swift.ui.views.Views
import com.spend.swift.ui.views.custom.CategoryDropdownMenu
import com.spend.swift.ui.views.custom.RemoveDialog
import com.spend.swift.ui.views.custom.TextBox
import com.spend.swift.ui.views.main.other.drawableToBitmap
import java.util.Date

@Composable
fun CurrentBuys(
    navController: NavController = rememberNavController()
){
    val viewModel: CurrentBuysViewModel = viewModel()

    val shoppingLists by viewModel.shoppingLists.collectAsState(emptyList())
    var shoppingList by remember { mutableStateOf(ShoppingList.getTemplate()) }

    val categories by viewModel.categories.collectAsState(listOf(Category.getTemplate()))
    var showEditListDialog by remember { mutableStateOf(false) }
    var showDeleteListDialog by remember { mutableStateOf(false) }

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
               .padding(top = 8.dp)
       ){
           items(shoppingLists){ item ->
               ShoppingListRow(
                   modifier = Modifier
                       .clickable {
                           shoppingList = item
                           showEditListDialog = true
                       },
                   shoppingList = item,
                   category = if(item.categoryId.isEmpty()) null
                   else categories.find { it.docId == item.categoryId }
               ){
                   navController.navigate(Views.Products.path.replace("{listId}", item.docId))
               }
           }
       }
    }
    if(showEditListDialog){
        EditAlertDialog(
            shoppingListObj = shoppingList,
            categories = categories,
            onSuccess = { newShoppingList ->
                showEditListDialog = false
                viewModel.save(newShoppingList)
            },
            onDelete = {
                showDeleteListDialog = true
            },
        ) {
            showEditListDialog = false
        }
    }

    if (showDeleteListDialog){
        RemoveDialog(
            onConfirm = {
                showEditListDialog = false
                showDeleteListDialog = false
                viewModel.delete(shoppingList)
                shoppingList = ShoppingList.getTemplate()
            }
        ){
            showDeleteListDialog = false
        }
    }
}

@Composable
private fun ShoppingListRow(
    modifier: Modifier = Modifier,
    shoppingList: ShoppingList,
    category: Category?,
    onOpen: () -> Unit
){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .shadow(2.dp, RoundedCornerShape(5.dp))
            .padding(horizontal = 12.dp, vertical = 16.dp)

    ) {
        Column(

            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = shoppingList.name,
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "${stringResource(id = R.string.buy_up_to)} ${shoppingList.completionDate.asDate()}",
                fontSize = 12.sp
            )
            Text(
                text = "(${stringResource(R.string.by)} ${shoppingList.createdBy})",
                fontSize = 14.sp
            )

        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            category?.let {
                val bitmap = SpendSwiftApp.iconPack.icons[it.iconId]?.drawable?.drawableToBitmap()
                bitmap?.let {
                    Icon(
                        bitmap = it.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier
                    )
                }
                Text(
                    text = "(${category.name})",
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
            }
            OutlinedIconButton(onClick = onOpen) {
                Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = null)
            }

        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditAlertDialog(
    shoppingListObj: ShoppingList,
    categories: List<Category>,
    onSuccess: (ShoppingList) -> Unit,
    onDelete: () -> Unit,
    onEnd: () -> Unit
){
    var shoppingList by remember { mutableStateOf(shoppingListObj) }
    var selectedCategory by remember { mutableStateOf(
        if(shoppingListObj.categoryId == "") categories[0]
        else categories.find { it.docId == shoppingListObj.categoryId } ?: categories[0]
    ) }

    val datePickerState = rememberDatePickerState(selectableDates = object : SelectableDates {
        override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            return utcTimeMillis >= Date().time
        }
    }, initialSelectedDateMillis = getTimeMillisNextDay(),
        initialDisplayMode = DisplayMode.Input,
    )

    AlertDialog(
        modifier = Modifier.fillMaxWidth(),
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
                TextBox(
                    title = R.string.shopping_list_name,
                    initialValue = shoppingList.name,
                    keyboardType = KeyboardType.Text,
                    onChange = { shoppingList = shoppingList.copy(name = it)}
                )
                Spacer(modifier = Modifier.height(16.dp))
                CategoryDropdownMenu(
                    selected = selectedCategory,
                    items = categories,
                    onClick = {
                        selectedCategory = it
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                DatePicker(
                    state = datePickerState,
                    showModeToggle = false,
                    title = null,
                    colors = DatePickerDefaults.colors(
                        selectedDayContainerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
                if(shoppingListObj.docId.isNotEmpty()){
                    TextButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .padding(top = 8.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.remove),
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = Color.Red,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            IconButton(onClick = {
                if(shoppingList.name.trim().isEmpty()){
                    SpendSwiftApp.getCtx().toast(R.string.min_lenght)
                    return@IconButton
                }
                shoppingList = shoppingList.copy(
                    categoryId = selectedCategory.docId,
                    completionDate = datePickerState.selectedDateMillis ?: 0
                )
                onSuccess(shoppingList)
            }) {
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