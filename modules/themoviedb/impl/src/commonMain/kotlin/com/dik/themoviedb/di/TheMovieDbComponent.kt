package com.dik.themoviedb.di

import com.dik.common.AppDispatchers
import com.dik.themoviedb.MoviesTheMovieDbApi
import com.dik.themoviedb.SearchTheMovieDbApi
import com.dik.themoviedb.TvEpisodesTheMovieDbApi
import com.dik.themoviedb.TvSeasonsTheMovieDbApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

abstract class TheMovieDbComponent: TheMovieDbApi {
    companion object {
        private var component: TheMovieDbComponent? = null
        private val mutex = Mutex()

        fun get(appDispatchers: AppDispatchers): TheMovieDbComponent {
            if (component == null) {
                runBlocking {
                    mutex.withLock {
                        if (component == null) {
                            component = object : TheMovieDbComponent() {
                                init {
                                    KoinModules.init(appDispatchers)
                                }

                                override fun searchApi(): SearchTheMovieDbApi = inject()
                                override fun movieApi(): MoviesTheMovieDbApi = inject()
                                override fun tvEpisodesApi(): TvEpisodesTheMovieDbApi = inject()
                                override fun tvSeasonsApi(): TvSeasonsTheMovieDbApi = inject()
                            }
                        }
                    }
                }
            }

            return component!!
        }
    }
}