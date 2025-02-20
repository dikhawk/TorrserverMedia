package com.dik.torrentlist.utils

import io.github.vinceglb.filekit.core.PlatformFile

internal actual fun PlatformFile.toPath(): String? = uri.toString()