package com.spend.swift.ui.views.main.other

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.maltaisn.icondialog.IconDialog
import com.maltaisn.icondialog.IconDialogSettings
import com.spend.swift.MainActivity
import com.spend.swift.R
import com.spend.swift.SpendSwiftApp
import com.spend.swift.helpers.SharedKeys
import com.spend.swift.helpers.SharedPrefsHelper
import com.spend.swift.model.Category
import com.spend.swift.ui.views.custom.SpecialTopAppBar
import com.spend.swift.ui.views.custom.TextBox


private const val ICON_TAG_DIALOG = "icon-dialog"

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
fun Categories(
    navController: NavController = rememberNavController()
){
    var isAddDialogShow by remember { mutableStateOf(false) }

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
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues = paddingValues)
        ){
            stickyHeader {


            }
        }
        if(isAddDialogShow){
            AddCategoryDialog()
        }
    }

}

@Composable
private fun AddCategoryDialog(){
    var category by remember {
        mutableStateOf(
            Category("", 955, SharedPrefsHelper.readLong(SharedKeys.ProfileId).toInt())
        )
    }

    val activity = LocalContext.current as MainActivity
    val iconDialog = activity.supportFragmentManager.findFragmentByTag(ICON_TAG_DIALOG) as IconDialog?
        ?: IconDialog.newInstance(IconDialogSettings())

    var drawable by remember {
        mutableStateOf(SpendSwiftApp.iconPack.icons[category.iconId]!!.drawable)
    }

    AlertDialog(
        onDismissRequest = {
            // TODO: actions
        },
        title = {
            Text(
                text = stringResource(id = R.string.edit),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        },
        confirmButton = {

        },
        dismissButton = {

        },
        text = {
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
        }
    )
    LaunchedEffect(Unit){
        MainActivity.iconFlow.collect {
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

