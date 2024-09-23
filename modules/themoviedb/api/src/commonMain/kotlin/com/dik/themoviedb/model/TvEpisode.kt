package com.dik.themoviedb.model

import kotlinx.datetime.LocalDate


data class TvEpisode(
    val id: Int,
    val name: String,
    val airDate: LocalDate,
    val episodeNumber: Int,
    val overview: String,
    val productionCode: String,
    val runtime: Int,
    val seasonNumber: Int,
    val stillPath: String,
    val voteAverage: Double,
    val voteCount: Int
)
