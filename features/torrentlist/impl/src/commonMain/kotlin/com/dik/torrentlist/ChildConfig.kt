package com.dik.torrentlist

import kotlinx.serialization.Serializable

@Serializable
sealed interface ChildConfig {

    @Serializable
    object List : ChildConfig

    @Serializable
    data class Details(val torrentUrl: String) : ChildConfig
}