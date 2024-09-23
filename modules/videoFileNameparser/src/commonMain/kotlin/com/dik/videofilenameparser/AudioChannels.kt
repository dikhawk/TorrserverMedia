package com.dik.videofilenameparser


enum class Channels(val value: String) {
    SEVEN("7.1"),
    SIX("5.1"),
    STEREO("stereo"),
    MONO("mono"),
    UNKNOWN("unknown")
}

data class ChannelsData(
    val channels: Channels = Channels.UNKNOWN,
    val source: String = Channels.UNKNOWN.value,
)

fun parseAudioChannels(title: String): ChannelsData {
    val eightChannelExp = "\\b(?<eight>7.?[01])\\b"
    val sixChannelExp = "\\b(?<six>(6\\W0(?:ch)?)(?=\\D|$)|(5\\W[01](?:ch)?)(?=\\D|$)|5ch|6ch)\\b"
    val stereoChannelExp = "(?<stereo>((2\\W0(?:ch)?)(?=\\D|$))|(stereo))"
    val monoChannelExp = "(?<mono>(1\\W0(?:ch)?)(?=\\D|$)|(mono)|(1ch))"

    val channelExp = listOf(
        eightChannelExp,
        sixChannelExp,
        stereoChannelExp,
        monoChannelExp
    ).joinToString("|").toRegex(RegexOption.IGNORE_CASE)

    val matchResult = channelExp.find(title) ?: return ChannelsData()

    val groups = matchResult.groups

    return when {
        groups["eight"] != null -> ChannelsData(
            channels = Channels.SEVEN,
            source = groups["eight"]?.value ?: Channels.UNKNOWN.value
        )

        groups["six"] != null -> ChannelsData(
            channels = Channels.SIX,
            source = groups["six"]?.value ?: Channels.UNKNOWN.value
        )

        groups["stereo"] != null -> ChannelsData(
            channels = Channels.STEREO,
            source = groups["stereo"]?.value ?: Channels.UNKNOWN.value
        )

        groups["mono"] != null -> ChannelsData(
            channels = Channels.MONO,
            source = groups["mono"]?.value ?: Channels.UNKNOWN.value
        )

        else -> ChannelsData()
    }
}
