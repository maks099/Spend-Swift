package com.spend.swift.ui.views.main.other

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jakewharton.processphoenix.ProcessPhoenix
import com.spend.swift.R
import com.spend.swift.SpendSwiftApp
import com.spend.swift.helpers.SharedKeys
import com.spend.swift.helpers.SharedPrefsHelper
import com.spend.swift.helpers.toast
import com.spend.swift.ui.theme.SpendSwiftTheme
import com.spend.swift.ui.views.custom.MainButton
import com.spend.swift.ui.views.custom.SpecialTopAppBar
import com.spend.swift.ui.views.custom.TextBox

@Preview
@Composable
fun AccountSettings(
    navController: NavController = rememberNavController()
){
    SpendSwiftTheme {
        val context = LocalContext.current
        Scaffold(
            topBar = {
                SpecialTopAppBar(
                    titleIcon = R.drawable.baseline_account_circle_24,
                    titleText = R.string.account,
                ){
                    navController.popBackStack()
                }
            }
        ) { paddingValues ->
            var nickname by remember { mutableStateOf(SharedPrefsHelper.readStr(SharedKeys.Nickname)?:"") }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround,
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextBox(
                            title = R.string.nick,
                            initialValue = nickname,
                            keyboardType = KeyboardType.Text,
                            onChange = {
                                nickname = it
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedIconButton(
                            modifier = Modifier.aspectRatio(1f),
                            onClick = {
                            if(nickname.trim().length >= 4){
                                SharedPrefsHelper.saveStr(SharedKeys.Nickname, nickname)
                                SpendSwiftApp.getCtx().toast(R.string.nick_is_updated)
                            } else {
                                SpendSwiftApp.getCtx().toast(R.string.min_nick_error)
                            }
                        }) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(id = R.string.nick_update_mark),
                        textAlign = TextAlign.Center
                    )
                }

                MainButton(res = R.string.logout) {
                    SharedPrefsHelper.saveStr(SharedKeys.Nickname, "")
                    SharedPrefsHelper.saveStr(SharedKeys.ProfileId, "")
                    ProcessPhoenix.triggerRebirth(context)
                }
            }
        }
    }
}