package com.dik.themoviedb.model

import kotlinx.datetime.LocalDate

data class TvShowDetails(
    val adult: Boolean,
    val backdropPath: String,
    val createdBy: List<Person>,
    val episodeRunTime: List<Int>,
    val firstAirDate: LocalDate,
    val genres: List<Genre>,
    val homepage: String,
    val id: Int,
    val inProduction: Boolean,
    val languages: List<String>,
    val lastAirDate: LocalDate,
    val lastEpisodeToAir: TvEpisode?,
    val name: String,
    val nextEpisodeToAir: TvEpisode?,
    val networks: List<Network>,
    val numberOfEpisodes: Int,
    val numberOfSeasons: Int,
    val originCountry: List<String>,
    val originalLanguage: String,
    val originalName: String,
    val overview: String,
    val popularity: Double,
    val posterPath: String,
    val productionCompanies: List<Company>,
    val productionCountries: List<Country>,
    val seasons: List<TvSeason>,
    val spokenLanguages: List<SpokenLanguage>,
    val status: String,
    val tagline: String,
    val type: String,
    val voteAverage: Double,
    val voteCount: Int
)
