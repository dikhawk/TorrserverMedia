package com.dik.themoviedb

import com.dik.themoviedb.model.Content
import com.dik.themoviedb.model.Movie
import com.dik.themoviedb.model.Person
import com.dik.themoviedb.model.TvShow
import com.dik.common.Result
import com.dik.themoviedb.errors.TheMovieDbError

interface SearchTheMovieDbApi {

    suspend fun multiSearching(
        query: String,
        includeAdult: Boolean = false,
        language: String = "ru-Ru",
        page: Int = 1
    ): Result<List<Content>, TheMovieDbError>

    suspend fun findPerson(
        query: String,
        includeAdult: Boolean = false,
        language: String = "ru-Ru",
        page: Int = 1
    ): Result<List<Person>, TheMovieDbError>

    suspend fun findTv(
        query: String,
        firstAirDateYear: Int? = null,
        includeAdult: Boolean = false,
        language: String = "ru-Ru",
        page: Int = 1,
        year: Int? = null
    ): Result<List<TvShow>, TheMovieDbError>

    suspend fun findMovie(
        query: String,
        includeAdult: Boolean = false,
        language: String = "ru-Ru",
        primaryReleaseYear: String? = null,
        page: Int = 1,
        region: String? = null,
        year: Int? = null
    ): Result<List<Movie>, TheMovieDbError>
}