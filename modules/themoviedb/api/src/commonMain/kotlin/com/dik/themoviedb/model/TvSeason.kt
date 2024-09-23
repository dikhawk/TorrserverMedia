package com.dik.themoviedb.model

data class TvSeason(
    val id: Int,
    val name: String,
    val airDate: String?,
    val episodeCount: Int,
    val overview: String,
    val posterPath: String?,
    val seasonNumber: Int,
    val voteAverage: Double
)
