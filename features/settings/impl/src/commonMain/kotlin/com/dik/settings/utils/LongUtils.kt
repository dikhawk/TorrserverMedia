package com.dik.settings.utils

internal fun Long.mbToBytes(): Long = this * 1024L * 1024L

internal fun Long.bytesToMb(): Long = this / 1024L / 1024L