package com.sychev.rss_reader.events

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import com.sychev.rss_reader.screens.news.NewsSortOrder

internal sealed interface AppEvent {
    data object RefreshNews : AppEvent
    data class SortNews(val order: NewsSortOrder) : AppEvent
    data class SearchNews(val query: String) : AppEvent
}

private val LocalAppEvents = staticCompositionLocalOf<MutableSharedFlow<AppEvent>> {
    error("AppProvider { ... } is missing from the composition")
}

@Composable
fun AppProvider(content: @Composable () -> Unit) {
    val events = remember { MutableSharedFlow<AppEvent>(extraBufferCapacity = 1) }
    CompositionLocalProvider(LocalAppEvents provides events, content = content)
}

@Composable
internal fun rememberAppEventEmitter(): (AppEvent) -> Unit {
    val events = LocalAppEvents.current
    val scope = rememberCoroutineScope()
    return remember(events, scope) {
        { event: AppEvent -> scope.launch { events.emit(event) } }
    }
}

@Composable
internal fun AppConsumer(onEvent: (AppEvent) -> Unit) {
    val events = LocalAppEvents.current
    LaunchedEffect(events) {
        events.collect { event -> onEvent(event) }
    }
}
