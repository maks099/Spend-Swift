package com.spend.swift.ui.views.main.other

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.spend.swift.DEFAULT_ICON_ID
import com.spend.swift.MainActivity
import com.spend.swift.R
import com.spend.swift.SpendSwiftApp
import com.spend.swift.helpers.SharedKeys
import com.spend.swift.helpers.SharedPrefsHelper
import com.spend.swift.helpers.toast
import com.spend.swift.model.BasicProduct
import com.spend.swift.model.Category
import com.spend.swift.ui.views.custom.CategoryDropdownMenu
import com.spend.swift.ui.views.custom.LoadingDialog
import com.spend.swift.ui.views.custom.RemoveDialog
import com.spend.swift.ui.views.custom.SpecialTopAppBar
import com.spend.swift.ui.views.custom.TextBox

@Composable
fun BasicProducts(
    navController: NavController = rememberNavController()
){
    val context = LocalContext.current
    val viewModel: BasicProductsViewModel = viewModel()

    val categories by viewModel.categories.collectAsState(listOf(Category(stringResource(id = R.string.all), DEFAULT_ICON_ID, "")))
    var selectedCategory by remember { mutableStateOf(categories[0]) }

    val basicProducts by viewModel.basicProducts.collectAsState(emptyList())

    var showEditBasicProductDialog by remember { mutableStateOf(false) }
    var isRemoveDialogShow by remember { mutableStateOf(false) }

    val basicProductPattern = BasicProduct("", "",  SharedPrefsHelper.readStr(SharedKeys.ProfileId) ?: "", "")
    var pickedBasicProduct by remember {
        mutableStateOf(basicProductPattern)
    }

    Scaffold(
        topBar = {
            SpecialTopAppBar(
                titleIcon = R.drawable.baseline_list_alt_24,
                titleText = R.string.basic_goods
            ){
                navController.popBackStack()
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showEditBasicProductDialog = true }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(vertical = 8.dp)
            ) {
                CategoryDropdownMenu(
                    selected = selectedCategory,
                    items = categories,
                    modifier = Modifier.weight(1f)
                ){
                    selectedCategory = it
                }
                val bitmap = SpendSwiftApp.iconPack.icons[selectedCategory.iconId]?.drawable?.drawableToBitmap()
                bitmap?.let {
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(
                        bitmap = it.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier
                    )
                }
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ){
                if(selectedCategory.name.uppercase() == context.getString(R.string.all).uppercase()){
                    items(basicProducts){ basicProduct ->
                        BasicProductRowItem(
                            category = categories.find { it.docId == basicProduct.categoryId },
                            basicProduct = basicProduct,
                            onClick = {
                                pickedBasicProduct = basicProduct
                                showEditBasicProductDialog = true
                            }
                        )
                    }
                } else {
                    val filteredProducts = basicProducts.filter { it.categoryId == selectedCategory.docId }
                    items(filteredProducts){ basicProduct ->
                        BasicProductRowItem(
                            category = null,
                            basicProduct = basicProduct,
                            onClick = {
                                pickedBasicProduct = basicProduct
                                showEditBasicProductDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    if(showEditBasicProductDialog){
        EditBasicProductDialog(
            basicProductObj = pickedBasicProduct,
            onSuccess = {
                showEditBasicProductDialog = false
                viewModel.save(it)
            },
            selectedCategory = if (selectedCategory.name.lowercase() == context.getString(R.string.all).lowercase()) null else selectedCategory,
            onDelete = { isRemoveDialogShow = true }) {
            showEditBasicProductDialog = false
        }
    }


    if (isRemoveDialogShow){
        RemoveDialog(
            onConfirm = {
                showEditBasicProductDialog = false
                isRemoveDialogShow = false
                viewModel.delete(pickedBasicProduct)
                pickedBasicProduct = basicProductPattern
            }
        ){
            isRemoveDialogShow = false
        }
    }

    if(viewModel.showLoadingDialog){
        LoadingDialog()
    }
}

@Composable
private fun EditBasicProductDialog(
    basicProductObj: BasicProduct,
    onSuccess: (BasicProduct) -> Unit,
    selectedCategory: Category?,
    onDelete: () -> Unit,
    onEnd: () -> Unit
){
    val activity = LocalContext.current as MainActivity

    var basicProduct by remember { mutableStateOf(
        basicProductObj.copy(
            categoryId = selectedCategory?.docId ?: ""
        ),
    ) }

    AlertDialog(
        onDismissRequest = { onEnd() },
        title = {
            Text(
                text = stringResource(id = R.string.edit),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        },
        confirmButton = {
            IconButton(onClick = {
                if (basicProduct.name.trim().length < 4){
                    activity.toast(R.string.min_lenght)
                } else {
                    onSuccess(basicProduct)
                }
            }) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
        },
        dismissButton = {
            IconButton(onClick = onEnd) {
                Icon(imageVector = Icons.Default.Close, contentDescription = null,  tint = MaterialTheme.colorScheme.primary)
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextBox(
                        title = R.string.basic_product_name,
                        initialValue = basicProduct.name,
                        keyboardType = KeyboardType.Text,
                        onChange = {
                            basicProduct = basicProduct.copy(name = it)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
                if(basicProduct.docId.isNotEmpty()){
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
        }
    )
}

@Composable
private fun BasicProductRowItem(
    modifier: Modifier = Modifier,
    category: Category?,
    basicProduct: BasicProduct,
    onClick: (BasicProduct) -> Unit
){
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .padding(horizontal = 4.dp, vertical = 8.dp)
            .fillMaxWidth()
            .shadow(2.dp)
            .clickable { onClick(basicProduct) }
            .padding(horizontal = 16.dp, vertical = 24.dp)
          ,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = basicProduct.name,
            textAlign = TextAlign.Center
        )
        category?.let { c ->
            val bitmap = SpendSwiftApp.iconPack.icons[c.iconId]?.drawable?.drawableToBitmap()
            bitmap?.let {
                Spacer(modifier = Modifier.width(16.dp))
                Icon(
                    bitmap = it.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                )
            }
        }
    }
}