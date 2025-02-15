package com.dik.common.i18n

import org.jetbrains.compose.resources.StringResource

class ComposeLocalizationResource: LocalizationResource {

    override suspend fun getString(resource: StringResource): String {
        return org.jetbrains.compose.resources.getString(resource)
    }
}