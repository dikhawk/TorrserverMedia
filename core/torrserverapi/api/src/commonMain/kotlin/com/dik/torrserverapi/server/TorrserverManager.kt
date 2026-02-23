package com.dik.torrserverapi.server

import kotlinx.coroutines.flow.Flow

interface TorrserverManager {

    fun observeTorrserverStatus(): Flow<TorrserverStatus>
    fun installOrUpdate(): Flow<TorrserverStatus>
    fun start(): Flow<TorrserverStatus>
    fun stop(): Flow<TorrserverStatus>
    fun restart(): Flow<TorrserverStatus>
    fun checkNewVersion(): Flow<TorrserverStatus>
}