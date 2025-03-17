package com.dik.themoviedb.mapper

import com.dik.themoviedb.MediaType
import com.dik.themoviedb.constants.MOVIEDB_PIC_ORIGINAL_URL
import com.dik.themoviedb.constants.MOVIEDB_PIC_W300_URL
import com.dik.themoviedb.constants.MOVIEDB_PIC_W500_URL
import com.dik.themoviedb.model.Content
import com.dik.themoviedb.model.Movie
import com.dik.themoviedb.model.Person
import com.dik.themoviedb.model.TvEpisode
import com.dik.themoviedb.model.TvSeason
import com.dik.themoviedb.model.TvShow
import com.dik.themoviedb.response.MoviePolymorphResponse
import com.dik.themoviedb.response.MovieResponse
import com.dik.themoviedb.response.PersonPolymorphResponse
import com.dik.themoviedb.response.PersonResponse
import com.dik.themoviedb.response.PolymorphContentResponse
import com.dik.themoviedb.response.TvEpisodeResponse
import com.dik.themoviedb.response.TvSeasonResponse
import com.dik.themoviedb.response.TvShowPolymorphResponse
import com.dik.themoviedb.response.TvShowResponse
import com.dik.themoviedb.toMediaType
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.DateTimeFormat

internal fun MoviePolymorphResponse.mapToMovie(): Movie {
    return Movie(
        backdropPath = MOVIEDB_PIC_W300_URL + this.backdropPath,
        id = this.id ?: 0,
        title = this.title ?: "",
        originalTitle = this.originalTitle ?: "",
        overview = this.overview ?: "",
        poster300 = if (this.posterPath.isNullOrEmpty()) "" else MOVIEDB_PIC_W300_URL + this.posterPath,
        poster500 = if (this.posterPath.isNullOrEmpty()) "" else MOVIEDB_PIC_W500_URL + this.posterPath,
        posterOriginal = if (this.posterPath.isNullOrEmpty()) "" else MOVIEDB_PIC_ORIGINAL_URL + this.posterPath,
        mediaType = this.mediaType.toMediaType(),
        adult = this.adult ?: false,
        originalLanguage = this.originalLanguage ?: "",
        genreIds = this.genreIds ?: emptyList(),
        popularity = this.popularity ?: 0.0,
        releaseDate = this.releaseDate.parseDate(),
        video = this.video ?: false,
        voteAverage = this.voteAverage ?: 0.0,
        voteCount = this.voteCount ?: 0
    )
}

internal fun PersonPolymorphResponse.mapToPerson(): Person {
    return Person(
        id = this.id ?: 0,
        name = this.name ?: "",
        originalName = this.originalName ?: "",
        mediaType = this.mediaType.toMediaType(),
        adult = this.adult ?: false,
        popularity = this.popularity ?: 0.0,
        gender = this.gender ?: -1,
        knownForDepartment = this.knownForDepartment ?: "",
        profilePath = if (this.profilePath.isNullOrEmpty()) "" else MOVIEDB_PIC_W300_URL + this.profilePath
    )
}

internal fun TvShowPolymorphResponse.mapToTvShow(): TvShow {
    return TvShow(
        backdropPath = MOVIEDB_PIC_W300_URL + this.backdropPath,
        id = this.id ?: 0,
        name = this.name ?: "",
        originalName = this.originalName ?: "",
        overview = this.overview ?: "",
        poster300 = if (this.posterPath.isNullOrEmpty()) "" else MOVIEDB_PIC_W300_URL + this.posterPath,
        poster500 = if (this.posterPath.isNullOrEmpty()) "" else MOVIEDB_PIC_W500_URL + this.posterPath,
        posterOriginal = if (this.posterPath.isNullOrEmpty()) "" else MOVIEDB_PIC_ORIGINAL_URL + this.posterPath,
        mediaType = this.mediaType.toMediaType(),
        adult = this.adult ?: false,
        originalLanguage = this.originalLanguage ?: "",
        genreIds = this.genreIds ?: emptyList(),
        popularity = this.popularity ?: 0.0,
        firstAirDate = this.firstAirDate.parseDate(),
        voteAverage = this.voteAverage ?: 0.0,
        voteCount = this.voteCount ?: 0,
        originCountry = this.originCountry ?: emptyList()
    )
}

internal fun List<PolymorphContentResponse>.mapToListContent(): List<Content> {
    return this.map { content ->
        when (content) {
            is MoviePolymorphResponse -> content.mapToMovie()
            is PersonPolymorphResponse -> content.mapToPerson()
            is TvShowPolymorphResponse -> content.mapToTvShow()
            else -> object : Content() {
                override val id: Int = content.id ?: 0
                override val mediaType: MediaType = content.mediaType.toMediaType()
            }
        }
    }
}

internal fun List<MovieResponse>.mapToMovieList(): List<Movie> {
    return this.map { it.mapToMovie() }
}

