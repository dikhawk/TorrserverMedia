package com.dik.torrserverapi.domain.filemanager

interface FileManager {
    suspend fun exists(path: String): Boolean
    suspend fun copy(source: String, target: String)
    suspend fun delete(path: String)
}