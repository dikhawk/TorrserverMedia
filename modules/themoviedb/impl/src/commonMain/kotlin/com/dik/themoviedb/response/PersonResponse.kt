package com.dik.themoviedb.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class PersonResponse(
    @SerialName("id") val id: Int?,
    @SerialName("name") val name: String?,
    @SerialName("original_name") val originalName: String?,
    @SerialName("adult") val adult: Boolean?,
    @SerialName("popularity") val popularity: Double?,
    @SerialName("gender") val gender: Int?,
    @SerialName("known_for_department") val knownForDepartment: String?,
    @SerialName("profile_path") val profilePath: String?,
    @SerialName("known_for") val knownFor: List<PolymorphContentResponse>?,
)