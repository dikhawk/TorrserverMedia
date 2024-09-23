package com.dik.themoviedb.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class MovieDetailsResponse(
    @SerialName("id") val id: Int,
    @SerialName("adult") val adult: Boolean,
    @SerialName("backdrop_path") val backdropPath: String,
    @SerialName("belongs_to_collection") val belongsToCollection: MovieCollectionResponse?,
    @SerialName("budget") val budget: Int,
    @SerialName("genres") val genres: List<GenreResponse>,
    @SerialName("homepage") val homepage: String,
    @SerialName("imdb_id") val imdbId: String,
    @SerialName("origin_country") val originCountry: List<String>,
    @SerialName("original_language") val originalLanguage: String,
    @SerialName("original_title") val originalTitle: String,
    @SerialName("overview") val overview: String,
    @SerialName("popularity") val popularity: Double,
    @SerialName("poster_path") val posterPath: String,
    @SerialName("production_companies") val productionCompanies: List<CompanyResponse>,
    @SerialName("production_countries") val productionCountries: List<CountryResponse>,
    @SerialName("release_date") val releaseDate: String,
    @SerialName("revenue") val revenue: Long,
    @SerialName("runtime") val runtime: Int,
    @SerialName("spoken_languages") val spokenLanguages: List<SpokenLanguageResponse>,
    @SerialName("status") val status: String,
    @SerialName("tagline") val tagline: String,
    @SerialName("title") val title: String,
    @SerialName("video") val video: Boolean,
    @SerialName("vote_average") val voteAverage: Double,
    @SerialName("vote_count") val voteCount: Int
)
