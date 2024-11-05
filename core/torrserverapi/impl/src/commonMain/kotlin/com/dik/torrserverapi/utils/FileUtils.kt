package com.dik.torrserverapi.utils

import okio.Path.Companion.toPath

fun fileToByteArray(fileToPath: String): ByteArray = fileToPath.toPath().nameBytes.toByteArray()