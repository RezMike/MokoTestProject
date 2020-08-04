/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package org.example.library

import com.github.aakira.napier.Antilog
import com.github.aakira.napier.Napier
import com.russhwolf.settings.Settings
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.units.TableUnitItem
import org.example.library.domain.di.DomainFactory
import org.example.library.domain.entity.News
import org.example.library.feature.article.di.ArticleFactory
import org.example.library.feature.article.model.ArticleSource
import org.example.library.feature.article.presentation.ArticleViewModel
import org.example.library.feature.config.di.ConfigFactory
import org.example.library.feature.config.model.ConfigStore
import org.example.library.feature.config.presentation.ConfigViewModel
import org.example.library.feature.list.di.ListFactory
import org.example.library.feature.list.model.ListSource
import org.example.library.feature.list.presentation.ArticleClickListener
import org.example.library.feature.list.presentation.ListViewModel

class SharedFactory(
    settings: Settings,
    antilog: Antilog,
    baseUrl: String,
    newsUnitsFactory: NewsUnitsFactory
) {
    private val domainFactory = DomainFactory(
        settings = settings,
        baseUrl = baseUrl
    )

    val configFactory = ConfigFactory(
        configStore = object : ConfigStore {
            override var apiToken: String?
                get() = domainFactory.configRepository.apiToken
                set(value) {
                    domainFactory.configRepository.apiToken = value
                }
            override var language: String?
                get() = domainFactory.configRepository.language
                set(value) {
                    domainFactory.configRepository.language = value
                }
        },
        validations = object : ConfigViewModel.Validations {
            override fun validateToken(value: String): StringDesc? {
                return if (value.isBlank()) {
                    MR.strings.invalid_token.desc()
                } else {
                    null
                }
            }

            override fun validateLanguage(value: String): StringDesc? {
                val validValues = listOf("ru", "us")
                return if (validValues.contains(value)) {
                    null
                } else {
                    StringDesc.ResourceFormatted(
                        MR.strings.invalid_language_s,
                        validValues.joinToString(", ")
                    )
                }
            }
        },
        defaultToken = "ed155d0a445e4b4fbd878fe1f3bc1b7f",
        defaultLanguage = "us"
    )

    val newsFactory: ListFactory<News> = ListFactory(
        listSource = object : ListSource<News> {
            override suspend fun getList(): List<News> {
                return domainFactory.newsRepository.getNewsList()
            }
        },
        strings = object : ListViewModel.Strings {
            override val unknownError: StringResource = MR.strings.unknown_error
        },
        unitsFactory = object : ListViewModel.UnitsFactory<News> {
            override fun createTile(data: News, listener: ArticleClickListener): TableUnitItem {
                return newsUnitsFactory.createNewsTile(
                    id = data.id,
                    title = data.fullName.orEmpty(),
                    description = data.description?.desc() ?: MR.strings.no_description.desc(),
                    image = data.image,
                    listener = listener
                )
            }
        }
    )

    val articleFactory = ArticleFactory(
        articleSource = object : ArticleSource<Int, News> {
            override suspend fun getArticle(id: Int): News? {
                return domainFactory.newsRepository.getNews(id)
            }
        },
        strings = object : ArticleViewModel.Strings {
            override val unknownError: StringResource = MR.strings.unknown_error
        }
    )

    init {
        Napier.base(antilog)
    }

    interface NewsUnitsFactory {
        fun createNewsTile(
            id: Int,
            title: String,
            description: StringDesc,
            image: String?,
            listener: ArticleClickListener
        ): TableUnitItem
    }
}