internal fun MovieResponse.mapToMovie(): Movie {
    return Movie(
        backdropPath = if (this.backdropPath.isNullOrEmpty()) "" else MOVIEDB_PIC_W300_URL + this.backdropPath,
        id = this.id ?: 0,
        title = this.title ?: "",
        originalTitle = this.originalTitle ?: "",
        overview = this.overview ?: "",
        poster300 = if (this.posterPath.isNullOrEmpty()) "" else MOVIEDB_PIC_W300_URL + this.posterPath,
        poster500 = if (this.posterPath.isNullOrEmpty()) "" else MOVIEDB_PIC_W500_URL + this.posterPath,
        posterOriginal = if (this.posterPath.isNullOrEmpty()) "" else MOVIEDB_PIC_ORIGINAL_URL + this.posterPath,
        mediaType = MediaType.Movie,
        adult = this.adult ?: false,
        originalLanguage = this.originalLanguage ?: "",
        genreIds = this.genreIds ?: emptyList(),
        popularity = this.popularity ?: 0.0,
        releaseDate = this.releaseDate.parseDate(),
        video = this.video ?: false,
        voteAverage = this.voteAverage ?: 0.0,
        voteCount = this.voteCount ?: 0
    )
}

internal fun List<PersonResponse>.mapToPersonList(): List<Person> {
    return this.map { it.mapToPerson() }
}

internal fun PersonResponse.mapToPerson(): Person {
    return Person(
        id = this.id ?: 0,
        name = this.name ?: "",
        originalName = this.originalName ?: "",
        mediaType = MediaType.Person,
        adult = this.adult ?: false,
        popularity = this.popularity ?: 0.0,
        gender = this.gender ?: -1,
        knownForDepartment = this.knownForDepartment ?: "",
        profilePath = if (this.profilePath.isNullOrEmpty()) "" else MOVIEDB_PIC_W300_URL + this.profilePath
    )
}

internal fun List<TvShowResponse>.mapToTvShowList(): List<TvShow> {
    return this.map { it.mapToTvShow() }
}

internal fun TvShowResponse.mapToTvShow(): TvShow {
    return TvShow(
        backdropPath = if (this.backdropPath.isNullOrEmpty()) "" else MOVIEDB_PIC_W300_URL + this.backdropPath,
        id = this.id ?: 0,
        name = this.name ?: "",
        originalName = this.originalName ?: "",
        overview = this.overview ?: "",
        poster300 = if (this.posterPath.isNullOrEmpty()) "" else MOVIEDB_PIC_W300_URL + this.posterPath,
        poster500 = if (this.posterPath.isNullOrEmpty()) "" else MOVIEDB_PIC_W500_URL + this.posterPath,
        posterOriginal = if (this.posterPath.isNullOrEmpty()) "" else MOVIEDB_PIC_ORIGINAL_URL + this.posterPath,
        mediaType = MediaType.TV,
        adult = this.adult ?: false,
        originalLanguage = this.originalLanguage ?: "",
        genreIds = this.genreIds ?: emptyList(),
        popularity = this.popularity ?: 0.0,
        firstAirDate = this.firstAirDate.parseDate(),
        voteAverage = this.voteAverage ?: 0.0,
        voteCount = this.voteCount ?: 0,
        originCountry = this.originCountry ?: emptyList()
    )
}

internal fun TvSeasonResponse.mapToTvSeason(): TvSeason {
    return TvSeason(
        id = this.id,
        name = this.name,
        airDate = this.airDate,
        episodeCount = this.episodeCount,
        overview = this.overview,
        poster300 = if (this.posterPath.isNullOrEmpty()) "" else MOVIEDB_PIC_W300_URL + this.posterPath,
        poster500 = if (this.posterPath.isNullOrEmpty()) "" else MOVIEDB_PIC_W500_URL + this.posterPath,
        posterOriginal = if (this.posterPath.isNullOrEmpty()) "" else MOVIEDB_PIC_ORIGINAL_URL + this.posterPath,
        seasonNumber = this.seasonNumber,
        voteAverage = this.voteAverage,
        episodes = this.episodes?.mapToListTvEpisode() ?: emptyList()
    )
}

internal fun List<TvEpisodeResponse>.mapToListTvEpisode(): List<TvEpisode> {
    return map { it.mapToTvEpisode() }
}

internal fun TvEpisodeResponse.mapToTvEpisode(): TvEpisode {
    return TvEpisode(
        id = id ?: 0,
        name = name ?: "",
        airDate = airDate.parseDate(),
        episodeNumber = episodeNumber ?: 0,
        overview = overview ?: "",
        productionCode = productionCode ?: "",
        runtime = runtime ?: 0,
        seasonNumber = seasonNumber ?: 0,
        stillPath = stillPath ?: "",
        voteAverage = voteAverage ?: 0.0,
        voteCount = voteCount ?: 0
    )
}

private val defaultDateFormat = LocalDate.Format { date(LocalDate.Formats.ISO) }

private fun String?.parseDate(format: DateTimeFormat<LocalDate> = defaultDateFormat): LocalDate {
    if (this.isNullOrEmpty()) return defaultDateFormat.parse("0000-01-01")

    return format.parse(this)
}