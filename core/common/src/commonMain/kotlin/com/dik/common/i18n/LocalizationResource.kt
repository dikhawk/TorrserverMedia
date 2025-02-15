package com.dik.common.i18n

import org.jetbrains.compose.resources.StringResource

interface LocalizationResource {

    suspend fun getString(strRes: StringResource): String
}