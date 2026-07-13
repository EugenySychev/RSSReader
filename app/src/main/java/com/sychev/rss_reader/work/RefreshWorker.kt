package com.sychev.rss_reader.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import com.sychev.domain.repositories.NewsRepository
import com.sychev.domain.repositories.SourceRepository

@HiltWorker
internal class RefreshWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val newsRepository: NewsRepository,
    private val sourceRepository: SourceRepository,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            newsRepository.deleteOldNews()
            val sources = sourceRepository.getSources().first().filter { it.isEnabled }
            for (source in sources) {
                try {
                    newsRepository.fetchLatestNews(source.url)
                } catch (e: Exception) {
                    // Skip this source and keep refreshing the rest.
                }
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
