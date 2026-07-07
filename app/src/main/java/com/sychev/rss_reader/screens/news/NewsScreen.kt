package com.sychev.rss_reader.screens.news

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.sychev.domain.model.NewsItem
import com.sychev.domain.model.getLocalTimeString
import com.sychev.rss_reader.R
import com.sychev.rss_reader.events.AppConsumer
import com.sychev.rss_reader.events.AppEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NewsScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: NewsScreenViewModel = hiltViewModel(),
    link: String,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val filterQuery = remember { mutableStateOf("") }
    LaunchedEffect(navController) {
        viewModel.setNavController(navController)
    }

    AppConsumer { event ->
        when (event) {
            AppEvent.RefreshNews -> viewModel.onRefresh()
            is AppEvent.SortNews -> viewModel.onSortOrderChanged(event.order)
            is AppEvent.SearchNews -> {
                filterQuery.value = event.query
            }
        }
    }

    PullToRefreshBox(
        isRefreshing = uiState.isRefreshing,
        onRefresh = viewModel::onRefresh,
        modifier = modifier.fillMaxSize(),
    ) {
        when {
            uiState.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
            uiState.error != null -> Text(
                text = uiState.error.orEmpty(),
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp),
            )

            else -> NewsList(
                items = if (link.isEmpty()) {
                    uiState.items
                } else {
                    uiState.items.filter { it.sourceUrl == link }
                },
                onAction = viewModel::onAction,
                filter = filterQuery,
            )
        }
    }
}

@Composable
private fun NewsList(
    items: List<NewsItem>,
    onAction: (NewsScreenAction) -> Unit,
    modifier: Modifier = Modifier,
    filter: MutableState<String>,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = dimensionResource(R.dimen.news_item_padding)),
    ) {
        items(items) { item ->
            if (
                filter.value.isEmpty() ||
                (item.title.contains(filter.value) || item.description.contains(filter.value))
            ) {
                NewsListItem(
                    item = item,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = dimensionResource(R.dimen.news_item_padding))
                        .clickable { onAction(NewsScreenAction.OpenItem(item)) },
                )
            }
        }
    }
}

@SuppressLint("SimpleDateFormat")
@Composable
private fun NewsListItem(item: NewsItem, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (item.imageUrl != null) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.title,
                modifier = Modifier
                    .height(dimensionResource(R.dimen.news_item_image_height))
                    .width(dimensionResource(R.dimen.news_item_image_width))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Fit,
            )
        }
        Column(modifier = Modifier.padding(dimensionResource(R.dimen.news_item_padding))) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontWeight = if (item.isRead) {
                    FontWeight.Normal
                } else {
                    FontWeight.Bold
                },
            )
            Text(
                text = item.getLocalTimeString(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 4.dp),
                fontWeight = if (item.isRead) {
                    FontWeight.Normal
                } else {
                    FontWeight.Bold
                },
            )
        }
    }
}
