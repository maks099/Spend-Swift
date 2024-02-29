package com.spend.swift.ui.views.main.lists

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.spend.swift.R
import com.spend.swift.SpendSwiftApp
import com.spend.swift.helpers.toast
import com.spend.swift.model.Product
import com.spend.swift.ui.views.custom.LoadingDialog
import com.spend.swift.ui.views.custom.RemoveDialog
import com.spend.swift.ui.views.custom.TextBox

@OptIn(ExperimentalLayoutApi::class)
@Preview
@Composable
fun Products(
    listId: String = "",
    navController: NavController = rememberNavController()
){
    val viewModel: ProductsViewModel = viewModel()
    viewModel.getShoppingList(listId)

    val shoppingList by viewModel.shoppingList.collectAsState()
    val basicProducts by viewModel.basicProducts.collectAsState()
    val products by viewModel.products.collectAsState()

    var pickedProduct by remember { mutableStateOf(Product.getTemplate()) }
    var helpListShow by remember { mutableStateOf(false) }
    var showRemoveDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            ProductTopAppBar(text = shoppingList.name) {
                navController.popBackStack()
            }
        }
    ) { paddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(top = 16.dp)
                .padding(horizontal = 16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp))
                    .padding(horizontal = 18.dp)
                    .padding(top = 18.dp, bottom = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextBox(
                        modifier = Modifier.weight(.6f),
                        title = R.string.product_name,
                        initialValue = pickedProduct.name,
                        keyboardType = KeyboardType.Text,
                        onChange = { pickedProduct = pickedProduct.copy(name = it) }
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    OutlinedIconButton(
                        modifier = Modifier.weight(.2f),
                        onClick = {
                        if(pickedProduct.name.trim().length < 4){
                            SpendSwiftApp.getCtx().toast(R.string.min_lenght)
                        } else {
                            viewModel.saveProduct(pickedProduct.name)
                            pickedProduct = pickedProduct.copy(name = "")
                        }
                    }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    }
                }
                IconButton(
                    onClick = { helpListShow = !helpListShow },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Icon(
                        imageVector = if(helpListShow) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null
                    )
                }

                if(helpListShow){
                    FlowRow(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.background)
                            .border(
                                2.dp,
                                MaterialTheme.colorScheme.primary,
                                RoundedCornerShape(20.dp)
                            )
                            .padding(8.dp)
                    ) {
                        basicProducts.forEach { basicProduct ->
                            TextButton(
                                onClick = { viewModel.saveProduct(basicProduct.name) },
                                modifier = Modifier
                                    .padding(4.dp)
                                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                            ) {
                                Text(text = basicProduct.name)
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            if(products.isNotEmpty()){
                LazyColumn(
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.background)
                        .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp))
                        .padding(16.dp)
                ){
                    items(products){ product ->
                        ProductRow(
                            modifier = Modifier.fillMaxWidth(),
                            product = product
                        ){
                            pickedProduct = product
                            showRemoveDialog = true
                        }
                    }
                }
            }
        }
    }

    if(showRemoveDialog){
        RemoveDialog(
            onConfirm = {
                viewModel.removeProduct(pickedProduct)
                showRemoveDialog = false
                pickedProduct = Product.getTemplate()
            }
        ){
            showRemoveDialog = false
        }
    }

    if(viewModel.showLoadingDialog){
        LoadingDialog()
    }
}

@Composable
private fun ProductRow(
    modifier: Modifier = Modifier,
    product: Product,
    onDelete: () -> Unit
){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .shadow(2.dp, RoundedCornerShape(5.dp))
            .padding(horizontal = 12.dp, vertical = 16.dp)
    ) {
        val showDecoration = product.closedBy.isNotEmpty()
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = product.name,
                textDecoration = if(showDecoration) TextDecoration.LineThrough else TextDecoration.None,
                color = if (showDecoration) Color.Gray else MaterialTheme.colorScheme.primary,
                fontSize = 16.sp
            )
            Text(
                text = "${stringResource(id = R.string.added_by)} ${product.addedBy}",
                textDecoration = if(showDecoration) TextDecoration.LineThrough else TextDecoration.None,
                color = if (showDecoration) Color.Gray else MaterialTheme.colorScheme.primary,
                fontSize = 12.sp
            )
        }
        if(product.closedBy.isEmpty()){
            OutlinedIconButton(
                onClick = onDelete,
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = Color.Red
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductTopAppBar(
    text: String,
    onBack: () -> Unit
){
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = text,
                style = androidx.compose.material3.MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline
                )
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer
        ),
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            }
        }
    )
}