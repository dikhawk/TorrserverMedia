package com.dik.torrentlist.screens.main

import com.dik.common.Result
import com.dik.common.errors.Error
import com.dik.themoviedb.SearchTheMovieDbApi
import com.dik.themoviedb.model.Content
import com.dik.themoviedb.model.Movie
import com.dik.themoviedb.model.TvShow
import com.dik.torrentlist.utils.fileName
import com.dik.torrserverapi.model.Torrent
import com.dik.videofilenameparser.parseFileNameBase
import com.dik.videofilenameparser.parseFileNameTvShow

internal class FindPosterForTorrent(
    private val searchTheMovieDbApi: SearchTheMovieDbApi,
) {

    suspend operator fun invoke(torrent: Torrent): Result<Poster, FindPosterForTorrentErrors> {
        val firstFile = torrent.files.firstOrNull()
        val name = firstFile?.path?.fileName() ?: torrent.name
        val tv = parseFileNameTvShow(name)
        val movie = parseFileNameBase(torrent.name)
        val seasonNumber = tv?.seasons?.firstOrNull() ?: 0
        val episodeNumber = tv?.episodeNumbers?.firstOrNull() ?: 0
        val isTv = (seasonNumber > 0) && (episodeNumber > 0)
        val title = if (isTv) tv?.title else movie.title

        if (!title.isNullOrEmpty()) {
            when (val queryResult = searchTheMovieDbApi.multiSearching(title)) {
                is Result.Error -> return Result.Error(
                    FindPosterForTorrentErrors.UnknownError(queryResult.error.toString())
                )

                is Result.Success -> {
                    val content: Content = queryResult.data.firstOrNull()
                        ?: return Result.Error(FindPosterForTorrentErrors.PosterNotFound)

                    val poster = when (content) {
                        is Movie -> Poster(
                            poster300 = content.poster300,
                            poster500 = content.poster300,
                            posterOriginal = content.poster300,
                        )
                        is TvShow -> Poster(
                            poster300 = content.poster300,
                            poster500 = content.poster300,
                            posterOriginal = content.poster300,
                        )
                        else -> return Result.Error(FindPosterForTorrentErrors.PosterNotFound)
                    }

                    return Result.Success(poster)
                }
            }
        }

        return Result.Error(FindPosterForTorrentErrors.PosterNotFound)
    }
}

internal data class Poster(
    val poster300: String? = null,
    val poster500: String? = null,
    val posterOriginal: String? = null
)

internal sealed interface FindPosterForTorrentErrors : Error {
    data object PosterNotFound : FindPosterForTorrentErrors
    data class UnknownError(val error: String) : FindPosterForTorrentErrors
}

