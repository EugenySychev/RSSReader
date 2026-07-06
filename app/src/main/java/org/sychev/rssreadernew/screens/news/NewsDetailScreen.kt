package org.sychev.rssreadernew.screens.news

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import org.sychev.domain.model.NewsItem
import java.text.DateFormat
import java.util.Date

@Composable
internal fun NewsDetailScreen(
    link: String,
    newsViewModel: NewsScreenViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by newsViewModel.uiState.collectAsStateWithLifecycle()
    val item = uiState.items.firstOrNull { it.link == link }

    Box(modifier = modifier.fillMaxSize()) {
        if (item != null) {
            NewsDetailContent(item = item, modifier = Modifier.align(Alignment.TopStart))
        } else {
            Text(
                text = "Article not found",
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp),
            )
        }
    }
}

@Composable
private fun NewsDetailContent(item: NewsItem, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        if (item.imageUrl != null) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop,
            )
        }
        Text(
            text = item.title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = 16.dp),
        )
        NewsDetailMeta(item = item, modifier = Modifier.padding(top = 4.dp))
        if (item.description.isNotBlank()) {
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 16.dp),
            )
        }
    }
}

@Composable
private fun NewsDetailMeta(item: NewsItem, modifier: Modifier = Modifier) {
    val meta = listOfNotNull(
        item.author,
        item.category,
        DateFormat.getDateTimeInstance().format(Date(item.pubDate)),
    ).joinToString(" · ")

    if (meta.isNotBlank()) {
        Text(
            text = meta,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = modifier,
        )
    }
}
