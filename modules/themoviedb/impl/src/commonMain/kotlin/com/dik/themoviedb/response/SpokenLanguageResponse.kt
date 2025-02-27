package com.dik.themoviedb.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SpokenLanguageResponse(
    @SerialName("english_name") val englishName: String,
    @SerialName("iso_639_1") val iso639_1: String,
    @SerialName("name") val name: String
)
