package com.dik.settings.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface ChildConfig {
    @Serializable
    object Main : ChildConfig
}
