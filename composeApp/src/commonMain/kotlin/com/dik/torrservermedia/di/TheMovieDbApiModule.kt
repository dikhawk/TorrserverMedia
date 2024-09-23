package com.dik.torrservermedia.di

import com.dik.themoviedb.di.TheMovieDbApi
import com.dik.themoviedb.di.TheMovieDbComponent
import org.koin.dsl.module

val theMovieDbApiModule = module {
    single<TheMovieDbApi> { TheMovieDbComponent.get(get()) }
}