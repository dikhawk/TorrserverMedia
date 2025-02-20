package com.dik.torrentlist.utils

internal interface FileUtils {
    suspend fun uriToPath(uri: String): String
    suspend fun isFileExist(path: String): Boolean
    suspend fun absolutPath(path: String): String
}