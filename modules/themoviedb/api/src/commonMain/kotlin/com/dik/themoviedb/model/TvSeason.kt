package com.dik.themoviedb.model

data class TvSeason(
    val id: Int,
    val name: String,
    val airDate: String?,
    val episodeCount: Int,
    val overview: String,
    val poster300: String?,
    val poster500: String?,
    val posterOriginal: String?,
    val seasonNumber: Int,
    val voteAverage: Double,
    val episodes: List<TvEpisode>
)
