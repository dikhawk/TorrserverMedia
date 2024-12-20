package com.dik.torrentlist.screens.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface ChildConfig {

    @Serializable
    data object Main : ChildConfig

    @Serializable
    data class Details(val torrentHash: String, val poster: String) : ChildConfig

    @Serializable
    data object Settings : ChildConfig
}