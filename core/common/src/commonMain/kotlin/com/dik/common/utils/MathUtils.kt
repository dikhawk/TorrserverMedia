package com.dik.common.utils

import kotlin.math.pow
import kotlin.math.roundToInt


fun Double.round(scaleFactor: Int): Double {
    val factor = 10.0.pow(scaleFactor)

    return (this * factor).roundToInt() / factor
}