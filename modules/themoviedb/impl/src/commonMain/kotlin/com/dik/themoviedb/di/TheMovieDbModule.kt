package com.dik.themoviedb.di

import com.dik.themoviedb.MoviesTheMovieDbApi
import com.dik.themoviedb.MoviesTheMovieDbApiImpl
import com.dik.themoviedb.SearchTheMovieDbApi
import com.dik.themoviedb.SearchTheMovieDbApiImpl
import com.dik.themoviedb.TvEpisodesTheMovieDbApi
import com.dik.themoviedb.TvEpisodesTheMovieDbApiImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val theMovieDbModule = module {
    singleOf(::SearchTheMovieDbApiImpl).bind<SearchTheMovieDbApi>()
    singleOf(::MoviesTheMovieDbApiImpl).bind<MoviesTheMovieDbApi>()
    singleOf(::TvEpisodesTheMovieDbApiImpl).bind<TvEpisodesTheMovieDbApi>()
}