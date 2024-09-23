package com.dik.themoviedb.model

import com.dik.themoviedb.MediaType
import kotlinx.datetime.LocalDate

data class TvShow(
    val backdropPath: String?,
    override val id: Int,
    val name: String,
    val originalName: String,
    val overview: String?,
    val posterPath: String?,
    override val mediaType: MediaType,
    val adult: Boolean,
    val originalLanguage: String,
    val genreIds: List<Int>,
    val popularity: Double,
    val firstAirDate: LocalDate,
    val voteAverage: Double,
    val voteCount: Int,
    val originCountry: List<String>
) : Content()