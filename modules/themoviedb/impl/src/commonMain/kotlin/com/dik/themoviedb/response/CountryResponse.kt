package com.dik.themoviedb.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CountryResponse(
    @SerialName("iso_3166_1") val iso3166_1: String,
    @SerialName("name") val name: String
)
