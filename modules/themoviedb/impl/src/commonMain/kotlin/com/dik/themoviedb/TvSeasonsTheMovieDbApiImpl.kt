package com.dik.themoviedb

import com.dik.common.Result
import com.dik.themoviedb.errors.TheMovieDbError
import com.dik.themoviedb.mapper.mapToTvSeason
import com.dik.themoviedb.model.TvSeason
import com.dik.themoviedb.response.TvSeasonResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.isSuccess

internal class TvSeasonsTheMovieDbApiImpl(
    private val httpClient: HttpClient
) : TvSeasonsTheMovieDbApi {

    override suspend fun details(
        seriesId: Int,
        seasonNumber: Int,
        language: String
    ): Result<TvSeason, TheMovieDbError> {
        try {
            val response =
                httpClient.get("tv/$seriesId/season/$seasonNumber") {
                    parameter("language", language)
                }

            if (!response.status.isSuccess()) {
                return Result.Error(TheMovieDbError.HttpError.ResponseReturnError(response.status.description))
            }

            val result = response.body<TvSeasonResponse>()

            return Result.Success(result.mapToTvSeason())
        } catch (e: Exception) {
            return Result.Error(TheMovieDbError.Unknown(e.toString()))
        }
    }
}