package com.dik.torrserverapi.server

import com.dik.common.AppDispatchers
import com.dik.common.Progress
import com.dik.common.ResultProgress
import com.dik.common.utils.round
import com.dik.torrserverapi.TorrserverError
import com.dik.torrserverapi.model.TorrserverFile
import io.ktor.client.HttpClient
import io.ktor.client.plugins.timeout
import io.ktor.client.request.prepareGet
import io.ktor.client.request.request
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.contentLength
import io.ktor.http.isSuccess
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.core.isEmpty
import io.ktor.utils.io.core.readBytes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okio.BufferedSink
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.buffer

internal class DownloadFile(
    private val dispatchers: AppDispatchers,
    private val ktor: HttpClient
) {

    operator fun invoke(
        fileUrl: String,
        outputFilePath: String
    ): Flow<ResultProgress<TorrserverFile, TorrserverError>> {
        var sink: BufferedSink? = null

        return flow<ResultProgress<TorrserverFile, TorrserverError>> {
            emit(ResultProgress.Loading(Progress(progress = 0.0)))

            val pathFile = outputFilePath.toPath()
            FileSystem.SYSTEM.createDirectories(pathFile.parent!!)
            FileSystem.SYSTEM.delete(pathFile)
            sink = FileSystem.SYSTEM.sink(pathFile).buffer()
            ktor.prepareGet(fileUrl).apply {
                request {
//                    headers {
//                        append("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/png,image/svg+xml,*/*;q=0.8")
//                        append("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:131.0) Gecko/20100101 Firefox/131.0")
//                    }
                    timeout {
                        requestTimeoutMillis = 15000
                        socketTimeoutMillis = 60000 * 2
                    }
                }
            }.execute { response ->
                if (!response.status.isSuccess()) {
                    emit(ResultProgress.Error(TorrserverError.HttpError.ResponseReturnError(response.status.description)))
                    return@execute
                }

                val channel: ByteReadChannel = response.bodyAsChannel()


                while (!channel.isClosedForRead) {
                    val packet = channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())

                    while (!packet.isEmpty) {
                        val bytes = packet.readBytes()
                        val totalBytes = response.contentLength() ?: 0L
                        val fileSize = FileSystem.SYSTEM.metadata(pathFile).size

                        sink?.write(bytes)

                        if (totalBytes > 0L) {
                            val progress = calculateProgress(fileSize!!, totalBytes)

                            emit(
                                ResultProgress.Loading(
                                    Progress(
                                        progress = progress,
                                        currentBytes = fileSize,
                                        totalBytes = totalBytes
                                    )
                                )
                            )
                        }
                    }
                }

                sink?.flush()
                sink?.close()
            }

            emit(ResultProgress.Success(TorrserverFile(filePath = outputFilePath)))
        }.catch { e ->
            emit(ResultProgress.Error(TorrserverError.Unknown(e.toString())))
            sink?.flush()
            sink?.close()
        }.flowOn(dispatchers.defaultDispatcher())
    }


    private fun calculateProgress(downloadedBytes: Long, totalBytes: Long): Double {
        return (downloadedBytes.toDouble() * 100.0 / totalBytes.toDouble()).round(2)
    }
}
