package com.dik.themoviedb

import com.dik.common.Result
import com.dik.themoviedb.errors.TheMovieDbError
import com.dik.themoviedb.model.TvEpisode

interface TvEpisodesTheMovieDbApi {

    suspend fun details(
        seriesId: Int,
        seasonNumber: Int,
        episodeNumber: Int,
        language: String = "ru-Ru"
    ): Result<TvEpisode, TheMovieDbError>
}