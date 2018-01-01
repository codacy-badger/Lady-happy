package com.egoriku.ladyhappy.presentation.fragment.allgoods

import com.egoriku.ladyhappy.domain.models.SingleCategoryModel
import com.egoriku.ladyhappy.domain.models.NewsModel

data class AllGoodsScreenModel(
        var categories: List<SingleCategoryModel> = emptyList(),
        var news: List<NewsModel> = emptyList()
) {
    fun hasCategories() = !categories.isEmpty()

    fun hasNews() = !news.isEmpty()

    fun isEmpty() = hasCategories() && hasNews()
}