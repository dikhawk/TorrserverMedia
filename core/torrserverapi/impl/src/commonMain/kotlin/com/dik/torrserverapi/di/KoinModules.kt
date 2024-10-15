package com.dik.torrserverapi.di

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.core.Koin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.koinApplication

object KoinModules {
    private val mutex = Mutex()

    var koin: Koin? = null
        private set

    fun init(dependencies: TorrserverDependencies) {
        if (koin == null) {
            runBlocking {
                mutex.withLock {
                    koin = koinApplication {
                        koinConfiguration(dependencies).invoke(this)
                        modules(torrserverModule, httpModule, dependencyModule(dependencies))
                    }.koin
                }
            }
        }
    }
}

internal expect fun koinConfiguration(dependencies: TorrserverDependencies): KoinAppDeclaration

internal inline fun <reified T> inject(): T {
    return KoinModules.koin!!.get()
}