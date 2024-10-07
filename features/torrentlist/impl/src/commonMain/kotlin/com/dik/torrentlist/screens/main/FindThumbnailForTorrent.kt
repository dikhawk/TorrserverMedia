package com.dik.torrentlist.screens.main

import com.dik.common.Result
import com.dik.themoviedb.SearchTheMovieDbApi
import com.dik.themoviedb.model.Movie
import com.dik.themoviedb.model.TvShow
import com.dik.torrentlist.utils.fileName
import com.dik.torrserverapi.model.Torrent
import com.dik.videofilenameparser.parseFileNameBase
import com.dik.videofilenameparser.parseFileNameTvShow

internal class FindThumbnailForTorrent(
    private val searchTheMovieDbApi: SearchTheMovieDbApi,
) {

    suspend operator fun invoke(torrent: Torrent): FindThumbnailForTorrentResult {
        val firstFile = torrent.files.firstOrNull()
        val name = firstFile?.path?.fileName() ?: torrent.name
        val tv = parseFileNameTvShow(name)
        val movie = parseFileNameBase(torrent.name)
        val seasonNumber = tv?.seasons?.firstOrNull() ?: 0
        val episodeNumber = tv?.episodeNumbers?.firstOrNull() ?: 0
        val isTv = (seasonNumber > 0) && (episodeNumber > 0)
        val title = if (isTv) tv?.title else movie.title

        if(!title.isNullOrEmpty()) {
            when(val queryResult = searchTheMovieDbApi.multiSearching(title)) {
                is Result.Error -> return FindThumbnailForTorrentResult(error = queryResult.error.toString())
                is Result.Success -> {
                    val content = queryResult.data.firstOrNull()
                        ?: return FindThumbnailForTorrentResult(error = "Thumbnail not found")

                    val thumbnail = when (content) {
                        is Movie -> content.posterPath
                        is TvShow -> content.posterPath
                        else -> return FindThumbnailForTorrentResult(error = "Thumbnail not found")
                    }

                    return FindThumbnailForTorrentResult(thumbnail = thumbnail)
                }
            }
        }

        return FindThumbnailForTorrentResult(error = "Thumbnail not found")
    }
}

internal data class FindThumbnailForTorrentResult(
    val error: String? = null,
    val thumbnail: String? = null
)