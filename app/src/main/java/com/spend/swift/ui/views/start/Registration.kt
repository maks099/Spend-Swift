package com.spend.swift.ui.views.start

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.spend.swift.R
import com.spend.swift.helpers.SharedKeys
import com.spend.swift.helpers.SharedPrefsHelper
import com.spend.swift.ui.theme.SpendSwiftTheme
import com.spend.swift.ui.view_models.SignViewModel
import com.spend.swift.ui.views.custom.LoadingDialog
import com.spend.swift.ui.views.custom.MainButton
import com.spend.swift.ui.views.custom.NickAlertDialog
import com.spend.swift.ui.views.custom.TextBox
import com.spend.swift.ui.views.Views

@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun Registration(
    navController: NavController = rememberNavController()
){
    val context = LocalContext.current
    val viewModel: SignViewModel = viewModel()

    var isEnterNickDialogShow by remember { mutableStateOf(false) }

    SpendSwiftTheme {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Image(
                painter = painterResource(id = R.drawable.wave_top),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth()
            )
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiaryContainer,
                modifier = Modifier
                    .fillMaxWidth(.5f)
                    .aspectRatio(1f)
            )


            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.signup),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                TextBox(
                    title = R.string.email,
                    initialValue = viewModel.profile.email,
                    keyboardType = KeyboardType.Email
                ) { viewModel.profile = viewModel.profile.copy(email = it) }
                TextBox(
                    title = R.string.password,
                    initialValue = viewModel.profile.password,
                    keyboardType = KeyboardType.Password
                ) { viewModel.profile = viewModel.profile.copy(password = it) }
            }
            MainButton(
                modifier = Modifier.fillMaxWidth(.5f),
                res = R.string.signup
            ) {
                viewModel.signIn {
                    isEnterNickDialogShow = true
                }
            }

            TextButton(
                onClick = { navController.navigate(Views.Login.path){
                    popUpTo(Views.Registration.path){ inclusive = true }
                } },
                modifier = Modifier
                    .padding(top = 24.dp)
            ) {
                Text(text = stringResource(id = R.string.have_account))
            }

            Image(
                painter = painterResource(id = R.drawable.wave_bottom),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if(isEnterNickDialogShow){
            NickAlertDialog {
                isEnterNickDialogShow = false
                SharedPrefsHelper.saveStr(SharedKeys.Nickname, it)
                navController.navigate(Views.Main.path){
                    popUpTo(Views.Registration.path){
                        inclusive = true
                    }
                }
            }
        }

        if (viewModel.isLoadingDialogShow){
            LoadingDialog()
        }
    }
}
