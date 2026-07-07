package com.sychev.data.parser

import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

internal class RssFeedFetcher @Inject constructor(
    private val parser: RssParser,
) {

    fun fetch(url: String): RssFeed {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.connectTimeout = 10_000
        connection.readTimeout = 30_000
        return try {
            parser.parse(connection.inputStream, url)
        } finally {
            connection.disconnect()
        }
    }
}
