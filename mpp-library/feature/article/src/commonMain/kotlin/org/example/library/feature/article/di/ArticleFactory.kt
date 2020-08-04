/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package org.example.library.feature.article.di

import org.example.library.feature.article.model.ArticleSource
import org.example.library.feature.article.presentation.ArticleViewModel

class ArticleFactory<K, T>(
    private val articleSource: ArticleSource<K, T>,
    private val strings: ArticleViewModel.Strings
) {
    fun createArticleViewModel(id: K): ArticleViewModel<K, T> {
        return ArticleViewModel(
            articleSource = articleSource,
            strings = strings,
            id = id
        )
    }
}
