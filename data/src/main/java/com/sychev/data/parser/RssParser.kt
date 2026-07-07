package com.sychev.data.parser

import com.sychev.domain.model.NewsItem
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

internal data class RssFeed(
    val imageUrl: String?,
    val items: List<NewsItem>,
)

internal class RssParser @Inject constructor() {

    private val dateFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH)

    fun parse(inputStream: InputStream, sourceUrl: String): RssFeed {
        val parser = XmlPullParserFactory.newInstance().newPullParser()
        parser.setInput(inputStream, null)

        val items = mutableListOf<NewsItem>()
        var inItem = false
        var inChannelImage = false
        var currentTag = ""
        var title = ""
        var description = ""
        var link = ""
        var imageUrl: String? = null
        var pubDate = 0L
        var author: String? = null
        var category: String? = null
        var channelImageUrl: String? = null

        var event = parser.eventType
        while (event != XmlPullParser.END_DOCUMENT) {
            when (event) {
                XmlPullParser.START_TAG -> {
                    currentTag = parser.name
                    when {
                        parser.name == "item" -> {
                            inItem = true
                            title = ""; description = ""; link = ""
                            imageUrl = null; pubDate = 0L; author = null; category = null
                        }
                        inItem && parser.name == "enclosure" -> {
                            val type = parser.getAttributeValue(null, "type").orEmpty()
                            if (type.startsWith("image")) {
                                imageUrl = parser.getAttributeValue(null, "url")
                            }
                        }
                        !inItem && parser.name == "image" -> inChannelImage = true
                        !inItem && channelImageUrl == null && parser.name == "itunes:image" -> {
                            channelImageUrl = parser.getAttributeValue(null, "href")
                        }
                        !inItem && channelImageUrl == null && parser.name == "media:thumbnail" -> {
                            channelImageUrl = parser.getAttributeValue(null, "url")
                        }
                    }
                }
                XmlPullParser.TEXT -> {
                    val text = parser.text?.trim().orEmpty()
                    if (text.isNotEmpty()) {
                        if (inItem) {
                            when (currentTag) {
                                "title" -> title = text
                                "description" -> if (description.isEmpty()) description = text
                                "link" -> link = text
                                "pubDate" -> pubDate = parseDate(text)
                                "author", "dc:creator" -> author = text
                                "category" -> if (category == null) category = text
                            }
                        } else if (inChannelImage && currentTag == "url" && channelImageUrl == null) {
                            channelImageUrl = text
                        }
                    }
                }
                XmlPullParser.END_TAG -> {
                    if (inItem && parser.name == "item") {
                        if (title.isNotEmpty()) {
                            items.add(
                                NewsItem(
                                    title = title,
                                    description = description,
                                    link = link,
                                    sourceUrl = sourceUrl,
                                    imageUrl = imageUrl,
                                    pubDate = pubDate,
                                    author = author,
                                    category = category,
                                    isRead = false,
                                )
                            )
                        }
                        inItem = false
                    } else if (!inItem && parser.name == "image") {
                        inChannelImage = false
                    }
                    currentTag = ""
                }
            }
            event = parser.next()
        }

        return RssFeed(imageUrl = channelImageUrl, items = items)
    }

    private fun parseDate(dateStr: String): Long = try {
        dateFormat.parse(dateStr)?.time ?: System.currentTimeMillis()
    } catch (e: Exception) {
        System.currentTimeMillis()
    }
}
