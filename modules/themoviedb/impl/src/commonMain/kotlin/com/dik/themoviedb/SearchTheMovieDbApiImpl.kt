package com.dik.themoviedb

import com.dik.common.Result
import com.dik.themoviedb.errors.TheMovieDbError
import com.dik.themoviedb.mapper.mapToListContent
import com.dik.themoviedb.mapper.mapToMovieList
import com.dik.themoviedb.mapper.mapToPersonList
import com.dik.themoviedb.mapper.mapToTvShowList
import com.dik.themoviedb.model.Content
import com.dik.themoviedb.model.Movie
import com.dik.themoviedb.model.Person
import com.dik.themoviedb.model.TvShow
import com.dik.themoviedb.response.MovieResponse
import com.dik.themoviedb.response.PageResponse
import com.dik.themoviedb.response.PersonResponse
import com.dik.themoviedb.response.PolymorphContentResponse
import com.dik.themoviedb.response.TvShowResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.isSuccess


internal class SearchTheMovieDbApiImpl(
    private val httpClient: HttpClient
): SearchTheMovieDbApi {

    override suspend fun multiSearching(
        query: String,
        includeAdult: Boolean,
        language: String,
        page: Int
    ): Result<List<Content>, TheMovieDbError> {
        try {
            val response = httpClient.get("search/multi") {
                parameter("query", query)
                parameter("include_adult", includeAdult)
                parameter("language", language)
                parameter("page", page)
            }

            if (!response.status.isSuccess()) {
                return Result.Error(TheMovieDbError.HttpError.ResponseReturnError(response.status.description))
            }

            val result = response.body<PageResponse<PolymorphContentResponse>>()

            if (result.results == null) return Result.Error(TheMovieDbError.HttpError.ResponseReturnNull)

            val content = result.results.mapToListContent()

            return Result.Success(content)
        } catch (e: Exception) {
            return Result.Error(TheMovieDbError.Unknown(e.toString()))
        }
    }

    override suspend fun findPerson(
        query: String,
        includeAdult: Boolean,
        language: String,
        page: Int
    ): Result<List<Person>, TheMovieDbError> {
        try {
            val response = httpClient.get("search/person") {
                parameter("query", query)
                parameter("include_adult", includeAdult)
                parameter("language", language)
                parameter("page", page)
            }

            if (!response.status.isSuccess()) {
                return Result.Error(TheMovieDbError.HttpError.ResponseReturnError(response.status.description))
            }

            val result = response.body<PageResponse<PersonResponse>>()

            if (result.results == null) return Result.Error(TheMovieDbError.HttpError.ResponseReturnNull)

            val content = result.results.mapToPersonList()

            return Result.Success(content)
        } catch (e: Exception) {
            return Result.Error(TheMovieDbError.Unknown(e.toString()))
        }
    }

    override suspend fun findTv(
        query: String,
        firstAirDateYear: Int?,
        includeAdult: Boolean,
        language: String,
        page: Int,
        year: Int?
    ): Result<List<TvShow>, TheMovieDbError> {
        try {
            val response = httpClient.get("search/tv") {
                parameter("query", query)
                parameter("first_air_date_year", firstAirDateYear)
                parameter("include_adult", includeAdult)
                parameter("language", language)
                parameter("page", page)
                parameter("year", year)
            }

            if (!response.status.isSuccess()) {
                return Result.Error(TheMovieDbError.HttpError.ResponseReturnError(response.status.description))
            }

            val result = response.body<PageResponse<TvShowResponse>>()

            if (result.results == null) return Result.Error(TheMovieDbError.HttpError.ResponseReturnNull)

            val content = result.results.mapToTvShowList()

            return Result.Success(content)
        } catch (e: Exception) {
            return Result.Error(TheMovieDbError.Unknown(e.toString()))
        }
    }

    override suspend fun findMovie(
        query: String,
        includeAdult: Boolean,
        language: String,
        primaryReleaseYear: String?,
        page: Int,
        region: String?,
        year: Int?
    ): Result<List<Movie>, TheMovieDbError> {
        try {
            val response = httpClient.get("search/movie") {
                parameter("query", query)
                parameter("include_adult", includeAdult)
                parameter("language", language)
                parameter("primary_release_year", primaryReleaseYear)
                parameter("page", page)
                parameter("region", region)
                parameter("year", year)
            }

            if (!response.status.isSuccess()) {
                return Result.Error(TheMovieDbError.HttpError.ResponseReturnError(response.status.description))
            }

            val result = response.body<PageResponse<MovieResponse>>()

            if (result.results == null) return Result.Error(TheMovieDbError.HttpError.ResponseReturnNull)

            val content = result.results.mapToMovieList()

            return Result.Success(content)
        } catch (e: Exception) {
            return Result.Error(TheMovieDbError.Unknown(e.toString()))
        }
    }
}