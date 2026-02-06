package com.dik.torrserverapi.server

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface TorrserverManager {

    fun observeTorrserverStatus(): StateFlow<TorrserverStatus>
    fun installOrUpdate(): Flow<TorrserverStatus>
    fun start(): Flow<TorrserverStatus>
    fun stop(): Flow<TorrserverStatus>
    fun restart(): Flow<TorrserverStatus>
    fun checkNewVersion(): Flow<TorrserverStatus>
}