package com.dik.themoviedb.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TvSeasonResponse(
    @SerialName("id")  val id: Int,
    @SerialName("name") val name: String,
    @SerialName("air_date") val airDate: String?,
    @SerialName("episode_count") val episodeCount: Int? = null,
    @SerialName("overview") val overview: String? = null,
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("season_number") val seasonNumber: Int? = null,
    @SerialName("vote_average") val voteAverage: Double,
    @SerialName("episodes") val episodes: List<TvEpisodeResponse>? = emptyList()
)
