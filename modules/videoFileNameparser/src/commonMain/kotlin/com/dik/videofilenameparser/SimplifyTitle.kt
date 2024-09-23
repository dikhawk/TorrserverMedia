package com.dik.videofilenameparser

val simpleTitleRegex = Regex("\\s*(?:480[ip]|576[ip]|720[ip]|1080[ip]|2160[ip]|HVEC|[xh][\\W_]?26[45]|DD\\W?5\\W1|[<>?*:|]|848x480|1280x720|1920x1080)((8|10)b(it))?", RegexOption.IGNORE_CASE)
val websitePrefixRegex = Regex("^\\[\\s*[a-z]+(\\.[a-z]+)+\\s*][- ]*|^www\\.[a-z]+\\.(?:com|net)[ -]*", RegexOption.IGNORE_CASE)
val cleanTorrentPrefixRegex = Regex("^\\[(?:REQ)\\]", RegexOption.IGNORE_CASE)
val cleanTorrentSuffixRegex = Regex("\\[(?:ettv|rartv|rarbg|cttv)\\]$", RegexOption.IGNORE_CASE)
val commonSourcesRegex = Regex("\\b(Bluray|(dvdr?|BD)rip|HDTV|HDRip|TS|R5|CAM|SCR|(WEB|DVD)?.?SCREENER|DiVX|xvid|web-?dl)\\b", RegexOption.IGNORE_CASE)
val requestInfoRegex = Regex("\\[.+?\\]", RegexOption.IGNORE_CASE)
val editionExp = Regex("\\b((Extended.|Ultimate.)?(Director.?s|Collector.?s|Theatrical|Anniversary|The.Uncut|DC|Ultimate|Final(?=(.(Cut|Edition|Version)))|Extended|Special|Despecialized|unrated|\\d{2,3}(th)?.Anniversary)(.(Cut|Edition|Version))?(.(Extended|Uncensored|Remastered|Unrated|Uncut|IMAX|Fan.?Edit))?|((Uncensored|Remastered|Unrated|Uncut|IMAX|Fan.?Edit|Edition|Restored|((2|3|4)in1)))){1,3}", RegexOption.IGNORE_CASE)
val languageExp = Regex("\\b(TRUE.?FRENCH|videomann|SUBFRENCH|PLDUB|MULTI)", RegexOption.IGNORE_CASE)
val sceneGarbageExp = Regex("\\b(PROPER|REAL|READ.NFO)", RegexOption.IGNORE_CASE)

fun simplifyTitle(title: String): String {
    var simpleTitle = title.replace(simpleTitleRegex, "")
    simpleTitle = simpleTitle.replace(websitePrefixRegex, "")
    simpleTitle = simpleTitle.replace(cleanTorrentPrefixRegex, "")
    simpleTitle = simpleTitle.replace(cleanTorrentSuffixRegex, "")
    simpleTitle = simpleTitle.replace(Regex(commonSourcesRegex.pattern, RegexOption.IGNORE_CASE), "")
    simpleTitle = simpleTitle.replace(webdlExp, "")

    val videoCodec1 = parseVideoCodec(simpleTitle).source
    if (videoCodec1.isNotEmpty()) {
        simpleTitle = simpleTitle.replace(videoCodec1, "")
    }

    val videoCodec2 = parseVideoCodec(simpleTitle).source
    if (videoCodec2.isNotEmpty()) {
        simpleTitle = simpleTitle.replace(videoCodec2, "")
    }

    return simpleTitle.trim()
}

fun releaseTitleCleaner(title: String?): String? {
    if (title.isNullOrEmpty() || title == "(") {
        return null
    }

    var trimmedTitle = title.replace('_', ' ')
    trimmedTitle = trimmedTitle.replace(requestInfoRegex, "").trim()
    trimmedTitle = trimmedTitle.replace(Regex(commonSourcesRegex.pattern, RegexOption.IGNORE_CASE), "").trim()
    trimmedTitle = trimmedTitle.replace(webdlExp, "").trim()
    trimmedTitle = trimmedTitle.replace(editionExp, "").trim()
    trimmedTitle = trimmedTitle.replace(languageExp, "").trim()
    trimmedTitle = trimmedTitle.replace(Regex(sceneGarbageExp.pattern, RegexOption.IGNORE_CASE), "").trim()

    for (lang in Language.entries) {
        trimmedTitle = trimmedTitle.replace(Regex("\\b${lang.name.uppercase()}"), "").trim()
    }

    // Проверяем на наличие лишних пробелов
    trimmedTitle = trimmedTitle.split("  ")[0]
    trimmedTitle = trimmedTitle.split("..")[0]

    val parts = trimmedTitle.split('.')
    var result = ""
    var n = 0
    var previousAcronym = false
    var nextPart = ""
    for (part in parts) {
        if (parts.size >= n + 2) {
            nextPart = parts[n + 1]
        }

        if (part.length == 1 && part.lowercase() != "a" && part.toIntOrNull() == null) {
            result += "$part."
            previousAcronym = true
        } else if (part.lowercase() == "a" && (previousAcronym || nextPart.length == 1)) {
            result += "$part."
            previousAcronym = true
        } else {
            if (previousAcronym) {
                result += " "
                previousAcronym = false
            }

            result += "$part "
        }

        n++
    }

    return result.trim()
}