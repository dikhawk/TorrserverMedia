package com.dik.videofilenameparser

private val blurayExp = Regex(
    "\\b(M?Blu-?Ray|HDDVD|BD|UHDBD|BDISO|BDMux|BD25|BD50|BR.?DISK|Bluray(1080|720)p?|BD(1080|720)p?)\\b",
    RegexOption.IGNORE_CASE
)
val webdlExp = Regex(
    "\\b(WEB[-_. ]DL|HDRIP|WEBDL|WEB-DLMux|NF|APTV|NETFLIX|NetflixU?HD|DSNY|DSNP|HMAX|AMZN|AmazonHD|iTunesHD|MaxdomeHD|WebHD|WEB$|[. ]WEB[. ](?:[xh]26[45]|DD5[. ]1)|\\d+0p[. ]WEB[. ]|\\b\\s\\/\\sWEB\\s\\/\\s\\b|AMZN[. ]WEB[. ])\\b",
    RegexOption.IGNORE_CASE
)
private val webripExp = Regex("\\b(WebRip|Web-Rip|WEBCap|WEBMux)\\b", RegexOption.IGNORE_CASE)
private val hdtvExp = Regex("\\b(HDTV)\\b", RegexOption.IGNORE_CASE)
private val bdripExp = Regex("\\b(BDRip)\\b", RegexOption.IGNORE_CASE)
private val brripExp = Regex("\\b(BRRip)\\b", RegexOption.IGNORE_CASE)
private val dvdrExp = Regex("\\b(DVD-R|DVDR)\\b", RegexOption.IGNORE_CASE)
private val dvdExp = Regex("\\b(DVD9?|DVDRip|NTSC|PAL|xvidvd|DvDivX)\\b", RegexOption.IGNORE_CASE)
private val dsrExp = Regex("\\b(WS[-_. ]DSR|DSR)\\b", RegexOption.IGNORE_CASE)
private val regionalExp = Regex("\\b(R[0-9]{1}|REGIONAL)\\b", RegexOption.IGNORE_CASE)
private val ppvExp = Regex("\\b(PPV)\\b", RegexOption.IGNORE_CASE)
private val scrExp = Regex("\\b(SCR|SCREENER|DVDSCR|(DVD|WEB).?SCREENER)\\b", RegexOption.IGNORE_CASE)
private val tsExp = Regex("\\b(TS|TELESYNC|HD-TS|HDTS|PDVD|TSRip|HDTSRip)\\b", RegexOption.IGNORE_CASE)
private val tcExp = Regex("\\b(TC|TELECINE|HD-TC|HDTC)\\b", RegexOption.IGNORE_CASE)
private val camExp = Regex("\\b(CAMRIP|CAM|HDCAM|HD-CAM)\\b", RegexOption.IGNORE_CASE)
private val workprintExp = Regex("\\b(WORKPRINT|WP)\\b", RegexOption.IGNORE_CASE)
private val pdtvExp = Regex("\\b(PDTV)\\b", RegexOption.IGNORE_CASE)
private val sdtvExp = Regex("\\b(SDTV)\\b", RegexOption.IGNORE_CASE)
private val tvripExp = Regex("\\b(TVRip)\\b", RegexOption.IGNORE_CASE)

enum class Source {
    BLURAY, WEBDL, WEBRIP, DVD, CAM, SCREENER, PPV, TELESYNC, TELECINE, WORKPRINT, TV
}

data class SourceGroups(
    val bluray: Boolean,
    val webdl: Boolean,
    val webrip: Boolean,
    val hdtv: Boolean,
    val bdrip: Boolean,
    val brrip: Boolean,
    val scr: Boolean,
    val dvdr: Boolean,
    val dvd: Boolean,
    val dsr: Boolean,
    val regional: Boolean,
    val ppv: Boolean,
    val ts: Boolean,
    val tc: Boolean,
    val cam: Boolean,
    val workprint: Boolean,
    val pdtv: Boolean,
    val sdtv: Boolean,
    val tvrip: Boolean
)

fun parseSourceGroups(title: String): SourceGroups {
    val normalizedName = title.replace("_", " ").replace("[", " ").replace("]", " ").trim()

    return SourceGroups(
        bluray = blurayExp.containsMatchIn(normalizedName),
        webdl = webdlExp.containsMatchIn(normalizedName),
        webrip = webripExp.containsMatchIn(normalizedName),
        hdtv = hdtvExp.containsMatchIn(normalizedName),
        bdrip = bdripExp.containsMatchIn(normalizedName),
        brrip = brripExp.containsMatchIn(normalizedName),
        scr = scrExp.containsMatchIn(normalizedName),
        dvdr = dvdrExp.containsMatchIn(normalizedName),
        dvd = dvdExp.containsMatchIn(normalizedName),
        dsr = dsrExp.containsMatchIn(normalizedName),
        regional = regionalExp.containsMatchIn(normalizedName),
        ppv = ppvExp.containsMatchIn(normalizedName),
        ts = tsExp.containsMatchIn(normalizedName),
        tc = tcExp.containsMatchIn(normalizedName),
        cam = camExp.containsMatchIn(normalizedName),
        workprint = workprintExp.containsMatchIn(normalizedName),
        pdtv = pdtvExp.containsMatchIn(normalizedName),
        sdtv = sdtvExp.containsMatchIn(normalizedName),
        tvrip = tvripExp.containsMatchIn(normalizedName)
    )
}

fun parseSource(title: String): List<Source> {
    val groups = parseSourceGroups(title)
    val result = mutableListOf<Source>()

    if (groups.bluray || groups.bdrip || groups.brrip) {
        result.add(Source.BLURAY)
    }

    if (groups.webrip) {
        result.add(Source.WEBRIP)
    }

    if (!groups.webrip && groups.webdl) {
        result.add(Source.WEBDL)
    }

    if (groups.dvdr || (groups.dvd && !groups.scr)) {
        result.add(Source.DVD)
    }

    if (groups.ppv) {
        result.add(Source.PPV)
    }

    if (groups.workprint) {
        result.add(Source.WORKPRINT)
    }

    if (groups.pdtv || groups.sdtv || groups.dsr || groups.tvrip || groups.hdtv) {
        result.add(Source.TV)
    }

    if (groups.cam) {
        result.add(Source.CAM)
    }

    if (groups.ts) {
        result.add(Source.TELESYNC)
    }

    if (groups.tc) {
        result.add(Source.TELECINE)
    }

    if (groups.scr) {
        result.add(Source.SCREENER)
    }

    return result
}
