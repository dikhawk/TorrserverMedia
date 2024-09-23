package com.dik.videofilenameparser

import Revision
import kotlinx.datetime.LocalDateTime
import parseQuality


fun parseFileNameBase(name: String): ParsedBase {
    val titleAndYear = parseTitleAndYear(name)
    val title = titleAndYear.title
    val year: String? = titleAndYear.year

    val edition = parseEdition(name)
    val videoCodec = parseVideoCodec(name).codec
    val audioCodec = parseAudioCodec(name).codec
    val audioChannels = parseAudioChannels(name).channels
    val group = parseGroup(name)
    val languages = parseLanguage(name)
    val quality = parseQuality(name)
    val multi = isMulti(name)
    val complete = isComplete(name)

    return ParsedBase(
        title = title,
        year = year,
        resolution = quality.resolution,
        sources = quality.sources,
        videoCodec = videoCodec,
        audioCodec = audioCodec,
        audioChannels = audioChannels,
        revision = quality.revision,
        group = group,
        edition = edition,
        languages = languages,
        multi = multi,
        complete = complete
    )
}

fun parseFileNameTvShow(name: String): ParsedShow? {
    val season = parseSeason(name)
    if (season != null) {
        val base = parseFileNameBase(name)

        return ParsedShow(
            title = season.seriesTitle,
            year = base.year,
            resolution = base.resolution,
            sources = base.sources,
            videoCodec = base.videoCodec,
            audioCodec = base.audioCodec,
            audioChannels = base.audioChannels,
            group = base.group,
            revision = base.revision,
            languages = base.languages,
            multi = base.multi,
            complete = base.complete,
            seasons = season.seasons,
            episodeNumbers = season.episodeNumbers,
            airDate = season.airDate,
            fullSeason = season.fullSeason,
            isPartialSeason = season.isPartialSeason,
            isMultiSeason = season.isMultiSeason,
            isSeasonExtra = season.isSeasonExtra,
            isSpecial = season.isSpecial,
            seasonPart = season.seasonPart,
            edition = base.edition,
            isTv = true
        )
    }

    return null
}

data class ParsedBase(
    val title: String,
    val year: String?,
    val edition: Edition,
    val resolution: Resolution?,
    val sources: List<Source>,
    val videoCodec: VideoCodec?,
    val audioCodec: AudioCodec?,
    val audioChannels: Channels?,
    val group: String?,
    val revision: Revision,
    val languages: Set<Language>,
    val multi: Boolean?,
    val complete: Boolean?
) : ParsedFilename

data class ParsedShow(
    val title: String,
    val year: String?,
    val edition: Edition,
    val resolution: Resolution?,
    val sources: List<Source>,
    val videoCodec: VideoCodec?,
    val audioCodec: AudioCodec?,
    val audioChannels: Channels?,
    val group: String?,
    val revision: Revision,
    val languages: Set<Language>,
    val multi: Boolean?,
    val complete: Boolean?,
    val seasons: List<Int>,
    val episodeNumbers: List<Int>,
    val airDate: LocalDateTime?,
    val fullSeason: Boolean?,
    val isPartialSeason: Boolean?,
    val isMultiSeason: Boolean?,
    val isSeasonExtra: Boolean?,
    val isSpecial: Boolean?,
    val seasonPart: Int?,
    val isTv: Boolean
) : ParsedFilename

sealed interface ParsedFilename