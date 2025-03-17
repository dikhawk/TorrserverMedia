package com.dik.torrserverapi.di

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.koinApplication

object KoinModules {

    private val mutex = Mutex()

    @Volatile
    var koin: Koin? = null
        private set

    fun init(dependencies: TorrserverDependencies) {
        if (koin != null) return

        runBlocking {
            mutex.withLock {
                if (koin == null) {
                    koin = koinApplication {
                        koinConfiguration(dependencies).invoke(this)
                        torrserverModules(dependencies)
                    }.koin
                }
            }
        }
    }

    private fun KoinApplication.torrserverModules(dependencies: TorrserverDependencies) {
        modules(
            torrserverModule,
            httpModule,
            dependencyModule(dependencies),
            platformModule()
        )
    }
}

internal expect fun koinConfiguration(dependencies: TorrserverDependencies): KoinAppDeclaration

internal inline fun <reified T> inject(): T {
    return KoinModules.koin!!.get()
}