package com.dik.themoviedb.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("movie")
internal data class MoviePolymorphResponse(
    @SerialName("backdrop_path") val backdropPath: String?,
    @SerialName("id") override val id: Int?,
    @SerialName("title") val title: String?,
    @SerialName("original_title") val originalTitle: String?,
    @SerialName("overview") val overview: String?,
    @SerialName("poster_path") val posterPath: String?,
    @SerialName("media_type") override val mediaType: String?,
    @SerialName("adult") val adult: Boolean?,
    @SerialName("original_language") val originalLanguage: String?,
    @SerialName("genre_ids") val genreIds: List<Int>?,
    @SerialName("popularity") val popularity: Double?,
    @SerialName("release_date") val releaseDate: String?,
    @SerialName("video") val video: Boolean?,
    @SerialName("vote_average") val voteAverage: Double?,
    @SerialName("vote_count") val voteCount: Int?
) : PolymorphContentResponse()
