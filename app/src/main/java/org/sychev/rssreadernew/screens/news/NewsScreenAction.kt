package org.sychev.rssreadernew.screens.news

import org.sychev.domain.model.NewsItem

internal sealed class NewsScreenAction {
    data class OpenItem(
        val item: NewsItem,
    ) : NewsScreenAction()

    data object ReadMore : NewsScreenAction()
}
