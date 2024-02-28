package com.spend.swift.ui.views.main.other

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.maltaisn.icondialog.IconDialog
import com.maltaisn.icondialog.IconDialogSettings
import com.spend.swift.DEFAULT_ICON_ID
import com.spend.swift.MainActivity
import com.spend.swift.R
import com.spend.swift.SpendSwiftApp
import com.spend.swift.helpers.SharedKeys
import com.spend.swift.helpers.SharedPrefsHelper
import com.spend.swift.helpers.toast
import com.spend.swift.model.Category
import com.spend.swift.ui.views.custom.LoadingDialog
import com.spend.swift.ui.views.custom.RemoveDialog
import com.spend.swift.ui.views.custom.SpecialTopAppBar
import com.spend.swift.ui.views.custom.TextBox
import java.util.Locale


private const val ICON_TAG_DIALOG = "icon-dialog"

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
fun Categories(
    navController: NavController = rememberNavController()
){
    var isAddDialogShow by remember { mutableStateOf(false) }
    var isRemoveDialogShow by remember { mutableStateOf(false) }
    val categoryPattern = Category("", DEFAULT_ICON_ID, SharedPrefsHelper.readStr(SharedKeys.ProfileId)?:"0")
    var category by remember { mutableStateOf(categoryPattern) }
    val viewModel: CategoriesViewModel = viewModel()

    Scaffold(
        topBar = {
            SpecialTopAppBar(
                titleIcon = R.drawable.baseline_category_24,
                titleText = R.string.categories,
            ){
                navController.popBackStack()
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                onClick = {
                    category = categoryPattern
                    isAddDialogShow = true
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    ) { paddingValues ->
        val categories by viewModel.categories.collectAsState(initial = emptyList())
        LazyVerticalGrid(
            modifier = Modifier
                .padding(paddingValues = paddingValues)
                .fillMaxSize(),
            columns = GridCells.Fixed(2)
        ){
            items(categories){c ->
                val shape = RoundedCornerShape(20.dp)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier

                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(shape)
                        .shadow(2.dp)
                        .clickable {
                            category = c
                            isAddDialogShow = true
                        }
                        .padding(horizontal = 16.dp, vertical = 24.dp)

                ) {
                    val bitmap = SpendSwiftApp.iconPack.icons[c.iconId]?.drawable?.drawableToBitmap()
                    bitmap?.let {
                        Icon(
                            bitmap = it.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth(.3f)
                                .aspectRatio(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = c.name.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(
                                Locale.ROOT
                            ) else it.toString()
                        },
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 18.sp
                        ),

                    )
                }
            }
        }
        if(isAddDialogShow){
            AddCategoryDialog(
                categoryObj = category,
                onSuccess = { newCategory ->
                    viewModel.save(newCategory)
                    isAddDialogShow = false
                },
                onDelete = {
                    isRemoveDialogShow = true
                }
            ){
                isAddDialogShow = false
            }
        }

        if (isRemoveDialogShow){
            RemoveDialog(
                onConfirm = {
                    isAddDialogShow = false
                    isRemoveDialogShow = false
                    viewModel.delete(category)
                    category = categoryPattern
                }
            ){
                isRemoveDialogShow = false
            }
        }

        if(viewModel.showLoadingDialog){
            LoadingDialog()
        }
    }
}

@Composable
private fun AddCategoryDialog(
    categoryObj: Category,
    onSuccess: (Category) -> Unit,
    onDelete: () -> Unit,
    onEnd: () -> Unit
){
    val activity = LocalContext.current as MainActivity
    val iconDialog = activity.supportFragmentManager.findFragmentByTag(ICON_TAG_DIALOG) as IconDialog?
        ?: IconDialog.newInstance(IconDialogSettings())

    var category by remember { mutableStateOf(categoryObj) }
    var drawable by remember {
        mutableStateOf(SpendSwiftApp.iconPack.icons[category.iconId]!!.drawable)
    }

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
                if (category.name.trim().length < 4){
                    activity.toast(R.string.min_category_name)
                }
                else if(category.name.lowercase() == SpendSwiftApp.getCtx().getString(R.string.all).lowercase()){
                    activity.toast(R.string.invalid_name)
                }
                else {
                    onSuccess(category)
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
                        title = R.string.category_name,
                        initialValue = category.name,
                        keyboardType = KeyboardType.Text,
                        onChange = {
                            category = category.copy(name = it)
                        },
                        modifier = Modifier.weight(.6f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    drawable?.drawableToBitmap()?.let { bitmap ->
                        IconButton(onClick = {
                            iconDialog.show(activity.supportFragmentManager, ICON_TAG_DIALOG)
                        }) {
                            Icon(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier
                                    .weight(.3f)
                                    .aspectRatio(1f)
                            )
                        }
                    }
                }
                if(category.docId.isNotEmpty()){
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
    LaunchedEffect(Unit){
        MainActivity.iconFlow.collect {
            category = category.copy(iconId = it.id)
            drawable = it.drawable
        }
    }
}


fun Drawable.drawableToBitmap(): Bitmap? {
    var bitmap: Bitmap? = null
    if (this is BitmapDrawable) {
        val bitmapDrawable = this
        if (bitmapDrawable.bitmap != null) {
            return bitmapDrawable.bitmap
        }
    }
    bitmap = if (this.intrinsicWidth <= 0 || this.intrinsicHeight <= 0) {
        Bitmap.createBitmap(
            1,
            1,
            Bitmap.Config.ARGB_8888
        ) // Single color bitmap will be created of 1x1 pixel
    } else {
        Bitmap.createBitmap(
            this.intrinsicWidth,
            this.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
    }
    val canvas = Canvas(bitmap)
    this.setBounds(0, 0, canvas.getWidth(), canvas.getHeight())
    this.draw(canvas)
    return bitmap
}

