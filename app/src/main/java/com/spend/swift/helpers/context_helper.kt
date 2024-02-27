package com.spend.swift.helpers

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

fun Context.toast(@StringRes res: Int){
    Toast.makeText(this, res, Toast.LENGTH_SHORT).show()
}