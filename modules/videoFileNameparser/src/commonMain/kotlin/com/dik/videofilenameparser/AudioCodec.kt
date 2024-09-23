package com.dik.videofilenameparser


enum class AudioCodec(val value: String) {
    MP3("MP3"),
    MP2("MP2"),
    DOLBY("Dolby Digital"),
    EAC3("Dolby Digital Plus"),
    AAC("AAC"),
    FLAC("FLAC"),
    DTS("DTS"),
    DTSHD("DTS-HD"),
    TRUEHD("Dolby TrueHD"),
    OPUS("Opus"),
    VORBIS("Vorbis"),
    PCM("PCM"),
    LPCM("LPCM"),
    UNKNOWN("Unknown");
}

data class AudioCodecData(
    val codec: AudioCodec = AudioCodec.UNKNOWN,
    val source: String = AudioCodec.UNKNOWN.value
)

fun parseAudioCodec(title: String): AudioCodecData {
    val mp3CodecExp = "\\b(?<mp3>(LAME(?:\\d)+-?(?:\\d)+)|(mp3))\\b"
    val mp2CodecExp = "\\b(?<mp2>(mp2))\\b"
    val dolbyCodecExp = "\\b(?<dolby>(Dolby)|(Dolby-?Digital)|(DD)|(AC3D?))\\b"
    val dolbyAtmosCodecExp = "\\b(?<dolbyatmos>(Dolby-?Atmos))\\b"
    val aacAtmosCodecExp = "\\b(?<aac>(AAC))\\d?.?\\d?(ch)?\\b"
    val eac3CodecExp = "\\b(?<eac3>(EAC3|DDP|DD\\+))\\b"
    val flacCodecExp = "\\b(?<flac>(FLAC))\\b"
    val dtsCodecExp = "\\b(?<dts>(DTS))\\b"
    val dtsHdCodecExp = "\\b(?<dtshd>(DTS-?HD)|(DTS(?=-?MA)|(DTS-X)))\\b"
    val trueHdCodecExp = "\\b(?<truehd>(True-?HD))\\b"
    val opusCodecExp = "\\b(?<opus>(Opus))\\b"
    val vorbisCodecExp = "\\b(?<vorbis>(Vorbis))\\b"
    val pcmCodecExp = "\\b(?<pcm>(PCM))\\b"
    val lpcmCodecExp = "\\b(?<lpcm>(LPCM))\\b"

    val audioCodecExp =
        listOf(
            mp3CodecExp,
            mp2CodecExp,
            dolbyCodecExp,
            dolbyAtmosCodecExp,
            aacAtmosCodecExp,
            eac3CodecExp,
            flacCodecExp,
            dtsHdCodecExp,
            dtsCodecExp,
            trueHdCodecExp,
            opusCodecExp,
            vorbisCodecExp,
            pcmCodecExp,
            lpcmCodecExp
        ).joinToString("|").toRegex(RegexOption.IGNORE_CASE)

    val matchResult = audioCodecExp.find(title) ?: return AudioCodecData()
    val groups = matchResult.groups

    return when {
        groups["aac"] != null -> AudioCodecData(
            codec = AudioCodec.AAC,
            source  = groups["aac"]?.value ?: AudioCodec.AAC.value
        )

        groups["dolbyatmos"] != null -> AudioCodecData(
            codec = AudioCodec.EAC3,
            source  = groups["dolbyatmos"]?.value ?: AudioCodec.AAC.value
        )

        groups["dolby"] != null -> AudioCodecData(
            codec = AudioCodec.DOLBY,
            source  = groups["dolby"]?.value ?: AudioCodec.AAC.value
        )

        groups["dtshd"] != null -> AudioCodecData(
            codec = AudioCodec.DTSHD,
            source  = groups["dtshd"]?.value ?: AudioCodec.AAC.value
        )

        groups["dts"] != null -> AudioCodecData(
            codec = AudioCodec.DTS,
            source  = groups["dts"]?.value ?: AudioCodec.AAC.value
        )

        groups["flac"] != null -> AudioCodecData(
            codec = AudioCodec.FLAC,
            source  = groups["flac"]?.value ?: AudioCodec.AAC.value
        )

        groups["truehd"] != null -> AudioCodecData(
            codec = AudioCodec.TRUEHD,
            source  = groups["truehd"]?.value ?: AudioCodec.AAC.value
        )

        groups["mp3"] != null -> AudioCodecData(
            codec = AudioCodec.MP3,
            source  = groups["mp3"]?.value ?: AudioCodec.AAC.value
        )

        groups["mp2"] != null -> AudioCodecData(
            codec = AudioCodec.MP2,
            source  = groups["mp2"]?.value ?: AudioCodec.AAC.value
        )

        groups["pcm"] != null -> AudioCodecData(
            codec = AudioCodec.PCM,
            source  = groups["pcm"]?.value ?: AudioCodec.AAC.value
        )

        groups["lpcm"] != null -> AudioCodecData(
            codec = AudioCodec.LPCM,
            source  = groups["lpcm"]?.value ?: AudioCodec.AAC.value
        )

        groups["opus"] != null -> AudioCodecData(
            codec = AudioCodec.OPUS,
            source  = groups["opus"]?.value ?: AudioCodec.AAC.value
        )

        groups["vorbis"] != null -> AudioCodecData(
            codec = AudioCodec.VORBIS,
            source  = groups["vorbis"]?.value ?: AudioCodec.AAC.value
        )

        groups["eac3"] != null -> AudioCodecData(
            codec = AudioCodec.EAC3,
            source  = groups["eac3"]?.value ?: AudioCodec.AAC.value
        )

        else -> AudioCodecData()
    }
}