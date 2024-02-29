package com.spend.swift.ui.views.main.lists

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.spend.swift.DEFAULT_ICON_ID
import com.spend.swift.R
import com.spend.swift.SpendSwiftApp
import com.spend.swift.helpers.asDate
import com.spend.swift.helpers.getTimeByFilterProperty
import com.spend.swift.helpers.getTimeMillisNextDay
import com.spend.swift.helpers.toast
import com.spend.swift.model.Category
import com.spend.swift.model.Product
import com.spend.swift.model.ShoppingList
import com.spend.swift.ui.theme.SpendSwiftTheme
import com.spend.swift.ui.views.Views
import com.spend.swift.ui.views.custom.CategoryDropdownMenu
import com.spend.swift.ui.views.custom.LoadingDialog
import com.spend.swift.ui.views.custom.RemoveDialog
import com.spend.swift.ui.views.custom.TextBox
import com.spend.swift.ui.views.main.other.drawableToBitmap
import kotlinx.coroutines.delay
import java.util.Date

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun CurrentBuys(
    navController: NavController = rememberNavController()
){
    val viewModel: BuysViewModel = viewModel()
    val categories by viewModel.categories.collectAsState(listOf(Category(SpendSwiftApp.getCtx().getString(R.string.all), DEFAULT_ICON_ID, "")))

    val products by viewModel.productsLists.collectAsState(emptyMap())
    val shoppingLists by viewModel.shoppingLists.collectAsState(emptyList())
    var shoppingList by remember { mutableStateOf(ShoppingList.getTemplate()) }

    var showEditListDialog by remember { mutableStateOf(false) }
    var showDeleteListDialog by remember { mutableStateOf(false) }
    var showCloseProductDialog by remember { mutableStateOf(false) }
    var showOpenProductDialog by remember { mutableStateOf(false) }

    var crunch by remember { mutableStateOf(false) }

    var pickedProduct by remember { mutableStateOf(Product.getTemplate()) }
    var filter by remember { mutableStateOf(Filter(category = categories.first())) }

    Scaffold(
       floatingActionButton = {
           FloatingActionButton(onClick = { showEditListDialog = true }) {
               Icon(imageVector = Icons.Filled.Add, contentDescription = null)
           }
       },

    ) { paddingValues ->
        Text(text = crunch.toString(), color = Color.Transparent)

        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(top = 8.dp)
        ) {
            if (categories.size > 1){
                stickyHeader {
                    Spacer(modifier = Modifier.height(8.dp))
                    FilterBlock(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        categories = categories,
                        filter = filter
                    ){
                        filter = it
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }


            products.map { map ->
                if(map.value.isEmpty() || map.value.find { it.closedBy == "" } != null){
                    val spByProductsList = shoppingLists.find { it.docId == map.key }
                    spByProductsList?.let {
                        val categoryFilterDrop = filter.category.docId.isNotEmpty() && spByProductsList.categoryId != filter.category.docId
                        val timeFilterDrop = filter.time != TIME.ALL_TIME && spByProductsList.completionDate > getTimeByFilterProperty(time = filter.time)
                        if (categoryFilterDrop || timeFilterDrop){
                            return@let
                        }

                        item {
                            ShoppingListRow(
                                modifier = Modifier
                                    .clickable {
                                        shoppingList = spByProductsList
                                        showEditListDialog = true
                                    },
                                shoppingList = spByProductsList,
                                productsList = map.value,
                                category = if (spByProductsList.categoryId.isEmpty()) null
                                else categories.find { it.docId == spByProductsList.categoryId },
                                onChangeProductStatus = { status, product ->
                                    pickedProduct = product
                                    when (status) {
                                        true -> showCloseProductDialog = true
                                        false -> showOpenProductDialog = true
                                    }
                                }
                            ) {
                                navController.navigate(
                                    Views.Products.path.replace(
                                        "{listId}",
                                        spByProductsList.docId
                                    )
                                )
                            }
                        }
                    }

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

    if(showCloseProductDialog){
        CloseProductDialog(
            productObj = pickedProduct,
            onDismiss = {
                showCloseProductDialog = false
            },
            onConfirm = {
                viewModel.closeProduct(it)
                showCloseProductDialog = false
            }
        )
    }

    if(showOpenProductDialog){
        OpenProductDialog(onConfirm = {
            viewModel.openProduct(pickedProduct)
            showOpenProductDialog = false
        }) {
            showOpenProductDialog = false
        }
    }

    if(viewModel.showLoadingDialog){
        LoadingDialog()
    }

    LaunchedEffect(Unit){
        while (true){
            delay(1500)
            crunch = !crunch
        }

    }
}



@Composable
private fun ShoppingListRow(
    modifier: Modifier = Modifier,
    shoppingList: ShoppingList,
    category: Category?,
    productsList: List<Product>?,
    onChangeProductStatus: (Boolean, Product) -> Unit,
    onOpen: () -> Unit
){
    val density = LocalDensity.current
    var showList by remember { mutableStateOf(false) }
    Column(
        modifier = modifier
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .shadow(2.dp, RoundedCornerShape(5.dp))
            .padding(horizontal = 12.dp, vertical = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = shoppingList.name,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                var rowHeight by remember { mutableStateOf(0) }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .onSizeChanged {
                            rowHeight = it.height
                        }
                ) {
                    Column {
                        Text(
                            text = "${stringResource(id = R.string.buy_up_to)} ${shoppingList.completionDate.asDate()}",
                            fontSize = 12.sp
                        )
                        Text(
                            text = "(${stringResource(R.string.by)} ${shoppingList.createdBy})",
                            fontSize = 12.sp
                        )
                    }
                    category?.let {
                        val bitmap = SpendSwiftApp.iconPack.icons[it.iconId]?.drawable?.drawableToBitmap()
                        bitmap?.let {
                            Spacer(modifier = Modifier
                                .padding(horizontal = 12.dp)
                                .height(with(density) { (rowHeight * .8f).toDp() })
                                .width(1.dp)
                                .background(MaterialTheme.colorScheme.primary))
                            Icon(
                                bitmap = it.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier
                            )
                        }
                    }
                }
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                OutlinedIconButton(onClick = onOpen) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = null)
                }
                if(productsList.isNullOrEmpty()){
                    Text(
                        text = stringResource(id = R.string.empty),
                        color = Color.Red,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                } else {
                    OutlinedIconButton(onClick = { showList = !showList }) {
                        Icon(
                            imageVector = if(showList) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null
                        )
                    }
                }
            }
        }
        productsList?.let {
            if (showList){
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp)
                ) {
                    productsList.forEach { product ->
                        val isClosed = product.closedBy.isNotEmpty()
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .border(
                                    2.dp,
                                    MaterialTheme.colorScheme.primary,
                                    RoundedCornerShape(10.dp)
                                )
                                .padding(vertical = 12.dp, horizontal = 8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = product.name,
                                    fontSize = 18.sp,
                                    color = if (isClosed) Color.Gray else MaterialTheme.colorScheme.primary,
                                    textDecoration = if(isClosed) TextDecoration.LineThrough else TextDecoration.Underline
                                )
                                Checkbox(
                                    checked = isClosed,
                                    onCheckedChange = {
                                        onChangeProductStatus(it, product)
                                    }
                                )
                            }
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "${stringResource(id = R.string.added_by)} ${product.addedBy}",
                                    color = if (isClosed) Color.Gray else MaterialTheme.colorScheme.primary,
                                    fontSize = 12.sp,
                                )
                                if(isClosed){
                                    Text(
                                        text = "${stringResource(id = R.string.closed_by)} ${product.closedBy}",
                                        color = if (isClosed) Color.Gray else MaterialTheme.colorScheme.primary,
                                        fontSize = 12.sp,
                                    )
                                }
                            }

                            if(isClosed){
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.price),
                                        color = if (isClosed) Color.Gray else MaterialTheme.colorScheme.primary,
                                        fontSize = 12.sp,
                                    )
                                    Text(
                                        text = "${product.price} $",
                                        color = if (isClosed) Color.Gray else MaterialTheme.colorScheme.primary,
                                        fontSize = 12.sp,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CloseProductDialog(
    productObj: Product,
    onDismiss: () -> Unit,
    onConfirm: (Product) -> Unit
){
    var product by remember { mutableStateOf(productObj) }
    AlertDialog(
        modifier = Modifier.fillMaxWidth(),
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(id = R.string.save),
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
                    title = R.string.price,
                    initialValue = product.price.toString(),
                    keyboardType = KeyboardType.Decimal,
                    onChange = { if(it.isDigitsOnly() && it.trim().isNotEmpty()) product = product.copy(price = it.toInt())}
                )
            }
        },
        confirmButton = {
            IconButton(onClick = {
                onConfirm(product)
            }) {
                Icon(imageVector = Icons.Filled.Check, contentDescription = null)
            }
        },
        dismissButton = {
            IconButton(onClick = onDismiss) {
                Icon(imageVector = Icons.Filled.Close, contentDescription = null)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAlertDialog(
    titleRes: Int = R.string.edit,
    shoppingListObj: ShoppingList,
    categories: List<Category>,
    onSuccess: (ShoppingList) -> Unit,
    onDelete: () -> Unit,
    onEnd: () -> Unit
){
    var shoppingList by remember { mutableStateOf(shoppingListObj) }
    var selectedCategory by remember { mutableStateOf(
        if(shoppingListObj.categoryId == "")
            categories[0]
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
                text = stringResource(id = titleRes),
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

@Composable
fun OpenProductDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
){
    SpendSwiftTheme {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = stringResource(id = R.string.attention),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            text = {
                Text(
                    text = stringResource(id = R.string.open_product),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                IconButton(onClick = onConfirm) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color.Red
                    )
                }
            },
            dismissButton = {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                    )
                }
            }
        )
    }
}