package com.dik.torrentlist.screens.navigation

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