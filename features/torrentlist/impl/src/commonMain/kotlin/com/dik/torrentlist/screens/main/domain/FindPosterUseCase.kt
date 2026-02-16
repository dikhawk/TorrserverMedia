package com.dik.torrentlist.screens.main.domain

import com.dik.appsettings.api.model.AppSettings
import com.dik.common.Result
import com.dik.common.errors.Error
import com.dik.common.onError
import com.dik.common.onSuccess
import com.dik.themoviedb.SearchTheMovieDbApi
import com.dik.themoviedb.model.Content
import com.dik.themoviedb.model.Movie
import com.dik.themoviedb.model.TvShow
import com.dik.torrentlist.utils.fileName
import com.dik.torrserverapi.model.Torrent
import com.dik.videofilenameparser.parseFileNameBase
import com.dik.videofilenameparser.parseFileNameTvShow

internal class FindPosterUseCase(
    private val appSettings: AppSettings,
    private val searchTheMovieDbApi: SearchTheMovieDbApi,
) {

    suspend operator fun invoke(torrent: Torrent): Result<Poster, FindPosterErrors> {
        val firstFile = torrent.files.firstOrNull()
        val name = firstFile?.path?.fileName() ?: torrent.name
        val tv = parseFileNameTvShow(name)
        val movie = parseFileNameBase(torrent.name)
        val seasonNumber = tv?.seasons?.firstOrNull() ?: 0
        val episodeNumber = tv?.episodeNumbers?.firstOrNull() ?: 0
        val isTv = (seasonNumber > 0) && (episodeNumber > 0)
        val title = if (isTv) tv?.title else movie.title
        val language = appSettings.language.iso

        if (title.isNullOrEmpty())
            return Result.Error(FindPosterErrors.TitleIsEmpty)

        searchTheMovieDbApi.multiSearching(query = title, language = language)
            .onSuccess { data ->
                val content: Content = data.firstOrNull()
                    ?: return Result.Error(FindPosterErrors.PosterNotFound)

                val poster = getPoster(content)
                    ?: return Result.Error(FindPosterErrors.PosterNotFound)

                return Result.Success(poster)
            }.onError { error ->
                return Result.Error(
                    FindPosterErrors.UnknownError(error.toString())
                )
            }

        return Result.Error(FindPosterErrors.PosterNotFound)
    }

    private fun getPoster(content: Content): Poster? = when(content) {
        is Movie -> Poster(
            poster300 = content.poster300,
            poster500 = content.poster500,
            posterOriginal = content.posterOriginal,
        )
        is TvShow -> Poster(
            poster300 = content.poster300,
            poster500 = content.poster500,
            posterOriginal = content.posterOriginal,
        )
        else -> null
    }
}

internal data class Poster(
    val poster300: String? = null,
    val poster500: String? = null,
    val posterOriginal: String? = null
)

internal sealed interface FindPosterErrors : Error {
    data object PosterNotFound : FindPosterErrors
    data object TitleIsEmpty : FindPosterErrors
    data class UnknownError(val error: String) : FindPosterErrors
}

