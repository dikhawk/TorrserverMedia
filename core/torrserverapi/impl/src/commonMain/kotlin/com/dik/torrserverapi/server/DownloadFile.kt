package com.dik.torrserverapi.server

import co.touchlab.kermit.Logger
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
import io.ktor.http.headers
import io.ktor.http.isSuccess
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readRemaining
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.io.readByteArray
import okio.BufferedSink
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.buffer
import okio.use

internal class DownloadFile(
    private val dispatchers: AppDispatchers,
    private val ktor: HttpClient
) {
    private val tag = "DownloadFile: "

    operator fun invoke(
        fileUrl: String,
        outputFilePath: String
    ): Flow<ResultProgress<TorrserverFile, TorrserverError>> {
        Logger.i("$tag Started download file from $fileUrl to $outputFilePath")
        var sink: BufferedSink? = null

        return flow<ResultProgress<TorrserverFile, TorrserverError>> {
            emit(ResultProgress.Loading(Progress(progress = 0.0)))

            ktor.prepareGet(fileUrl) {
                timeout {
                    connectTimeoutMillis = 60000 * 2
                    requestTimeoutMillis = 60000 * 2
                    socketTimeoutMillis = 60000 * 2
                }
                request {
                    headers {
                        append("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/png,image/svg+xml,*/*;q=0.8")
                        append("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:131.0) Gecko/20100101 Firefox/131.0")
                    }
                }
            }.execute { response ->
                if (!response.status.isSuccess()) {
                    emit(ResultProgress.Error(TorrserverError.HttpError.ResponseReturnError(response.status.description)))
                    return@execute
                }

                val channel: ByteReadChannel = response.bodyAsChannel()
                val pathFile = outputFilePath.toPath()
                FileSystem.SYSTEM.createDirectories(pathFile.parent!!)
                FileSystem.SYSTEM.delete(pathFile)
                sink = FileSystem.SYSTEM.sink(pathFile).buffer()

                sink.use { sink ->
                    while (!channel.isClosedForRead) {
                        val packet = channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())

                        while (!packet.exhausted()) {
                            val bytes = packet.readByteArray()
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
            }

            Logger.i("$tag Successfully download file from $fileUrl to $outputFilePath")
            emit(ResultProgress.Success(TorrserverFile(filePath = outputFilePath)))
        }.catch { e ->
            emit(ResultProgress.Error(TorrserverError.Unknown(e.toString())))
            Logger.e("$tag $e")
            sink?.flush()
            sink?.close()
        }.flowOn(dispatchers.defaultDispatcher())
    }


    private fun calculateProgress(downloadedBytes: Long, totalBytes: Long): Double {
        return (downloadedBytes.toDouble() * 100.0 / totalBytes.toDouble()).round(2)
    }
}
