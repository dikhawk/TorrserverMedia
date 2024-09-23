package com.dik.videofilenameparser


val websitePrefixExp = Regex("^\\[\\s*[a-z]+(\\.[a-z]+)+\\s*\\][- ]*|^www\\.[a-z]+\\.(?:com|net)[ -]*", RegexOption.IGNORE_CASE)
val cleanReleaseGroupExp = Regex("-(RP|1|NZBGeek|Obfuscated|Obfuscation|Scrambled|sample|Pre|postbot|xpost|Rakuv[a-z0-9]*|WhiteRev|BUYMORE|AsRequested|AlternativeToRequested|GEROV|Z0iDS3N|Chamele0n|4P|4Planet|AlteZachen|RePACKPOST)+$", RegexOption.IGNORE_CASE)
val releaseGroupRegexExp = Regex("-(?<releasegroup>[a-z0-9]+)(?<!WEB-DL|WEB-RIP|480p|720p|1080p|2160p|DTS-(HD|X|MA|ES)|([a-zA-Z]{3}-ENG))(?:\b|[-._ ])", RegexOption.IGNORE_CASE)
val animeReleaseGroupExp = Regex("^(?:\\[(?<subgroup>(?!\\s).+?(?<!\\s))\\](?:_|-|\\s|\\.)?)", RegexOption.IGNORE_CASE)
val exceptionReleaseGroupRegex = Regex("(\\[)?(?<releasegroup>(Joy|YIFY|YTS\\.(MX|LT|AG)|FreetheFish|VH-PROD|FTW-HS|DX-TV|Blu-bits|afm72|Anna|Bandi|Ghost|Kappa|MONOLITH|Qman|RZeroX|SAMPA|Silence|theincognito|D-Z0N3|t3nzin|Vyndros|HDO|DusIctv|DHD|SEV|CtrlHD|-ZR-|ADC|XZVN|RH|Kametsu|r00t|HONE))(\\])?$", RegexOption.IGNORE_CASE)

fun parseGroup(title: String): String? {
    val nowebsiteTitle = title.replace(websitePrefixExp, "")
    val releaseTitle = parseTitleAndYear(nowebsiteTitle).title.replace(" ", ".")
    var trimmed = nowebsiteTitle
        .replace(" ", ".")
        .replace(if (releaseTitle == nowebsiteTitle) "" else releaseTitle, "")
        .replace("""\.-\.""".toRegex(), ".")

    trimmed = simplifyTitle(removeFileExtension(trimmed.trim()))

    if (trimmed.isEmpty()) {
        return null
    }

    val exceptionResult = exceptionReleaseGroupRegex.find(trimmed)
    if (exceptionResult?.groups?.get("releasegroup") != null) {
        return exceptionResult.groups["releasegroup"]?.value
    }

    val animeResult = animeReleaseGroupExp.find(trimmed)
    if (animeResult?.groups != null) {
        return animeResult.groups["subgroup"]?.value ?: ""
    }

    trimmed = trimmed.replace(cleanReleaseGroupExp, "")

    val globalReleaseGroupExp = Regex(releaseGroupRegexExp.pattern, RegexOption.IGNORE_CASE)
    var result: MatchResult? = globalReleaseGroupExp.find(trimmed)

    while (result != null) {
        if (result.groups.isEmpty()) {
            result = globalReleaseGroupExp.find(trimmed, result.range.last + 1)
            continue
        }

        val group = result.groups["releasegroup"]?.value ?: ""
        return group
    }

    return null
}