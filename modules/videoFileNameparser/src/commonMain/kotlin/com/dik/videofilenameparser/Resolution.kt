package com.dik.videofilenameparser


enum class Resolution(val value: String) {
    R2160P("2160P"),
    R1080P("1080P"),
    R720P("720P"),
    R576P("576P"),
    R540P("540P"),
    R480P("480P"),
    UNKNOWN("UNKNOWN");
}

data class ResolutionData(
    val source: String = Resolution.UNKNOWN.value,
    val resolution: Resolution = Resolution.UNKNOWN
)

val R2160pExp = Regex(
    "(?<R2160P>2160p|4k[-_. ](?:UHD|HEVC|BD)|(?:UHD|HEVC|BD)[-_. ]4k|\\b(4k)\\b|COMPLETE.UHD|UHD.COMPLETE)",
    RegexOption.IGNORE_CASE
)
private val R1080pExp = Regex("(?<R1080P>1080(i|p)|1920x1080)(10bit)?", RegexOption.IGNORE_CASE)
private val R720pExp = Regex("(?<R720P>720(i|p)|1280x720|960p)(10bit)?", RegexOption.IGNORE_CASE)
private val R576pExp = Regex("(?<R576P>576(i|p))", RegexOption.IGNORE_CASE)
private val R540pExp = Regex("(?<R540P>540(i|p))", RegexOption.IGNORE_CASE)
private val R480Exp = Regex("(?<R480P>480(i|p)|640x480|848x480)", RegexOption.IGNORE_CASE)

val resolutionExp = Regex(
    listOf(
        R2160pExp.pattern,
        R1080pExp.pattern,
        R720pExp.pattern,
        R576pExp.pattern,
        R540pExp.pattern,
        R480Exp.pattern
    ).joinToString("|"),
    RegexOption.IGNORE_CASE
)

fun parseResolution(title: String): ResolutionData {
    val result = resolutionExp.find(title)
    val groups = result?.groups

    groups?.let {
        Resolution.entries.forEach { resolution ->
            if (groups[resolution.name] != null) {
                return ResolutionData(
                    resolution = resolution,
                    source = groups[resolution.name]?.value ?: ""
                )
            }
        }
    }

    // Fallback to guessing from some sources
    // Make safe assumptions like dvdrip is probably 480p
    val source = parseSource(title)
    if (source.contains(Source.DVD)) {
        return ResolutionData(resolution = Resolution.R480P, source = "")
    }

    return ResolutionData()
}
