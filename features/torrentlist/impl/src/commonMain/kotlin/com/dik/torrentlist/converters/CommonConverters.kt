package com.dik.torrentlist.converters

import com.dik.common.utils.round

fun Double.bytesToBits(): String {
    val kilobits = this * 8.0 / 1024.0
    val megabits = kilobits / 1024.0
    val gigabits = megabits / 1024.0

    return when {
        gigabits >= 1.0 -> "${gigabits.round(2)} Gb/s"
        megabits >= 1.0 -> "${megabits.round(2)} Mb/s"
        else -> "${kilobits.round(2)} Kb/s"
    }
}

fun Long.toReadableSize(): String {
    val kilobytes = this / 1024.0
    val megabytes = kilobytes / 1024.0
    val gigabytes = megabytes / 1024.0
    val terabytes = gigabytes / 1024.0

    return when {
        terabytes >= 1 -> "${terabytes.round(2)} TB"
        gigabytes >= 1 -> "${gigabytes.round(2)} GB"
        megabytes >= 1 -> "${megabytes.round(2)} MB"
        else -> "${kilobytes.round(2)} KB"
    }
}