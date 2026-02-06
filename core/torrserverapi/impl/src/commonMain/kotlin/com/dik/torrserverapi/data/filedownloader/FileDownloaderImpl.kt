package com.dik.torrserverapi.data.filedownloader

import com.dik.common.utils.round
import com.dik.torrserverapi.TorrserverError
import com.dik.torrserverapi.domain.filedownloader.DownloadFileRusult
import com.dik.torrserverapi.domain.filedownloader.FileDownloader
import io.ktor.client.HttpClient
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.timeout
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.prepareGet
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.contentLength
import io.ktor.http.headers
import io.ktor.http.isSuccess
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readRemaining
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.io.readByteArray
import okio.BufferedSink
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.buffer
import okio.use

internal class FileDownloaderImpl(
    private val ktor: HttpClient,
    private val fileSystem: FileSystem,
) : FileDownloader {

    override fun downloadFile(
        url: String,
        outputPath: String
    ): Flow<DownloadFileRusult> = channelFlow {
        send(DownloadFileRusult.Starting)
        ktor.prepareGet(url) {
            downloadHeader()
        }.execute { response ->
            validateResponse(response)
            saveResponseToFile(response, outputPath)
        }
        send(DownloadFileRusult.Done)
    }.catch { e ->
        emit(DownloadFileRusult.Erorr(TorrserverError.Unknown(e.toString())))
    }

    private fun HttpRequestBuilder.downloadHeader() {
        timeout {
            connectTimeoutMillis = 60000 * 2
            requestTimeoutMillis = 60000 * 2
            socketTimeoutMillis = 60000 * 2
        }
        headers {
            append(
                "Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/png,image/svg+xml,*/*;q=0.8"
            )
            append(
                "User-Agent",
                "Mozilla/5.0 (X11; Linux x86_64; rv:131.0) Gecko/20100101 Firefox/131.0"
            )
        }
    }

    private fun validateResponse(response: HttpResponse) {
        if (!response.status.isSuccess()) {
            throw ResponseException(response, response.status.description)
        }
    }

    private suspend fun SendChannel<DownloadFileRusult>.writeChannelToSink(
        channel: ByteReadChannel,
        response: HttpResponse,
        path: Path,
        sink: BufferedSink
    ) {

        while (!channel.isClosedForRead) {
            val packet = channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())

            while (!packet.exhausted()) {
                val bytes = packet.readByteArray()

                sink.write(bytes)
                sink.flush()

                postProgressIfNeeded(response, path)
            }
        }
        sink.flush()
    }

    private suspend fun SendChannel<DownloadFileRusult>.postProgressIfNeeded(
        response: HttpResponse,
        path: Path
    ) {
        val totalBytes = response.contentLength() ?: return
        val currentBytes = fileSystem.metadata(path).size ?: return
        val progress = calculateProgress(currentBytes, totalBytes)

        send(DownloadFileRusult.Progress(progress))
    }

    private fun calculateProgress(
        downloadedBytes: Long,
        totalBytes: Long
    ): Double {
        if (totalBytes <= downloadedBytes) {
            return 100.0
        }

        return (downloadedBytes.toDouble() * 100.0 / totalBytes.toDouble()).round(2)
    }

    private suspend fun SendChannel<DownloadFileRusult>.saveResponseToFile(
        response: HttpResponse,
        outputFilePath: String
    ) {
        val channel = response.bodyAsChannel()
        val path = outputFilePath.toPath()

        prepareFile(path)

        fileSystem.sink(path).buffer().use { sink ->
            writeChannelToSink(
                channel = channel,
                response = response,
                path = path,
                sink = sink
            )
        }
    }

    private fun prepareFile(path: Path) {
        fileSystem.createDirectories(path.parent!!)
        fileSystem.delete(path)
    }
}