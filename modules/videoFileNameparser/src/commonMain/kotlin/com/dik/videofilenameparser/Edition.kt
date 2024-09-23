package com.dik.videofilenameparser


data class Edition(
    val internal: Boolean? = null,
    val limited: Boolean? = null,
    val remastered: Boolean? = null,
    val extended: Boolean? = null,
    val theatrical: Boolean? = null,
    val directors: Boolean? = null,  // Directors cut
    val unrated: Boolean? = null,
    val imax: Boolean? = null,
    val fanEdit: Boolean? = null,
    val hdr: Boolean? = null,
    val bw: Boolean? = null,  // black and white
    val threeD: Boolean? = null,  // 3D film
    val hsbs: Boolean? = null,  // half side by side 3D
    val sbs: Boolean? = null,  // side by side 3D
    val hou: Boolean? = null,  // half over under 3D
    val uhd: Boolean? = null,  // most 2160p should be UHD but there might be some that aren't?
    val oar: Boolean? = null,  // original aspect ratio
    val dolbyVision: Boolean? = null,
    val hardcodedSubs: Boolean? = null,
    val deletedScenes: Boolean? = null,
    val bonusContent: Boolean? = null
)

val internalExp = Regex("\\b(INTERNAL)\\b", RegexOption.IGNORE_CASE)
val remasteredExp = Regex("\\b(Remastered|Anniversary|Restored)\\b", RegexOption.IGNORE_CASE)
val imaxExp = Regex("\\b(IMAX)\\b", RegexOption.IGNORE_CASE)
val unratedExp = Regex("\\b(Uncensored|Unrated)\\b", RegexOption.IGNORE_CASE)
val extendedExp = Regex("\\b(Extended|Uncut|Ultimate|Rogue|Collector)\\b", RegexOption.IGNORE_CASE)
val theatricalExp = Regex("\\b(Theatrical)\\b", RegexOption.IGNORE_CASE)
val directorsExp = Regex("\\b(Directors?)\\b", RegexOption.IGNORE_CASE)
val fanExp = Regex("\\b(Despecialized|Fan.?Edit)\\b", RegexOption.IGNORE_CASE)
val limitedExp = Regex("\\b(LIMITED)\\b", RegexOption.IGNORE_CASE)
val hdrExp = Regex("\\b(HDR)\\b", RegexOption.IGNORE_CASE)
val threeD = Regex("\\b(3D)\\b", RegexOption.IGNORE_CASE)
val hsbs = Regex("\\b(Half-?SBS|HSBS)\\b", RegexOption.IGNORE_CASE)
val sbs = Regex("\\b((?<!H|HALF-)SBS)\\b", RegexOption.IGNORE_CASE)
val hou = Regex("\\b(HOU)\\b", RegexOption.IGNORE_CASE)
val uhd = Regex("\\b(UHD)\\b", RegexOption.IGNORE_CASE)
val oar = Regex("\\b(OAR)\\b", RegexOption.IGNORE_CASE)
val dolbyVision = Regex("\\b(DV(\\b(HDR10|HLG|SDR))?)\\b", RegexOption.IGNORE_CASE)
val hardcodedSubsExp = Regex(
    "\\b((?<hcsub>(\\w+(?<!SOFT|HORRIBLE)SUBS?))|(?<hc>(HC|SUBBED)))\\b",
    RegexOption.IGNORE_CASE
)
val deletedScenes = Regex("\\b((Bonus.)?Deleted.Scenes)\\b", RegexOption.IGNORE_CASE)
val bonusContent = Regex(
    "\\b((Bonus|Extras|Behind.the.Scenes|Making.of|Interviews|Featurettes|Outtakes|Bloopers|Gag.Reel).(?!(Deleted.Scenes)))\\b",
    RegexOption.IGNORE_CASE
)
val bw = Regex("\\b(BW)\\b", RegexOption.IGNORE_CASE)

fun parseEdition(title: String): Edition {
    val parsedTitle = parseTitleAndYear(title).title
    val withoutTitle = title.replace(".", " ").replace(parsedTitle, "").lowercase()

    val result = Edition(
        internal = internalExp.containsMatchIn(withoutTitle),
        limited = limitedExp.containsMatchIn(withoutTitle),
        remastered = remasteredExp.containsMatchIn(withoutTitle),
        extended = extendedExp.containsMatchIn(withoutTitle),
        theatrical = theatricalExp.containsMatchIn(withoutTitle),
        directors = directorsExp.containsMatchIn(withoutTitle),
        unrated = unratedExp.containsMatchIn(withoutTitle),
        imax = imaxExp.containsMatchIn(withoutTitle),
        fanEdit = fanExp.containsMatchIn(withoutTitle),
        hdr = hdrExp.containsMatchIn(withoutTitle),
        threeD = threeD.containsMatchIn(withoutTitle),
        hsbs = hsbs.containsMatchIn(withoutTitle),
        sbs = sbs.containsMatchIn(withoutTitle),
        hou = hou.containsMatchIn(withoutTitle),
        uhd = uhd.containsMatchIn(withoutTitle),
        oar = oar.containsMatchIn(withoutTitle),
        dolbyVision = dolbyVision.containsMatchIn(withoutTitle),
        hardcodedSubs = hardcodedSubsExp.containsMatchIn(withoutTitle),
        deletedScenes = deletedScenes.containsMatchIn(withoutTitle),
        bonusContent = bonusContent.containsMatchIn(withoutTitle),
        bw = bw.containsMatchIn(withoutTitle)
    )

    return result
}