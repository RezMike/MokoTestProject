/*
 * Copyright 2019 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

package org.example.app.units

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.units.TableUnitItem
import org.example.app.TileNews
import org.example.library.SharedFactory
import org.example.library.feature.list.presentation.ArticleClickListener

class NewsListUnitsFactory : SharedFactory.NewsUnitsFactory {
    override fun createNewsTile(
        id: Int,
        title: String,
        description: StringDesc,
        image: String?,
        listener: ArticleClickListener
    ): TableUnitItem {
        // TileNews is generated by units plugin from https://github.com/icerockdev/moko-units
        // generation based on databinding layouts
        return TileNews().apply {
            itemId = id.toLong()
            this.id = id
            this.title = title
            this.description = description
            this.image = image
            this.listener = listener
        }
    }

    companion object {
        @JvmStatic
        @BindingAdapter("imageUrl")
        fun loadImage(view: ImageView, url: String?) {
            Glide.with(view.context)
                .load(url)
                .into(view)
        }
    }
}
