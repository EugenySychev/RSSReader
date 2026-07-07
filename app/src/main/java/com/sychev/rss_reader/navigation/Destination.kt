package com.sychev.rss_reader.navigation

import android.net.Uri

internal sealed class Destination(val route: String, val label: String) {
    data object News : Destination("news", "News")
    data object Sources : Destination("sources", "Sources")
    data object Settings : Destination("settings", "Settings")
    data object NewsDetail : Destination("news_detail/{link}", "Article") {
        fun createRoute(link: String) = "news_detail/${Uri.encode(link)}"
    }
    data object NewsSource : Destination("news/{link}", "NewsForSource") {
        fun createRoute(link: String) = "news/${Uri.encode(link)}"
    }
}

internal val drawerDestinations = listOf(Destination.Sources, Destination.Settings)
