package com.dik.themoviedb.model

import com.dik.themoviedb.MediaType
import kotlinx.datetime.LocalDate

data class Movie(
    val backdropPath: String,
    override val id: Int,
    val title: String,
    val originalTitle: String,
    val overview: String,
    val poster300: String?,
    val poster500: String?,
    val posterOriginal: String?,
    override val mediaType: MediaType,
    val adult: Boolean,
    val originalLanguage: String,
    val genreIds: List<Int>,
    val popularity: Double,
    val releaseDate: LocalDate,
    val video: Boolean,
    val voteAverage: Double,
    val voteCount: Int
) : Content()