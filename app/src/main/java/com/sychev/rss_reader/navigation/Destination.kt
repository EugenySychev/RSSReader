package com.sychev.rss_reader.navigation

import android.net.Uri
import androidx.annotation.StringRes
import com.sychev.rss_reader.R

internal sealed class Destination(val route: String, @StringRes val label: Int) {
    data object News : Destination("news", R.string.destination_news)
    data object Sources : Destination("sources", R.string.destination_sources)
    data object Settings : Destination("settings", R.string.destination_settings)
    data object NewsDetail : Destination("news_detail/{link}", R.string.destination_article) {
        fun createRoute(link: String) = "news_detail/${Uri.encode(link)}"
    }
    data object NewsSource : Destination("news/{link}", R.string.destination_news_for_source) {
        fun createRoute(link: String) = "news/${Uri.encode(link)}"
    }
}

internal val drawerDestinations = listOf(Destination.Sources, Destination.Settings)
