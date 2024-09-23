package com.dik.videofilenameparser


enum class VideoCodec(val value: String) {
    X265("x265"),
    X264("x264"),
    H264("h264"),
    H265("h265"),
    WMV("WMV"),
    XVID("xvid"),
    DVDR("dvdr"),
    UNKNOWN("unknown")
}

data class VideoCodecData(
    val codec: VideoCodec = VideoCodec.UNKNOWN,
    val source: String = VideoCodec.UNKNOWN.value
)

private val x265Exp = "(?<x265>x265)"
private val h265Exp = "(?<h265>h265)"
private val x264Exp = "(?<x264>x264)"
private val h264Exp = "(?<h264>h264)"
private val WMVExp = "(?<wmv>WMV)"
private val xvidhdExp = "(?<xvidhd>XvidHD)"
private val xvidExp = "(?<xvid>X-?vid)"
private val divxExp = "(?<divx>divx)"
private val hevcExp = "(?<hevc>HEVC)"
private val dvdrExp = "(?<dvdr>DVDR)\\b"

private val codecExp = listOf(
    x265Exp,
    h265Exp,
    x264Exp,
    h264Exp,
    WMVExp,
    xvidhdExp,
    xvidExp,
    divxExp,
    hevcExp,
    dvdrExp
).joinToString("|").toRegex(RegexOption.IGNORE_CASE)

fun parseVideoCodec(title: String): VideoCodecData {
    val result = codecExp.find(title)
    val groups = result?.groups

    groups?.let {
        if (groups["h264"] != null) {
            return VideoCodecData(codec = VideoCodec.H264, source = groups["h264"]?.value ?: "")

        }

        if (groups["h265"] != null) {
            return VideoCodecData(codec = VideoCodec.H265, source = groups["h265"]?.value ?: "")
        }

        if (groups["x265"] != null || groups["hevc"] != null) {
            return VideoCodecData(codec = VideoCodec.X265, source = groups["hevc"]?.value ?: "")
        }

        if (groups["x264"] != null) {
            return VideoCodecData(codec = VideoCodec.X264, source = groups["x264"]?.value ?: "")
        }

        if (groups["xvidhd"] != null || groups["xvid"] != null || groups["divx"] != null) {
            return VideoCodecData(
                codec = VideoCodec.XVID, source = (groups["xvidhd"]?.value ?: groups["xvid"]?.value
                ?: groups["divx"]?.value) ?: ""
            )
        }

        if (groups["wmv"] != null) {
            return VideoCodecData(codec = VideoCodec.WMV, source = groups["wmv"]?.value ?: "")
        }

        if (groups["dvdr"] != null) {
            return VideoCodecData(codec = VideoCodec.DVDR, source = groups["dvdr"]?.value ?: "")
        }
    }

    return VideoCodecData()
}