package com.spend.swift.ui.views.custom

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.TextField
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.spend.swift.SpendSwiftApp
import com.spend.swift.model.Category
import com.spend.swift.ui.views.main.other.drawableToBitmap
import java.util.Locale

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CategoryDropdownMenu(
    modifier: Modifier = Modifier,
    selected: Category,
    items: List<Category>,
    onClick: (Category) -> Unit
) {
    if (items.isNotEmpty()){
        var expanded by remember { mutableStateOf(false) }

        Box(
            modifier = modifier
                .fillMaxWidth()
        ) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                },
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                androidx.compose.material3.TextField(
                    value = selected.name,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    items.forEach { item ->
                        DropdownMenuItem(
                            modifier = Modifier
                                .fillMaxWidth(),
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    val bitmap = SpendSwiftApp.iconPack.icons[item.iconId]?.drawable?.drawableToBitmap()
                                    bitmap?.let {
                                        Icon(
                                            bitmap = it.asImageBitmap(),
                                            contentDescription = null,
                                            modifier = Modifier
                                        )
                                        Spacer(modifier = Modifier.width(16.dp))
                                    }
                                    Text(
                                        text = item.name,
                                    )
                                }
                            },
                            onClick = {
                                onClick(item)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}