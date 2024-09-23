package com.dik.videofilenameparser

private val fileExtensions = listOf(
    // Unknown
    ".webm",
    // SDTV
    ".m4v", ".3gp", ".nsv", ".ty", ".strm", ".rm", ".rmvb", ".m3u", ".ifo", ".mov", ".qt",
    ".divx", ".xvid", ".bivx", ".nrg", ".pva", ".wmv", ".asf", ".asx", ".ogm", ".ogv", ".m2v",
    ".avi", ".bin", ".dat", ".dvr-ms", ".mpg", ".mpeg", ".mp4", ".avc", ".vp3", ".svq3", ".nuv",
    ".viv", ".dv", ".fli", ".flv", ".wpl",

    // DVD
    ".img", ".iso", ".vob",

    // HD
    ".mkv", ".mk3d", ".ts", ".wtv",

    // Bluray
    ".m2ts"
)

private val fileExtensionExp = Regex("\\.[a-z0-9]{2,4}$", RegexOption.IGNORE_CASE)

fun removeFileExtension(title: String): String {
    return fileExtensionExp.replace(title) { matchResult ->
        val matchedExtension = matchResult.value
        if (fileExtensions.contains(matchedExtension)) {
            ""
        } else {
            matchedExtension
        }
    }
}