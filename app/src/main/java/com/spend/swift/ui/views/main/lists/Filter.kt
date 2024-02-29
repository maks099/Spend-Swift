package com.spend.swift.ui.views.main.lists

import com.spend.swift.model.Category

data class Filter(
    val time: TIME = TIME.ALL_TIME,
    val category: Category
)
