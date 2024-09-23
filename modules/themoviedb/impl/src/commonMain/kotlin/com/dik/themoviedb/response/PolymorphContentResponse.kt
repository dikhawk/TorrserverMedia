package com.dik.themoviedb.response

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@OptIn(ExperimentalSerializationApi::class)
@JsonClassDiscriminator("media_type")
@Serializable
internal abstract class PolymorphContentResponse {
    @SerialName("id") abstract val id: Int?
    @SerialName("media_type") abstract val mediaType: String?
}