package com.dik.themoviedb.model

import kotlinx.datetime.LocalDate

data class MovieDetails(
    val adult: Boolean,
    val backdropPath: String,
    val belongsToCollection: MovieCollection?,
    val budget: Int,
    val genres: List<Genre>,
    val homepage: String,
    val id: Int,
    val imdbId: String,
    val originCountry: List<String>,
    val originalLanguage: String,
    val originalTitle: String,
    val overview: String,
    val popularity: Double,
    val posterPath: String,
    val productionCompanies: List<Company>,
    val productionCountries: List<Country>,
    val releaseDate: LocalDate,
    val revenue: Long,
    val runtime: Int,
    val spokenLanguages: List<SpokenLanguage>,
    val status: String,
    val tagline: String,
    val title: String,
    val video: Boolean,
    val voteAverage: Double,
    val voteCount: Int
)
