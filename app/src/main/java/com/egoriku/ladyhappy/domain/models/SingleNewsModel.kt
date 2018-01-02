package com.egoriku.ladyhappy.domain.models

import com.egoriku.corelib_kt.Constants
import java.util.Date

data class SingleNewsModel(
        val date: Date = Date(),
        val description: String = Constants.EMPTY,
        val images: List<String> = listOf()
)

data class NewsModel(val news: List<SingleNewsModel> = listOf())