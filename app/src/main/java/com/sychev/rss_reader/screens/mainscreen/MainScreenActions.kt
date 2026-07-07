package com.sychev.rss_reader.screens.mainscreen

import com.sychev.domain.model.SourceItem

internal sealed class MainScreenActions {
    data class OnNavigateFromMenu(val route: String, val url: String) : MainScreenActions()
    data class SelectNewsSource(val source: SourceItem) : MainScreenActions()
    data object SelectSettings : MainScreenActions()
    data object SelectSourcesScreen : MainScreenActions()
}