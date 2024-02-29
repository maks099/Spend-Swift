package com.spend.swift.ui.views.main.lists

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.spend.swift.DEFAULT_ICON_ID
import com.spend.swift.R
import com.spend.swift.SpendSwiftApp
import com.spend.swift.helpers.asDate
import com.spend.swift.helpers.getTimeByFilterProperty
import com.spend.swift.model.Category
import com.spend.swift.model.Product
import com.spend.swift.model.ShoppingList
import com.spend.swift.ui.views.custom.LoadingDialog
import com.spend.swift.ui.views.custom.RemoveDialog
import com.spend.swift.ui.views.main.other.drawableToBitmap
import kotlinx.coroutines.delay

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ClosedBuys(
    navController: NavController = rememberNavController()
){
    val viewModel: BuysViewModel = viewModel()

    var crunch by remember { mutableStateOf(false) }

    val products by viewModel.productsLists.collectAsState(emptyMap())
    val shoppingLists by viewModel.shoppingLists.collectAsState(emptyList())
    val categories by viewModel.categories.collectAsState(listOf(Category(SpendSwiftApp.getCtx().getString(R.string.all), DEFAULT_ICON_ID, "")))

    var showEditDialog by remember { mutableStateOf(false) }
    var showRemoveDialog by remember { mutableStateOf(false) }
    var pickedShoppingList by remember { mutableStateOf(ShoppingList.getTemplate()) }
    var filter by remember { mutableStateOf(Filter(category = categories.first())) }

    LazyColumn(
        modifier = Modifier
            .padding(top = 8.dp)
    ){
        if (categories.size > 1) {
            stickyHeader {
                Spacer(modifier = Modifier.height(8.dp))
                FilterBlock(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    categories = categories,
                    filter = filter
                ) {
                    filter = it
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        products.forEach { (listId, productList) ->
            if(productList.find { it.closedBy == "" } == null && productList.isNotEmpty()){
                item {
                    shoppingLists.find { it.docId == listId }?.let { shoppingList ->
                        val categoryFilterDrop = filter.category.docId.isNotEmpty() && shoppingList.categoryId != filter.category.docId
                        val timeFilterDrop = filter.time != TIME.ALL_TIME && shoppingList.completionDate < getTimeByFilterProperty(coefficient = -1, time = filter.time)
                        if (categoryFilterDrop || timeFilterDrop){
                            return@let
                        }
                        ClosedShoppingList(
                            shoppingList = shoppingList,
                            category = categories.find { it.docId == shoppingList.categoryId },
                            products = productList
                        ){
                            pickedShoppingList = shoppingList
                            showEditDialog = true
                        }
                    }
                }
            }
        }
    }

    if (viewModel.showLoadingDialog){
        LoadingDialog()
    }

    if(showRemoveDialog){
        RemoveDialog(
            onConfirm = {
                showRemoveDialog = false
                showEditDialog = false
                viewModel.delete(pickedShoppingList)
            }
        ){
            showRemoveDialog = false
        }
    }

    if(showEditDialog){
        EditAlertDialog(
            titleRes = R.string.duplicate_title,
            shoppingListObj = pickedShoppingList,
            categories = categories,
            onSuccess = {
                viewModel.reSaveList(it, products[it.docId])
                showEditDialog = false
            },
            onDelete = {
                showRemoveDialog = true
            }) {
                showEditDialog = false
            }
    }

    LaunchedEffect(Unit){
        while (true){
            delay(1500)
            crunch = !crunch
        }
    }
}

@Composable
private fun ClosedShoppingList(
    modifier: Modifier = Modifier,
    category: Category?,
    shoppingList: ShoppingList,
    products: List<Product>,
    onCopy: () -> Unit
){
    val density = LocalDensity.current
    var showList by remember { mutableStateOf(false) }
    Column(
        modifier = modifier
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .shadow(2.dp, RoundedCornerShape(5.dp))
            .clickable { onCopy() }
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
                OutlinedIconButton(onClick = onCopy) {
                    Icon(Icons.Default.Edit, contentDescription = null)
                }
                OutlinedIconButton(onClick = { showList = !showList }) {
                    Icon(
                        imageVector = if(showList) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null
                    )
                }
            }
        }

        if (showList){
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp)
            ) {
                products.forEach { product ->
                    val isClosed = product.closedBy.isNotEmpty()
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
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
                        Text(
                            text = product.name,
                            fontSize = 18.sp,
                            textDecoration = TextDecoration.Underline
                        )
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "${stringResource(id = R.string.added_by)} ${product.addedBy}",
                                fontSize = 12.sp,
                            )
                            Text(
                                text = "${stringResource(id = R.string.closed_by)} ${product.closedBy}",
                                fontSize = 12.sp,
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(id = R.string.price),
                                fontSize = 12.sp,
                            )
                            Text(
                                text = "${product.price} $",
                                fontSize = 12.sp,
                            )
                        }
                    }
                }
            }
        }
    }
}