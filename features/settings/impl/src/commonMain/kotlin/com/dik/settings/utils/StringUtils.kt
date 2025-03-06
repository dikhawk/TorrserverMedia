package com.dik.settings.utils

internal fun String.toIntOrZero(): Int = if (this.isEmpty()) 0 else this.toInt()

internal fun String.toLongOrZero(): Long = if (this.isEmpty()) 0L else this.toLong()