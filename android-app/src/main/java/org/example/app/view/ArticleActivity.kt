/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package org.example.app.view

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import dev.icerock.moko.mvvm.MvvmActivity
import dev.icerock.moko.mvvm.createViewModelFactory
import org.example.app.AppComponent
import org.example.app.BR
import org.example.app.R
import org.example.app.databinding.ActivityArticleBinding
import org.example.library.domain.entity.News
import org.example.library.feature.article.presentation.ArticleViewModel

class ArticleActivity : MvvmActivity<ActivityArticleBinding, ArticleViewModel<Int, News>>() {

    override val layoutId: Int = R.layout.activity_article
    override val viewModelClass = ArticleViewModel::class.java as Class<ArticleViewModel<Int, News>>
    override val viewModelVariableId: Int = BR.viewModel

    override fun viewModelFactory(): ViewModelProvider.Factory = createViewModelFactory {
        AppComponent.factory.articleFactory.createArticleViewModel(
            id = intent.getIntExtra(ARTICLE_ID, -1)
        )
    }

    companion object {
        const val ARTICLE_ID = "ARTICLE_ID"
    }
}

fun Context.startArticleActivity(articleId: Int) {
    val intent = Intent(this, ArticleActivity::class.java)
    intent.putExtra(ArticleActivity.ARTICLE_ID, articleId)
    startActivity(intent)
}
