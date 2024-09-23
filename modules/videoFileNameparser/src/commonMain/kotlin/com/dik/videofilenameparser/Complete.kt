package com.dik.videofilenameparser


private val completeDvdExp = "\\b(NTSC|PAL)?.DVDR\\b".toRegex(RegexOption.IGNORE_CASE)

fun isCompleteDvd(title: String): Boolean? {
    return if (completeDvdExp.containsMatchIn(title)) true else null
}


private val completeExp = "\\b(COMPLETE)\\b".toRegex(RegexOption.IGNORE_CASE)

fun isComplete(title: String): Boolean? {
    return if (completeExp.containsMatchIn(title) || isCompleteDvd(title) == true) true else null
}
