package com.dik.common.i18n

import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.flow.MutableStateFlow


enum class AppLanguage(val iso: String, val title: String) {
    ENGLISH("en", "English"),
    RUSSIAN("ru", "Русский"),
}

fun String.toAppLanguage(): AppLanguage {
    return AppLanguage.entries.find { it.iso == this } ?: AppLanguage.ENGLISH
}

val LocalAppLanguage = staticCompositionLocalOf<AppLanguage> { error("AppLanguage not provided") }

expect suspend fun setLocalization(lang: AppLanguage)

val currentLanguageFlow = MutableStateFlow(AppLanguage.ENGLISH)