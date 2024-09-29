package com.dik.themoviedb

import com.dik.common.Result
import com.dik.themoviedb.errors.TheMovieDbError
import com.dik.themoviedb.model.TvSeason

interface TvSeasonsTheMovieDbApi {

    suspend fun details(
        seriesId: Int,
        seasonNumber: Int,
        language: String = "ru-Ru"
    ): Result<TvSeason, TheMovieDbError>
}