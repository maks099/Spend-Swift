package com.spend.swift.ui.views.custom

import androidx.annotation.StringRes
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.spend.swift.R
import com.spend.swift.SpendSwiftApp
import com.spend.swift.helpers.toast


@Composable
fun TextBox(
    modifier: Modifier = Modifier,
    @StringRes title: Int,
    initialValue: String,
    keyboardType: KeyboardType,
    onChange: (String) -> Unit
){
    OutlinedTextField(
        modifier = modifier,
        value = initialValue,
        singleLine = true,
        label = {
            Text(text = stringResource(id = title))
        },
        visualTransformation = if(keyboardType == KeyboardType.Password) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType),
        onValueChange = { onChange(it) },
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
            focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    )
}

@Composable
fun MainButton(
    modifier: Modifier = Modifier,
    @StringRes res: Int,
    shape: RoundedCornerShape = RoundedCornerShape(10.dp),
    onClick: () -> Unit
){
    Button(
        onClick = onClick,
        shape = shape,
        modifier = modifier
            .border(2.dp, Color.Black, shape = shape)
    ) {
        Text(
            text = stringResource(id = res),
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Color.White
            )
        )
    }
}

@Composable
fun NickAlertDialog(
    onSuccess: (String) -> Unit = {}
){
    var nickName by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = {  },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.nick_explanation)
                )
                TextBox(
                    title = R.string.nick,
                    initialValue = nickName,
                    keyboardType = KeyboardType.Text,
                    onChange = {
                        nickName = it
                    }
                )
            }
        },
        confirmButton = {
            IconButton(onClick = {
                if(nickName.trim().length >= 4){
                    onSuccess(nickName)
                } else SpendSwiftApp.getCtx().toast(R.string.min_nick_error)
            }) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null
                )
            }
        }
    )
}

@Preview
@Composable
fun LoadingDialog(){
    AlertDialog(
        modifier = Modifier
            .padding(vertical = 32.dp),
        text = {
               Column(
                   verticalArrangement = Arrangement.Center,
                   horizontalAlignment = Alignment.CenterHorizontally,
                   modifier = Modifier.fillMaxWidth()
               ) {
                    CircularProgressIndicator(
                        color = Color.Black,
                        modifier = Modifier

                    )
               }
        },
        onDismissRequest = { },
        confirmButton = { /*TODO*/ }
    )
}