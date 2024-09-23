package com.dik.themoviedb.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class PageResponse<ResponseType>(
    @SerialName("page") val page: Int? = 0,
    @SerialName("results") val results: List<ResponseType>? = emptyList(),
    @SerialName("total_pages") val totalPages: Int? = 0,
    @SerialName("total_results") val totalResults: Int? = 0,
)