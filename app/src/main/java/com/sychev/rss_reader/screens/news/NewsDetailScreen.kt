package com.sychev.rss_reader.screens.news

import android.net.Uri
import android.util.Log
import android.view.textclassifier.TextLinks
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.sychev.domain.model.NewsItem
import java.text.DateFormat
import java.util.Date
import androidx.core.net.toUri

@Composable
internal fun NewsDetailScreen(
    link: String,
    newsViewModel: NewsScreenViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by newsViewModel.uiState.collectAsStateWithLifecycle()
    val items = uiState.items
    val startIndex = items.indexOfFirst { it.link == link }

    if (startIndex == -1) {
        Box(modifier = modifier.fillMaxSize()) {
            Text(
                text = "Article not found",
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp),
            )
        }
        return
    }

    val pagerState = rememberPagerState(initialPage = startIndex) { items.size }
    val currentItem by remember(items) {
        derivedStateOf { items.getOrNull(pagerState.currentPage) }
    }

    LaunchedEffect(currentItem?.link) {
        currentItem?.link?.let { newsViewModel.markAsRead(it) }
    }

    HorizontalPager(
        state = pagerState,
        modifier = modifier.fillMaxSize(),
    ) { page ->
        items.getOrNull(page)?.let { item ->
            NewsDetailContent(item = item)
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
        Log.d("LOG", "Show $item")
        Text(
            text = item.title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = 16.dp),
        )
        if (item.link.isNotBlank()) {
            val annotatedString: AnnotatedString = remember {
                buildAnnotatedString {
                    val styleCenter = SpanStyle(
                        color = Color(0xff64B5F6),
                        fontSize = 20.sp,
                        textDecoration = TextDecoration.Underline
                    )
                    withLink(LinkAnnotation.Url(url = item.link)) {
                        withStyle(
                            style = styleCenter
                        ) {
                            append(item.link.toUri().host)
                        }
                    }
                }
            }
            Text(
                text = annotatedString,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
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
