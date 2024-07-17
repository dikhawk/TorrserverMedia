package com.dik.torrentlist

import kotlinx.serialization.Serializable

@Serializable
sealed interface ChildConfig {

    @Serializable
    object Main : ChildConfig

    @Serializable
    data class Details(val torrentUrl: String) : ChildConfig

    @Serializable
    object Settings : ChildConfig
}