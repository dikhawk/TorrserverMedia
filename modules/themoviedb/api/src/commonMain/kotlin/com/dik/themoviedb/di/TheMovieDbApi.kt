package com.dik.themoviedb.di

import com.dik.moduleinjector.BaseApi
import com.dik.themoviedb.MoviesTheMovieDbApi
import com.dik.themoviedb.SearchTheMovieDbApi
import com.dik.themoviedb.TvEpisodesTheMovieDbApi

interface TheMovieDbApi: BaseApi {
    fun searchApi(): SearchTheMovieDbApi
    fun movieApi(): MoviesTheMovieDbApi
    fun tvEpisodesApi(): TvEpisodesTheMovieDbApi
}