package com.dik.common

import kotlinx.coroutines.CoroutineDispatcher

interface AppDispatchers {

    fun ioDispatcher(): CoroutineDispatcher
    fun defaultDispatcher(): CoroutineDispatcher
    fun mainDispatcher(): CoroutineDispatcher
}