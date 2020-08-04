/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package org.example.library.feature.list.presentation

import com.github.aakira.napier.Napier
import dev.icerock.moko.mvvm.State
import dev.icerock.moko.mvvm.asState
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcher
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcherOwner
import dev.icerock.moko.mvvm.livedata.*
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.units.TableUnitItem
import kotlinx.coroutines.launch
import org.example.library.feature.list.model.ListSource

class ListViewModel<T>(
    override val eventsDispatcher: EventsDispatcher<EventsListener>,
    private val listSource: ListSource<T>,
    private val strings: Strings,
    private val unitsFactory: UnitsFactory<T>
) : ViewModel(),
    EventsDispatcherOwner<ListViewModel.EventsListener>,
    ArticleClickListener {

    private val _state: MutableLiveData<State<List<T>, Throwable>> =
        MutableLiveData(initialValue = State.Loading())

    val state: LiveData<State<List<TableUnitItem>, StringDesc>> = _state
        .dataTransform {
            map { news ->
                news.map { unitsFactory.createTile(it, this@ListViewModel) }
            }
        }
        .errorTransform {
            // new type inference require set types oO
            map<Throwable, StringDesc> { it.message?.desc() ?: strings.unknownError.desc() }
        }

    init {
        loadList()
    }

    override fun onArticleClick(id: Int) {
        Napier.d("SHOW $id")
        eventsDispatcher.dispatchEvent { routeToArticle(id) }
    }

    fun onRetryPressed() {
        loadList()
    }

    fun onRefresh(completion: () -> Unit) {
        viewModelScope.launch {
            try {
                val items = listSource.getList()

                _state.value = items.asState()
            } catch (error: Throwable) {
                Napier.e("can't refresh", throwable = error)
            } finally {
                completion()
            }
        }
    }

    private fun loadList() {
        viewModelScope.launch {
            try {
                _state.value = State.Loading()

                val items = listSource.getList()

                _state.value = items.asState()
            } catch (error: Throwable) {
                _state.value = State.Error(error)
            }
        }
    }

    interface UnitsFactory<T> {
        fun createTile(data: T, listener: ArticleClickListener): TableUnitItem
    }

    interface Strings {
        val unknownError: StringResource
    }

    interface EventsListener {
        fun routeToArticle(id: Int)
    }
}

interface ArticleClickListener {
    fun onArticleClick(id: Int)
}
