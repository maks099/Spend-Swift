package com.spend.swift

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.maltaisn.icondialog.IconDialog
import com.maltaisn.icondialog.data.Icon
import com.maltaisn.icondialog.pack.IconPack
import com.spend.swift.helpers.SharedKeys
import com.spend.swift.helpers.SharedPrefsHelper
import com.spend.swift.ui.theme.SpendSwiftTheme
import com.spend.swift.ui.views.start.Login
import com.spend.swift.ui.views.main.Main
import com.spend.swift.ui.views.start.Registration
import com.spend.swift.ui.views.Views
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), IconDialog.Callback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val startDestination =
            if(SharedPrefsHelper.readStr(SharedKeys.Nickname) == "") Views.Login.path
            else Views.Main.path

        setContent {
            val navController = rememberNavController()
            SpendSwiftTheme {
                NavHost(navController = navController, startDestination = startDestination){
                    composable(Views.Login.path) { Login(navController) }
                    composable(Views.Registration.path) { Registration(navController) }
                    composable(Views.Main.path) { Main() }
                }
            }
        }
    }

    override val iconDialogIconPack: IconPack?
        get() = SpendSwiftApp.iconPack

    override fun onIconDialogIconsSelected(dialog: IconDialog, icons: List<Icon>) {
        lifecycleScope.launch {
            icons.map { iconFlow.emit(it) }
        }
        Toast.makeText(this, "${icons.map { it.id }}", Toast.LENGTH_SHORT).show()
    }

    companion object {
        val iconFlow = MutableSharedFlow<Icon>()
    }

}
