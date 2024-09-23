package com.dik.themoviedb.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class TvShowDetailsResponse(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("adult") val adult: Boolean,
    @SerialName("backdrop_path") val backdropPath: String,
    @SerialName("created_by") val createdBy: List<PersonPolymorphResponse>,
    @SerialName("episode_run_time") val episodeRunTime: List<Int>,
    @SerialName("first_air_date") val firstAirDate: String,
    @SerialName("genres") val genres: List<GenreResponse>,
    @SerialName("homepage") val homepage: String,
    @SerialName("in_production") val inProduction: Boolean,
    @SerialName("languages") val languages: List<String>,
    @SerialName("last_air_date") val lastAirDate: String,
    @SerialName("last_episode_to_air") val lastEpisodeToAir: TvEpisodeResponse,
    @SerialName("next_episode_to_air") val nextEpisodeToAir: TvEpisodeResponse?,
    @SerialName("networks") val networks: List<NetworkResponse>,
    @SerialName("number_of_episodes") val numberOfEpisodes: Int,
    @SerialName("number_of_seasons") val numberOfSeasons: Int,
    @SerialName("origin_country") val originCountry: List<String>,
    @SerialName("original_language") val originalLanguage: String,
    @SerialName("original_name") val originalName: String,
    @SerialName("overview") val overview: String,
    @SerialName("popularity") val popularity: Double,
    @SerialName("poster_path") val posterPath: String,
    @SerialName("production_companies") val productionCompanies: List<CompanyResponse>,
    @SerialName("production_countries") val productionCountries: List<CountryResponse>,
    @SerialName("seasons") val seasons: List<SeasonResponse>,
    @SerialName("spoken_languages") val spokenLanguages: List<SpokenLanguageResponse>,
    @SerialName("status") val status: String,
    @SerialName("tagline") val tagline: String,
    @SerialName("type") val type: String,
    @SerialName("vote_average") val voteAverage: Double,
    @SerialName("vote_count") val voteCount: Int
)
