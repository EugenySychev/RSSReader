package org.sychev.rssreadernew.navigation

import android.net.Uri

internal sealed class Destination(val route: String, val label: String) {
    data object News : Destination("news", "News")
    data object Sources : Destination("sources", "Sources")
    data object Settings : Destination("settings", "Settings")
    data object NewsDetail : Destination("news_detail/{link}", "Article") {
        fun createRoute(link: String) = "news_detail/${Uri.encode(link)}"
    }
}

internal val drawerDestinations = listOf(Destination.News, Destination.Sources, Destination.Settings)
