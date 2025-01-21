package com.dik.common.i18n

import kotlinx.coroutines.flow.update
import java.util.Locale

actual suspend fun setLocalization(lang: AppLanguage) {
    Locale.setDefault(Locale.forLanguageTag(lang.iso))
    currentLanguageFlow.update { lang }
}