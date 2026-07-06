package org.sychev.data.parser

import org.sychev.domain.model.NewsItem
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Locale

internal class RssParser {

    private val dateFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH)

    fun parse(inputStream: InputStream): List<NewsItem> {
        val parser = XmlPullParserFactory.newInstance().newPullParser()
        parser.setInput(inputStream, "UTF-8")

        val items = mutableListOf<NewsItem>()
        var inItem = false
        var currentTag = ""
        var title = ""
        var description = ""
        var link = ""
        var imageUrl: String? = null
        var pubDate = 0L
        var author: String? = null
        var category: String? = null

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
                    }
                }
                XmlPullParser.TEXT -> {
                    if (inItem) {
                        val text = parser.text?.trim().orEmpty()
                        if (text.isNotEmpty()) {
                            when (currentTag) {
                                "title" -> title = text
                                "description" -> if (description.isEmpty()) description = text
                                "link" -> link = text
                                "pubDate" -> pubDate = parseDate(text)
                                "author", "dc:creator" -> author = text
                                "category" -> if (category == null) category = text
                            }
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
                                    imageUrl = imageUrl,
                                    pubDate = pubDate,
                                    author = author,
                                    category = category,
                                )
                            )
                        }
                        inItem = false
                    }
                    currentTag = ""
                }
            }
            event = parser.next()
        }

        return items
    }

    private fun parseDate(dateStr: String): Long = try {
        dateFormat.parse(dateStr)?.time ?: System.currentTimeMillis()
    } catch (e: Exception) {
        System.currentTimeMillis()
    }
}
