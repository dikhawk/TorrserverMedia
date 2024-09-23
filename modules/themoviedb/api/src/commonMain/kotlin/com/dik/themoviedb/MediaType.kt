package com.dik.themoviedb

enum class MediaType(val text: String) {
    TV("tv"),
    Movie("movie"),
    Person("person"),
    NotDefined("not_defined")
}

fun String?.toMediaType(): MediaType {
    return when (this) {
        MediaType.TV.text -> MediaType.TV
        MediaType.Movie.text -> MediaType.Movie
        MediaType.Person.text -> MediaType.Person
        else -> MediaType.NotDefined
    }
}