/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package org.example.library.feature.article.presentation

import dev.icerock.moko.mvvm.State
import dev.icerock.moko.mvvm.asState
import dev.icerock.moko.mvvm.livedata.LiveData
import dev.icerock.moko.mvvm.livedata.MutableLiveData
import dev.icerock.moko.mvvm.livedata.errorTransform
import dev.icerock.moko.mvvm.livedata.map
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.launch
import org.example.library.feature.article.model.ArticleSource

class ArticleViewModel<K, T>(
    private val articleSource: ArticleSource<K, T>,
    private val strings: Strings,
    private val id: K
) : ViewModel() {

    private val _state: MutableLiveData<State<T, String?>> =
        MutableLiveData(initialValue = State.Loading())

    val state: LiveData<State<T, StringDesc>> = _state
        .errorTransform {
            // new type inference require set types
            map<String?, StringDesc> { it?.desc() ?: strings.unknownError.desc() }
        }

    init {
        loadArticle()
    }

    private fun loadArticle() {
        viewModelScope.launch {
            try {
                _state.value = State.Loading()

                val article = articleSource.getArticle(id)

                _state.value = article?.asState() ?: State.Empty()
            } catch (error: Throwable) {
                _state.value = State.Error(error.message)
            }
        }
    }

    interface Strings {
        val unknownError: StringResource
    }
}
