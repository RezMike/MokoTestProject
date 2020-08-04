/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package org.example.library.feature.list.di

import dev.icerock.moko.mvvm.dispatcher.EventsDispatcher
import org.example.library.feature.list.model.ListSource
import org.example.library.feature.list.presentation.ListViewModel

class ListFactory<T>(
    private val listSource: ListSource<T>,
    private val strings: ListViewModel.Strings,
    private val unitsFactory: ListViewModel.UnitsFactory<T>
) {
    fun createListViewModel(
        eventsDispatcher: EventsDispatcher<ListViewModel.EventsListener>
    ): ListViewModel<T> {
        return ListViewModel(
            eventsDispatcher = eventsDispatcher,
            listSource = listSource,
            strings = strings,
            unitsFactory = unitsFactory
        )
    }
}
