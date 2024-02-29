package com.spend.swift.ui.views.main.lists

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.spend.swift.R
import com.spend.swift.helpers.getTimeByFilterProperty
import com.spend.swift.model.Category
import com.spend.swift.ui.views.custom.CategoryDropdownMenu
import java.time.Year

@Composable
fun FilterBlock(
    modifier: Modifier = Modifier,
    categories: List<Category>,
    filter: Filter,
    onChange: (Filter) -> Unit
){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
    ) {
        CategoryDropdownMenu(
            modifier = Modifier,
            widthCoef = .5f,
            selected = filter.category,
            items = categories,
            onClick = {
                onChange(filter.copy(category = it))
            }
        )
        Spacer(modifier = Modifier.width(24.dp))
        DateDropdownMenu(
            filter = filter,
            onChange = {
                onChange(filter.copy(time = it))
            }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun DateDropdownMenu(
    filter: Filter,
    onChange: (TIME) -> Unit,
    modifier: Modifier = Modifier
){
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
            val textRes =
            TextField(
                value = stringResource(id = TimeToStr(filter.time)),
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    focusedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                ),
                modifier = Modifier
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                TIME.entries.forEach { t ->
                    DropdownMenuItem(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = {
                            Text(text = stringResource(id = TimeToStr(t)))
                        },
                        onClick = {
                            onChange(t)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

enum class TIME{
    ALL_TIME,
    YEAR,
    CVARTAL,
    MONTH
}

private fun TimeToStr(t: TIME) = when(t){
    TIME.YEAR -> R.string.year
    TIME.CVARTAL -> R.string.cvartal
    TIME.MONTH -> R.string.month
    TIME.ALL_TIME -> R.string.all_time
}

private fun timesValues(coef: Int) = mapOf<TIME, Long>(
    TIME.ALL_TIME to getTimeByFilterProperty(coefficient = coef, time = TIME.ALL_TIME),
    TIME.YEAR to getTimeByFilterProperty(coefficient = coef, time = TIME.YEAR),
    TIME.CVARTAL to getTimeByFilterProperty(coefficient = coef,time = TIME.CVARTAL),
    TIME.MONTH to getTimeByFilterProperty(coefficient = coef,time = TIME.MONTH),
)

