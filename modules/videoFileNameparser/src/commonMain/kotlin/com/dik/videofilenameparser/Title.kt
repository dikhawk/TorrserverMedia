package com.dik.videofilenameparser


data class Title(
    val title: String,
    val year: String? = null,
)

val movieTitleYearRegex = listOf(
    // Special, Despecialized, etc. Edition Movies, e.g: Mission.Impossible.3.Special.Edition.2011
    Regex("^(?<title>(?![\\[(]).+?)?(?:(?:[-_\\W](?<!\\[)[!]))*\\(?\\b(?<edition>((Extended\\.|Ultimate\\.)?(Director'?s|Collector'?s|Theatrical|Anniversary|The\\.Uncut|Ultimate|Final(?=\\.(Cut|Edition|Version))|Extended|Rogue|Special|Despecialized|\\d{2,3}(th)?\\.Anniversary)?(\\.(Cut|Edition|Version))?(\\.(Extended|Uncensored|Remastered|Unrated|Uncut|IMAX|Fan.?Edit))?|Uncensored|Remastered|Unrated|Uncut|IMAX|Fan.?Edit|Edition|Restored|((2|3|4)in1))\\b\\)?.{1,3}(?<year>(1(8|9)|20)\\d{2}(?!p|i|\\d+|\\]|\\W\\d+)))+(\\W+|_|$)(?!\\\\)", RegexOption.IGNORE_CASE),
    // Folder movie format, e.g: Blade Runner 2049 (2017)
    Regex("^(?<title>(?![\\(\\[]).+?)?(?:(?:[-_\\W](?<![\\[])[!]))*\\((?<year>(1(8|9)|20)\\d{2}(?!p|i|(1(8|9)|20)\\d{2}|\\]|\\W(1(8|9)|20)\\d{2}))+", RegexOption.IGNORE_CASE),
    // Normal movie format, e.g: Mission.Impossible.3.2011
    Regex("^(?<title>(?![\\(\\[]).+?)?(?:(?:[-_\\W](?<![\\(\\[])[!]))*(?<year>(1(8|9)|20)\\d{2}(?!p|i|(1(8|9)|20)\\d{2}|\\]|\\W(1(8|9)|20)\\d{2}))+([\\W_]|$)(?!\\\\)", RegexOption.IGNORE_CASE),
    // PassThePopcorn Torrent names: Star.Wars[PassThePopcorn]
    Regex("^(?<title>.+?)?(?:(?:[-_\\W](?<![\\(\\[])[!]))*(?<year>(\\[\\w *\\]))+([\\W_]|$)(?!\\\\)", RegexOption.IGNORE_CASE),
    // That did not work? Maybe some tool uses [] for years. Who would do that?
    Regex("^(?<title>(?![\\(\\[]).+?)?(?:(?:[-_\\W](?<!\\[)[!]))*(?<year>(1(8|9)|20)\\d{2}(?!p|i|\\d+|\\W\\d+))+([\\W_]|$)(?!\\\\)", RegexOption.IGNORE_CASE),
    // As a last resort for movies that have ( or [ in their title.
    Regex("^(?<title>.+?)?(?:(?:[-_\\W](?<!\\[)[!]))*(?<year>(1(8|9)|20)\\d{2}(?!p|i|\\d+|\\]|\\W\\d+))+([\\W_]|$)(?!\\\\)", RegexOption.IGNORE_CASE)
)

fun parseTitleAndYear(title: String): Title {
    val simpleTitle = simplifyTitle(title)

    // Removing the group from the end could be trouble if a title is "title-year"
    val grouplessTitle = simpleTitle.replace(Regex("-([a-z0-9]+)$", RegexOption.IGNORE_CASE), "")

    for (exp in movieTitleYearRegex) {
        val match = exp.find(grouplessTitle)
        if (match != null && match.groups.isNotEmpty()) {
            val cleanedTitle = releaseTitleCleaner(match.groups["title"]?.value ?: "")
            if (cleanedTitle == null) continue

            val year = match.groups["year"]?.value

            return Title(title = cleanedTitle, year = year)
        }
    }

    // year not found, attempt parsing using codec or resolution
    val resolutionText = parseResolution(title).source
    val resolutionPosition = title.indexOf(resolutionText ?: "")
    val videoCodecText = parseVideoCodec(title).source
    val videoCodecPosition = title.indexOf(videoCodecText ?: "")
    val channelsText = parseAudioChannels(title).source
    val channelsPosition = title.indexOf(channelsText ?: "")
    val audioCodecText = parseAudioCodec(title).source
    val audioCodecPosition = title.indexOf(audioCodecText ?: "")

    val positions = listOf(resolutionPosition, audioCodecPosition, channelsPosition, videoCodecPosition).filter { it > 0 }
    if (positions.isNotEmpty()) {
        val firstPosition = positions.minOrNull() ?: 0

        return Title(title = releaseTitleCleaner(title.substring(0, firstPosition).trim()) ?: "", year = null)
    }

    return Title(title = title.trim(), year = null)
}
