package com.dik.torrservermedia.di

import com.dik.common.i18n.ComposeLocalizationResource
import com.dik.common.i18n.LocalizationResource
import org.koin.dsl.module

internal val commonModule = module {
    single<LocalizationResource> { ComposeLocalizationResource() }
}