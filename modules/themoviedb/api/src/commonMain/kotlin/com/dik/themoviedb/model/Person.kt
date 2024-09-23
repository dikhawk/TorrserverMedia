package com.dik.themoviedb.model

import com.dik.themoviedb.MediaType

data class Person(
    override val id: Int,
    val name: String,
    val originalName: String,
    override val mediaType: MediaType,
    val adult: Boolean,
    val popularity: Double,
    val gender: Int,
    val knownForDepartment: String,
    val profilePath: String?
) : Content()