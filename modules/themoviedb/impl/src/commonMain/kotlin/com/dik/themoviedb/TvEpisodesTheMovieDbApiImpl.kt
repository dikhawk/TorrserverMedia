package com.dik.themoviedb

import com.dik.common.Result
import com.dik.themoviedb.errors.TheMovieDbError
import com.dik.themoviedb.mapper.mapToTvEpisode
import com.dik.themoviedb.model.TvEpisode
import com.dik.themoviedb.response.TvEpisodeResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.isSuccess

internal class TvEpisodesTheMovieDbApiImpl(
    private val httpClient: HttpClient
) : TvEpisodesTheMovieDbApi {

    override suspend fun details(
        seriesId: Int,
        seasonNumber: Int,
        episodeNumber: Int,
        language: String
    ): Result<TvEpisode, TheMovieDbError> {
        try {
            val response =
                httpClient.get("tv/$seriesId/season/$seasonNumber/episode/$episodeNumber") {
                    parameter("language", language)
                }

            if (!response.status.isSuccess()) {
                return Result.Error(TheMovieDbError.HttpError.ResponseReturnError(response.status.description))
            }

            val result = response.body<TvEpisodeResponse>()

            return Result.Success(result.mapToTvEpisode())
        } catch (e: Exception) {
            return Result.Error(TheMovieDbError.Unknown(e.toString()))
        }
    }
}