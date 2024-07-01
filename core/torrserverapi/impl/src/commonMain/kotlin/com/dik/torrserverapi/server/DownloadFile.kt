package com.dik.torrserverapi.server

import com.dik.common.AppDispatchers
import com.dik.common.Progress
import com.dik.common.Result
import com.dik.common.utils.round
import com.dik.torrserverapi.TorrserverError
import com.dik.torrserverapi.model.TorrserverFile
import io.ktor.client.HttpClient
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.contentLength
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.core.isEmpty
import io.ktor.utils.io.core.readBytes
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.buffer
import java.io.File

internal class DownloadFile(
    private val dispatchers: AppDispatchers,
    private val ktor: HttpClient
) {

    fun start(fileUrl: String, outputFilePath: String) =
        flow<Result<TorrserverFile, TorrserverError>> {
            emit(Result.Loading(Progress(progress = 0.0)))

            val pathFile = outputFilePath.toPath()
            FileSystem.SYSTEM.createDirectories(pathFile.parent!!)
            FileSystem.SYSTEM.delete(pathFile)
            val sink = FileSystem.SYSTEM.sink(pathFile).buffer()
            ktor.prepareGet(fileUrl).execute { response ->
                val channel: ByteReadChannel = response.bodyAsChannel()
                while (!channel.isClosedForRead) {
                    val packet = channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())
                    while (!packet.isEmpty) {
                        val bytes = packet.readBytes()
                        val totalBytes = response.contentLength() ?: 0L
                        val fileSize = FileSystem.SYSTEM.metadata(pathFile).size

                        sink.write(bytes)

                        if (totalBytes > 0L)
                            emit(
                                Result.Loading(
                                    Progress(
                                        progress = calculateProgress(fileSize!!, totalBytes),
                                        currentBytes = fileSize,
                                        totalBytes = totalBytes
                                    )
                                )
                            )
                    }
                }
            }


            emit(Result.Success(TorrserverFile(filePath = outputFilePath)))
        }.catch { e ->
            emit(Result.Error(TorrserverError.Common.Unknown(e.toString())))
        }.flowOn(dispatchers.defaultDispatcher())


    private fun calculateProgress(downloadedBytes: Long, totalBytes: Long): Double {
        return (downloadedBytes.toDouble() * 100.0 / totalBytes.toDouble()).round(2)
    }
}
