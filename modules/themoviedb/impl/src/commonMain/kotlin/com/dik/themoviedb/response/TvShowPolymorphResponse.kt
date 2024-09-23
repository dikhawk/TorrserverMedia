package com.dik.themoviedb.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("tv")
internal data class TvShowPolymorphResponse(
    @SerialName("backdrop_path") val backdropPath: String?,
    @SerialName("id") override val id: Int?,
    @SerialName("name") val name: String?,
    @SerialName("original_name") val originalName: String?,
    @SerialName("overview") val overview: String?,
    @SerialName("poster_path") val posterPath: String?,
    @SerialName("media_type") override val mediaType: String?,
    @SerialName("adult") val adult: Boolean?,
    @SerialName("original_language") val originalLanguage: String?,
    @SerialName("genre_ids") val genreIds: List<Int>?,
    @SerialName("popularity") val popularity: Double?,
    @SerialName("first_air_date") val firstAirDate: String?,
    @SerialName("vote_average") val voteAverage: Double?,
    @SerialName("vote_count") val voteCount: Int?,
    @SerialName("origin_country") val originCountry: List<String>?
) : PolymorphContentResponse()