package com.sychev.rss_reader.screens.news

import com.sychev.domain.model.NewsItem

internal sealed class NewsScreenAction {
    data class OpenItem(
        val item: NewsItem,
    ) : NewsScreenAction()

    data object ReadMore : NewsScreenAction()
}
