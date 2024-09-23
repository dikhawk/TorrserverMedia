package com.dik.themoviedb.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TvEpisodeResponse(
    @SerialName("id") val id: Int?,
    @SerialName("name") val name: String?,
    @SerialName("air_date") val airDate: String?,
    @SerialName("episode_number") val episodeNumber: Int?,
    @SerialName("overview") val overview: String?,
    @SerialName("production_code") val productionCode: String?,
    @SerialName("runtime") val runtime: Int?,
    @SerialName("season_number") val seasonNumber: Int?,
    @SerialName("still_path") val stillPath: String?,
    @SerialName("vote_average") val voteAverage: Double?,
    @SerialName("vote_count") val voteCount: Int?
)