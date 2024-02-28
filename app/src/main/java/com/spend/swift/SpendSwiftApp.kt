package com.spend.swift

import android.app.Application
import android.content.Context
import com.maltaisn.icondialog.pack.IconPack
import com.maltaisn.icondialog.pack.IconPackLoader
import com.maltaisn.iconpack.defaultpack.createDefaultIconPack
import com.spend.swift.db.firebaseAuth
import com.spend.swift.helpers.SharedPrefsHelper

const val DEFAULT_ICON_ID = 955

class SpendSwiftApp : Application() {

    companion object {
        private lateinit var appContext: Context
        fun getCtx() = appContext

        lateinit var iconPack: IconPack
    }

    override fun onCreate() {
        super.onCreate()

        appContext = applicationContext

        SharedPrefsHelper.init(applicationContext)
        firebaseAuth()
        loadIconPack()
    }

    private fun loadIconPack() {
        val loader = IconPackLoader(this)
        iconPack = createDefaultIconPack(loader)
        iconPack.loadDrawables(loader.drawableLoader)
    }
}