package com.dik.videofilenameparser

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime


val reportTitleExp = listOf(
    // Daily episode with year in series title and air time after date (Plex DVR format)
    Regex(
        "^(?<title>.+?\\((?<titleyear>\\d{4})\\))[-_. ]+(?<airyear>19[4-9]\\d|20\\d\\d)(?<sep>[-_]?)(?<airmonth>0\\d|1[0-2])\\k<sep>(?<airday>[0-2]\\d|3[01])[-_. ]\\d{2}[-_. ]\\d{2}[-_. ]\\d{2}",
        RegexOption.IGNORE_CASE
    ),

    // Daily episodes without title (2018-10-12, 20181012)
    Regex(
        "^(?<airyear>19[6-9]\\d|20\\d\\d)(?<sep>[-_]?)(?<airmonth>0\\d|1[0-2])\\k<sep>(?<airday>[0-2]\\d|3[01])(?!\\d)",
        RegexOption.IGNORE_CASE
    ),
    // Multi-Part episodes without a title (S01E05.S01E06)
    Regex(
        "^(?:\\W*S(?<season>(?<!\\d{1,20})(?:\\d{1,2}|\\d{4})(?!\\d+))(?:e{1,2}(?<episode>\\d{1,3}(?!\\d+)))+){2,}",
        RegexOption.IGNORE_CASE
    ),
    // Multi-episode with single episode numbers (S6.E1-E2, S6.E1E2, S6E1E2, etc)
    Regex(
        "^(?<title>.+?)[-_. ]S(?<season>(?<!\\d{1,20})(?:\\d{1,2}|\\d{4})(?!\\d+))(?:[-_. ]?[ex]?(?<episode>(?<!\\d{1,20})\\d{1,2}(?!\\d+)))+",
        RegexOption.IGNORE_CASE
    ),
    // Multi-Episode with a title (S01E05E06, S01E05-06, S01E05 E06, etc) and trailing info in slashes
    Regex(
        "^(?<title>.+?)(?:(?:-\\W)+S?(?<season>(?<!\\d{1,20})(?:\\d{1,2})(?!\\d+))(?:[ex]|\\W[ex]|){1,2}(?<episode>\\d{2,3}(?!\\d+))(?:(?:-|[ex]|\\W[ex]|){1,2}(?<episode1>\\d{2,3}(?!\\d+)))+).+?(?:[.+?])(?!\\))",
        RegexOption.IGNORE_CASE
    ),
    // Episodes without a title, Multi (S01E04E05, 1x04x05, etc)
    Regex(
        "(?:S?(?<season>(?<!\\d{1,50})(?:\\d{1,2}|\\d{4})(?!\\d+))(?:(?:[-_]|[ex]){1,2}(?<episode>\\d{2,3}(?!\\d+))){2,})",
        RegexOption.IGNORE_CASE
    ),
    // Episodes without a title, Single (S01E05, 1x05)
    Regex(
        "^(?:S?(?<season>(?<!\\d{1,50})(?:\\d{1,2}|\\d{4})(?!\\d+))(?:(?:[-_ ]?[ex])(?<episode>\\d{2,3}(?!\\d+))))",
        RegexOption.IGNORE_CASE
    ),
    // Anime - [SubGroup] Title Episode Absolute Episode Number ([SubGroup] Series Title Episode 01)
    Regex(
        "^(?:\\[(?<subgroup>.+?)\\][-_. ]?)(?<title>.+?)[-_. ](?:Episode)(?:[-_. ]+(?<absoluteepisode>(?<!\\d{1,50})\\d{2,3}(\\.\\d{1,2})?(?!\\d+)))+(?:_|-|\\s|\\.)*?(?<hash>\\[.{8}\\])?(?:$|\\.)?",
        RegexOption.IGNORE_CASE
    ),
    // Anime - [SubGroup] Title Absolute Episode Number + Season+Episode
    Regex(
        "^(?:\\[(?<subgroup>.+?)\\](?:_|-|\\s|\\.)?)(?<title>.+?)(?:(?:[-_\\W](?<![\\(\\)\\[!]))+(?<absoluteepisode>\\d{2,3}(\\.\\d{1,2})?))+(?:_|-|\\s|\\.)+(?:S?(?<season>(?<!\\d{1,50})\\d{1,2}(?!\\d+))(?:(?:-|[ex]|\\W[ex]){1,2}(?<episode>\\d{2}(?!\\d+)))+).*?(?<hash>[\\(\\[]\\w{8}[\\)\\]])?(?:$|\\.)",
        RegexOption.IGNORE_CASE
    ),
    // Anime - [SubGroup] Title Season+Episode + Absolute Episode Number
    Regex(
        "^(?:\\[(?<subgroup>.+?)\\](?:_|-|\\s|\\.)?)(?<title>.+?)(?:[-_\\W](?<![\\[\\(\\)\\[]!))+(?:S?(?<season>(?<!\\d{1,50})\\d{1,2}(?!\\d+))(?:(?:-|[ex]|\\W[ex]){1,2}(?<episode>\\d{2}(?!\\d+)))+)(?:(?:_|-|\\s|\\.)+(?<absoluteepisode>(?<!\\d{1,20})\\d{2,3}(\\.\\d{1,2})?(?!\\d+)))+.*?(?<hash>\\[\\w{8}\\])?(?:$|\\.)",
        RegexOption.IGNORE_CASE
    ),
    // Anime - [SubGroup] Title Season+Episode
    Regex(
        "^(?:\\[(?<subgroup>.+?)\\](?:_|-|\\s|\\.)?)(?<title>.+?)(?:[-_\\W](?<![\\(\\)\\[!]))+(?:S?(?<season>(?<!\\d{1,50})\\d{1,2}(?!\\d+))(?:(?:[ex]|\\W[ex]){1,2}(?<episode>\\d{2}(?!\\d+)))+)(?:\\s|\\.).*?(?<hash>\\[\\w{8}\\])?(?:$|\\.)",
        RegexOption.IGNORE_CASE
    ),
    // Anime - [SubGroup] Title with trailing number Absolute Episode Number
    Regex(
        "^\\[(?<subgroup>.+?)\\][-_. ]?(?<title>[^-]+?\\d+?)[-_. ]+(?:[-_. ]?(?<absoluteepisode>\\d{3}(\\.\\d{1,2})?(?!\\d+)))+(?:[-_. ]+(?<special>special|ova|ovd))?.*?(?<hash>\\[\\w{8}\\])?(?:$|\\.mkv)",
        RegexOption.IGNORE_CASE
    ),
    // Anime - [SubGroup] Title - Absolute Episode Number
    Regex(
        "^\\[(?<subgroup>.+?)\\][-_. ]?(?<title>.+?)(?:[. ]-[. ](?<absoluteepisode>\\d{2,3}(\\.\\d{1,2})?(?!\\d+|[-])))+(?:[-_. ]+(?<special>special|ova|ovd))?.*?(?<hash>\\[\\w{8}\\])?(?:$|\\.mkv)",
        RegexOption.IGNORE_CASE
    ),

    // Anime - [SubGroup] Title Absolute Episode Number
    Regex(
        "^\\[(?<subgroup>.+?)\\][-_. ]?(?<title>.+?)[-_. ]+\\(?(?:[-_. ]?#?(?<absoluteepisode>\\d{2,3}(\\.\\d{1,2})?(?!\\d+)))+\\)?(?:[-_. ]+(?<special>special|ova|ovd))?.*?(?<hash>\\[\\w{8}\\])?(?:$|\\.mkv)",
        RegexOption.IGNORE_CASE
    ),
    // Multi-episode Repeated (S01E05 - S01E06, 1x05 - 1x06, etc)
    Regex(
        "^(?<title>.+?)(?:(?:[-_\\W](?<![\\(\\[!]))+S?(?<season>(?<!\\d{1,50})(?:\\d{1,2}|\\d{4})(?!\\d+))(?:(?:[ex]|[-_. ]e){1,2}(?<episode>\\d{1,3}(?!\\d+)))+){2,}",
        RegexOption.IGNORE_CASE
    ),

    // Single episodes with a title (S01E05, 1x05, etc)
    // modified from sonarr to not match "trailing info in slashes"
    Regex(
        "^(?<title>.+?)(?:(?:[-_\\W](?<![\\(\\[!]))+S?(?<season>(?<!\\d{1,50})(?:\\d{1,2})(?!\\d+))(?:[ex]|\\W[ex]|_){1,2}(?<episode>(?!265|264)\\d{2,3}(?!\\d+|(?:[ex]|\\W[ex]|_|-){1,2})))",
        RegexOption.IGNORE_CASE
    ),

    // Anime - Title Season EpisodeNumber + Absolute Episode Number [SubGroup]
    Regex(
        "^(?<title>.+?)(?:[-_\\W](?<![\\(\\[!]))+(?:S?(?<season>(?<!\\d{1,50})\\d{1,2}(?!\\d+))(?:(?:[ex]|\\W[ex]){1,2}(?<episode>(?<!\\d{1,50})\\d{2}(?!\\d+)))).+?(?:[-_. ]?(?<absoluteepisode>(?<!\\d{1,50})\\d{3}(\\.\\d{1,2})?(?!\\d+)))+.+?\\[(?<subgroup>.+?)\\](?:$|\\.mkv)",
        RegexOption.IGNORE_CASE
    ),

    // Anime - Title Absolute Episode Number [SubGroup] [Hash]? (Series Title Episode 99-100 [RlsGroup] [ABCD1234])
    Regex(
        "^(?<title>.+?)[-_. ]Episode(?:[-_. ]+(?<absoluteepisode>\\d{2,3}(\\.\\d{1,2})?(?!\\d+)))+(?:.+?)\\[(?<subgroup>.+?)\\].*?(?<hash>\\[\\w{8}\\])?(?:$|\\.)",
        RegexOption.IGNORE_CASE
    ),

    // Anime - Title Absolute Episode Number [SubGroup] [Hash]
    Regex(
        "^(?<title>.+?)(?:(?:_|-|\\s|\\.)+(?<absoluteepisode>\\d{3}(\\.\\d{1,2})(?!\\d+)))+(?:.+?)\\[(?<subgroup>.+?)\\].*?(?<hash>\\[\\w{8}\\])?(?:$|\\.)",
        RegexOption.IGNORE_CASE
    ),

    // Anime - Title Absolute Episode Number [Hash]
    Regex(
        "^(?<title>.+?)(?:(?:_|-|\\s|\\.)+(?<absoluteepisode>\\d{2,3}(\\.\\d{1,2})?(?!\\d+)))+(?:[-_. ]+(?<special>special|ova|ovd))?[-_. ]+.*?(?<hash>\\[\\w{8}\\])(?:$|\\.)",
        RegexOption.IGNORE_CASE
    ),

    // Episodes with airdate AND season/episode number, capture season/epsiode only
    Regex(
        "^(?<title>.+?)?\\W*(?<airdate>\\d{4}\\W+[0-1][0-9]\\W+[0-3][0-9])(?!\\W+[0-3][0-9])[-_. ](?:s?(?<season>(?<!\\d{1,50})(?:\\d{1,2})(?!\\d+)))(?:[ex](?<episode>(?<!\\d{1,50})(?:\\d{1,3})(?!\\d+)))",
        RegexOption.IGNORE_CASE
    ),

    // Episodes with airdate AND season/episode number
    Regex(
        "^(?<title>.+?)?\\W*(?<airyear>\\d{4})\\W+(?<airmonth>[0-1][0-9])\\W+(?<airday>[0-3][0-9])(?!\\W+[0-3][0-9]).+?(?:s?(?<season>(?<!\\d{1,50})(?:\\d{1,2})(?!\\d+)))(?:[ex](?<episode>(?<!\\d{1,50})(?:\\d{1,3})(?!\\d+)))",
        RegexOption.IGNORE_CASE
    ),

    // Episodes with a title, 4 digit season number, Single episodes (S2016E05, etc) & Multi-episode (S2016E05E06, S2016E05-06, S2016E05 E06, etc)
    Regex(
        "^(?<title>.+?)(?:(?:[-_\\W](?<![\\(\\[!]))+S(?<season>(?<!\\d{1,50})(?:\\d{4})(?!\\d+))(?:e|\\We|_){1,2}(?<episode>\\d{2,3}(?!\\d+))(?:(?:-|e|\\We|_){1,2}(?<episode1>\\d{2,3}(?!\\d+)))*)\\W?(?!\\\\)",
        RegexOption.IGNORE_CASE
    ),

    // Episodes with a title, 4 digit season number, Single episodes (2016x05, etc) & Multi-episode (2016x05x06, 2016x05-06, 2016x05 x06, etc)
    Regex(
        "^(?<title>.+?)(?:(?:[-_\\W](?<![\\(\\[!]))+(?<season>(?<!\\d{1,50})(?:\\d{4})(?!\\d+))(?:x|\\Wx){1,2}(?<episode>\\d{2,3}(?!\\d+))(?:(?:-|x|\\Wx|_){1,2}(?<episode1>\\d{2,3}(?!\\d+)))*)\\W?(?!\\\\)",
        RegexOption.IGNORE_CASE
    ),

    //Multi-season pack
    Regex(
        "^(?<title>.+?)[-_. ]+S(?<season>(?<!\\d{1,50})(?:\\d{1,2})(?!\\d+))\\W?-\\W?S?(?<season1>(?<!\\d{1,50})(?:\\d{1,2})(?!\\d+))",
        RegexOption.IGNORE_CASE
    ),

    // Partial season pack
    Regex(
        "^(?<title>.+?)(?:\\W+S(?<season>(?<!\\d{1,50})(?:\\d{1,2})(?!\\d+))\\W+(?:(?:Part\\W?|(?<!\\d{1,50}\\W{1,150})e)(?<seasonpart>\\d{1,2}(?!\\d+)))+)",
        RegexOption.IGNORE_CASE
    ),

    // Mini-Series with year in title, treated as season 1, episodes are labelled as Part01, Part 01, Part.1
    Regex(
        "^(?<title>.+?\\d{4})(?:\\W+(?:(?:Part\\W?|e)(?<episode>\\d{1,2}(?!\\d+)))+)",
        RegexOption.IGNORE_CASE
    ),

    // Mini-Series, treated as season 1, multi episodes are labelled as E1-E2
    Regex(
        "^(?<title>.+?)(?:[-._ ][e])(?<episode>\\d{2,3}(?!\\d+))(?:(?:-?[e])(?<episode1>\\d{2,3}(?!\\d+)))+",
        RegexOption.IGNORE_CASE
    ),

    // Mini-Series, treated as season 1, episodes are labelled as Part01, Part 01, Part.1
    Regex(
        "^(?<title>.+?)(?:\\W+(?:(?:Part\\W?|(?<!\\d{1,50}\\W{1,50})e)(?<episode>\\d{1,2}(?!\\d+)))+)",
        RegexOption.IGNORE_CASE
    ),

    // Mini-Series, treated as season 1, episodes are labelled as Part One/Two/Three/...Nine, Part.One, Part_One
    Regex(
        "^(?<title>.+?)(?:\\W+(?:Part[-._ ](?<episode>One|Two|Three|Four|Five|Six|Seven|Eight|Nine)(>[-._ ])))",
        RegexOption.IGNORE_CASE
    ),

    // Mini-Series, treated as season 1, episodes are labelled as XofY
    Regex(
        "^(?<title>.+?)(?:\\W+(?:(?<episode>(?<!\\d{1,50})\\d{1,2}(?!\\d+))of\\d+)+)",
        RegexOption.IGNORE_CASE
    ),

    // Supports Season 01 Episode 03
    Regex(
        "(?:.(?:\"\"|^))(?<title>.?)(?:-_\\W)+(?:\\W?Season\\W?)(?<season>(?<!\\d{1,50})\\d{1,2}(?!\\d+))(?:\\W|)+(?:Episode\\W)(?:[-. ]?(?<episode>(?<!\\d{1,50})\\d{1,2}(?!\\d+)))+",
        RegexOption.IGNORE_CASE
    ),

    // Multi-episode with episodes in square brackets (Series Title [S01E11E12] or Series Title [S01E11-12])
    Regex(
        "(?:.*(?:^))(?<title>.*?)[-._ ]+\\[S(?<season>(?<!\\d{1,50})\\d{2}(?!\\d+))(?:[E-]{1,2}(?<episode>(?<!\\d{1,50})\\d{2}(?!\\d+)))+\\]",
        RegexOption.IGNORE_CASE
    ),

    // Multi-episode release with no space between series title and season (S01E11E12)
    Regex(
        "(?:.*(?:^))(?<title>.*?)S(?<season>(?<!\\d{1,50})\\d{2}(?!\\d+))(?:E(?<episode>(?<!\\d{1,50})\\d{2}(?!\\d+)))+",
        RegexOption.IGNORE_CASE
    ),


    // Single episode season or episode S1E1 or S1-E1 or S1.Ep1 or S01.Ep.01
    Regex(
        "(?:.*(?:\"\"|^))(?<title>.*?)(?:\\W?|_)S(?<season>(?<!\\d{1,50})\\d{1,2}(?!\\d+))(?:\\W|_)?Ep?[ ._]?(?<episode>(?<!\\d{1,50})\\d{1,2}(?!\\d+))",
        RegexOption.IGNORE_CASE
    ),

    // 3 digit season S010E05
    Regex(
        "(?:.*(?:\"\"|^))(?<title>.*?)(?:\\W?|_)S(?<season>(?<!\\d{1,50})\\d{3}(?!\\d+))(?:\\W|_)?E(?<episode>(?<!\\d{1,50})\\d{1,2}(?!\\d+))",
        RegexOption.IGNORE_CASE
    ),

    // 5 digit episode number with a title
    Regex(
        "^(?:(?<title>.+?)(?:_|-|\\s|\\.)+)(?:S?(?<season>(?<!\\d{1,50})\\d{1,2}(?!\\d+)))(?:(?:-|[ex]|\\W[ex]|_){1,2}(?<episode>(?<!\\d{1,50})\\d{5}(?!\\d+)))",
        RegexOption.IGNORE_CASE
    ),

    // 5 digit multi-episode with a title
    Regex(
        "^(?:(?<title>.+?)(?:_|-|\\s|\\.)+)(?:S?(?<season>(?<!\\d{1,50})\\d{1,2}(?!\\d+)))(?:(?:[-_. ]{1,3}ep){1,2}(?<episode>(?<!\\d{1,50})\\d{5}(?!\\d+)))+",
        RegexOption.IGNORE_CASE
    ),

    // Separated season and episode numbers S01 - E01
    Regex(
        "^(?<title>.+?)(?:_|-|\\s|\\.)+S(?<season>\\d{2}(?!\\d+))(\\W-\\W)E(?<episode>(?<!\\d{1,50})\\d{2}(?!\\d+))(?!\\\\)",
        RegexOption.IGNORE_CASE
    ),

    // Anime - Title with season number - Absolute Episode Number (Title S01 - EP14)
    Regex(
        "^(?<title>.+?S\\d{1,2})[-_. ]{3,}(?:EP)?(?<absoluteepisode>\\d{2,3}(\\.\\d{1,2})?(?!\\d+|[-]))",
        RegexOption.IGNORE_CASE
    ),


    // Anime - French titles with single episode numbers, with or without leading sub group ([RlsGroup] Title - Episode 1)
    Regex(
        "^(?:[(?<subgroup>.+?)][-_. ]?)?(?<title>.+?)[-_. ]+?(?:Episode[-_. ]+?)(?<absoluteepisode>\\d{1}(\\.\\d{1,2})?(?!\\d+))",
        RegexOption.IGNORE_CASE
    ),

    // Season only releases
    Regex(
        "^(?<title>.+?)\\W(?:S|Season)\\W?(?<season>\\d{1,2}(?!\\d+))(\\W+|_|\$)(?<extras>EXTRAS|SUBPACK)?(?!\\\\)",
        RegexOption.IGNORE_CASE
    ),

    // Episodes with a title and season/episode in square brackets
    Regex(
        "^(?<title>.+?)(?:(?:[-_\\W](?<![()\\[!]))+\\[S?(?<season>(?<!\\d{1,50})\\d{1,2}(?!\\d+))(?:(?:-|[ex]|\\W[ex]|_){1,2}(?<episode>(?<!\\d{1,50})\\d{2}(?!\\d+|i|p)))+\\])\\W?(?!\\\\)",
        RegexOption.IGNORE_CASE
    ),

    // Supports 103/113 naming
    Regex(
        "^(?<title>.+?)?(?:(?:[_.](?<![()\\[!]))+(?<season>(?<!\\d{1,50})[1-9])(?<episode>[1-9][0-9]|[0][1-9])(?![a-z]|\\d+))+(?:[_.]|$)",
        RegexOption.IGNORE_CASE
    ),

    // 4 digit episode number
    // Episodes without a title, Single (S01E05, 1x05) AND Multi (S01E04E05, 1x04x05, etc)
    Regex(
        "^(?:S?(?<season>(?<!\\d{1,50})\\d{1,2}(?!\\d+))(?:(?:-|[ex]|\\W[ex]|_){1,2}(?<episode>\\d{4}(?!\\d+|i|p)))+)(\\W+|_|\$)(?!\\\\)",
        RegexOption.IGNORE_CASE
    ),


    // 4 digit episode number
    // Episodes with a title, Single episodes (S01E05, 1x05, etc) & Multi-episode (S01E05E06, S01E05-06, S01E05 E06, etc)
    Regex(
        "^(?<title>.+?)(?:(?:[-_\\W](?<![()\\[!]))+S?(?<season>(?<!\\d{1,50})\\d{1,2}(?!\\d+))(?:(?:-|[ex]|\\W[ex]|_){1,2}(?<episode>\\d{4}(?!\\d+|i|p)))+)\\W?(?!\\\\)",
        RegexOption.IGNORE_CASE
    ),

    // Episodes with airdate (2018.04.28)
    Regex(
        "^(?<title>.+?)?\\W*(?<airyear>\\d{4})[-_. ]+(?<airmonth>[0-1][0-9])[-_. ]+(?<airday>[0-3][0-9])(?![-_. ]+[0-3][0-9])",
        RegexOption.IGNORE_CASE
    ),

    // Episodes with airdate (04.28.2018)
    Regex(
        "^(?<title>.+?)?\\W*(?<airmonth>[0-1][0-9])[-_. ]+(?<airday>[0-3][0-9])[-_. ]+(?<airyear>\\d{4})(?!\\d+)",
        RegexOption.IGNORE_CASE
    ),

    // Supports 1103/1113 naming
    Regex(
        "^(?<title>.+?)?(?:(?:[-_\\W](?<![()\\[!]))*(?<season>(?<!\\d{1,50}|\\(|\\[|e|x)\\d{2})(?<episode>(?<!e|x)\\d{2}(?!p|i|\\d+|\\)|\\]|\\W\\d+|\\W(?:e|ep|x)\\d+)))+(\\W+|_|$)(?!\\\\)",
        RegexOption.IGNORE_CASE
    ),

    // Episodes with single digit episode number (S01E1, S01E5E6, etc)
    Regex(
        "^(?<title>.*?)(?:(?:[-_\\W](?<![()\\[!]))+S?(?<season>(?<!\\d{1,50})\\d{1,2}(?!\\d+))(?:(?:-|[ex]){1,2}(?<episode>\\d{1}))+)+(\\W+|_|$)(?!\\\\)",
        RegexOption.IGNORE_CASE
    ),

    // iTunes Season 1\05 Title (Quality).ext
    Regex(
        "^(?:Season(?:_|-|\\s|\\.)(?<season>(?<!\\d{1,50})\\d{1,2}(?!\\d+)))(?:_|-|\\s|\\.)(?<episode>(?<!\\d{1,50})\\d{1,2}(?!\\d+))",
        RegexOption.IGNORE_CASE
    ),

    // iTunes 1-05 Title (Quality).ext
    Regex(
        "^(?:(?<season>(?<!\\d{1,50})(?:\\d{1,2})(?!\\d+))(?:-(?<episode>\\d{2,3}(?!\\d+))))",
        RegexOption.IGNORE_CASE
    ),

    // Anime Range - Title Absolute Episode Number (ep01-12)
    Regex(
        "^(?:\\[(?<subgroup>.+?)\\][-_. ]?)?(?<title>.+?)(?:_|\\s|\\.)+(?:e|ep)(?<absoluteepisode>\\d{2,3}(\\.\\d{1,2})?)-(?<absoluteepisode1>(?<!\\d{1,50})\\d{1,2}(\\.\\d{1,2})?(?!\\d+|-)).*?(?<hash>\\[\\w{8}\\])?(?:\$|\\.)",
        RegexOption.IGNORE_CASE
    ),

    // Anime - Title Absolute Episode Number (e66)
    Regex(
        "^(?:\\[(?<subgroup>.+?)\\\\][-_. ]?)?(?<title>.+?)(?:(?:_|-|\\s|\\.)+(?:e|ep)(?<absoluteepisode>\\d{2,4}(\\.\\d{1,2})?))+.*?(?<hash>\\[\\w{8}\\\\])?(?:\$|\\.)",
        RegexOption.IGNORE_CASE
    ),

    // Anime - Title Episode Absolute Episode Number (Series Title Episode 01)
    Regex(
        "^(?<title>.+?)[-_. ](?:Episode)(?:[-_. ]+(?<absoluteepisode>(?<!\\d{1,50})\\d{2,3}(\\.\\d{1,2})?(?!\\d+)))+(?:_|-|\\s|\\.)*?(?<hash>\\[.{8}\\\\])?(?:\$|\\.)?",
        RegexOption.IGNORE_CASE
    ),

    // Anime Range - Title Absolute Episode Number (1 or 2 digit absolute episode numbers in a range, 1-10)
    Regex(
        "^(?:\\[(?<subgroup>.+?)\\][-_. ]?)?(?<title>.+?)[_. ]+(?<absoluteepisode>(?<!\\d{1,50})\\d{1,2}(\\.\\d{1,2})?(?!\\d+))-(?<absoluteepisode1>(?<!\\d{1,50})\\d{1,2}(\\.\\d{1,2})?(?!\\d+|-))(?:_|\\s|\\.)*?(?<hash>\\[.{8}\\])?(?:$|\\.)?",
        RegexOption.IGNORE_CASE
    ),

    // Anime - Title Absolute Episode Number
    Regex(
        "^(?:\\[(?<subgroup>.+?)\\][-_. ]?)?(?<title>.+?)(?:[-_. ]+(?<absoluteepisode>(?<!\\d{1,50})\\d{2,3}(\\.\\d{1,2})?(?!\\d+)))+(?:_|-|\\s|\\.)*?(?<hash>\\[.{8}\\])?(?:$|\\.)?",
        RegexOption.IGNORE_CASE
    ),

    // Anime - Title {Absolute Episode Number}
    Regex(
        "^(?:\\[(?<subgroup>.+?)\\\\][-_. ]?)?(?<title>.+?)(?:(?:[-_\\W](?<![\\\\(\\[!]))+(?<absoluteepisode>(?<!\\d{1,50})\\d{2,3}(\\.\\d{1,2})?(?!\\d+)))+(?:_|-|\\s|\\.)*?(?<hash>\\[.{8}\\\\])?(?:$|\\.)?",
        RegexOption.IGNORE_CASE
    ),

    // Extant, terrible multi-episode naming (extant.10708.hdtv-lol.mp4)
    Regex(
        "^(?<title>.+?)[-_. ](?<season>[0]?\\d?)(?:(?<episode>\\d{2}){2}(?!\\d+))[-_. ]",
        RegexOption.IGNORE_CASE
    ),
)

val rejectedRegexes = listOf(
    // Generic match for md5 and mixed-case hashes.
    Regex("^[0-9a-zA-Z]{32}", RegexOption.IGNORE_CASE),

    // Generic match for shorter lower-case hashes.
    Regex("^[a-z0-9]{24}$", RegexOption.IGNORE_CASE),

    // Format seen on some NZBGeek releases
    // Be very strict with these coz they are very close to the valid 101 ep numbering.
    Regex("^[A-Z]{11}\\d{3}$", RegexOption.IGNORE_CASE),
    Regex("^[a-z]{12}\\d{3}$", RegexOption.IGNORE_CASE),

    // Backup filename (Unknown origins)
    Regex("^Backup_\\d{5,}S\\d{2}-\\d{2}$", RegexOption.IGNORE_CASE),

    // 123 - Started appearing December 2014
    Regex("^123$", RegexOption.IGNORE_CASE),

    // abc - Started appearing January 2015
    Regex("^b00bs$\"", RegexOption.IGNORE_CASE),

    // 170424_26 - Started appearing August 2018
    Regex("^\\d{6}_\\d{2}$\"", RegexOption.IGNORE_CASE),
)

val requestInfoExp = Regex("^(?:\\[.+?\\])+")
val sixDigitAirDateMatchExp = Regex(
    "(?<=[_.-])(?<airdate>(?<!\\d)(?<airyear>[1-9]\\d{1})(?<airmonth>[0-1][0-9])(?<airday>[0-3][0-9]))(?=[_.-])",
    RegexOption.IGNORE_CASE
)

data class Season(
    val releaseTitle: String,
    val seriesTitle: String,
    val seasons: List<Int>,
    val episodeNumbers: List<Int>,
    val airDate: LocalDateTime?,
    val fullSeason: Boolean,
    val isPartialSeason: Boolean,
    val isMultiSeason: Boolean,
    val isSeasonExtra: Boolean,
    val isSpecial: Boolean,
    val seasonPart: Int
)

fun parseSeason(title: String): Season? {
    if (!preValidation(title)) {
        return null
    }

    var simpleTitle = simplifyTitle(title)

    val sixDigitAirDateMatch = sixDigitAirDateMatchExp.find(title)
    sixDigitAirDateMatch?.groups?.let { groups ->
        val airYear = groups.value("airyear")
        val airMonth = groups.value("airmonth")
        val airDay = groups.value("airday")
        if (airMonth != "00" || airDay != "00") {
            val fixedDate = "20$airYear.$airMonth.$airDay"
            simpleTitle = simpleTitle.replace(groups.value("airdate"), fixedDate)
        }
    }

    for (exp in reportTitleExp) {
        val match = exp.find(simpleTitle) ?: continue
        val result = parseMatchCollection(match, simpleTitle) ?: continue

        if (result.fullSeason == true &&
            result.releaseTokens != null && Regex("Special", RegexOption.IGNORE_CASE)
                .containsMatchIn(result.releaseTokens!!)
        ) {
            result.fullSeason = false
            result.isSpecial = true
        }

        return Season(
            releaseTitle = title,
            seriesTitle = result.seriesName,
            seasons = result.seasonNumbers ?: emptyList(),
            episodeNumbers = result.episodeNumbers ?: emptyList(),
            airDate = result.airDate,
            fullSeason = result.fullSeason ?: false,
            isPartialSeason = result.isPartialSeason ?: false,
            isMultiSeason = result.isMultiSeason ?: false,
            isSeasonExtra = result.isSeasonExtra ?: false,
            isSpecial = result.isSpecial ?: false,
            seasonPart = result.seasonPart ?: 0
        )
    }

    return null
}

fun preValidation(title: String): Boolean {
    for (exp in rejectedRegexes) {
        if (exp.containsMatchIn(title)) {
            return false
        }
    }
    return true
}

fun completeRange(arr: List<Int>): List<Int> {
    val uniqArr = arr.toSet().toList().sorted() // Remove duplicates and sort

    val first = uniqArr.firstOrNull() ?: return arr
    val last = uniqArr.lastOrNull() ?: return arr

    if (first > last) {
        return arr
    }

    return (first..last).toList()
}

fun indexOfEnd(str1: String, str2: String): Int {
    val io = str1.indexOf(str2)
    return if (io == -1) -1 else io + str2.length
}

data class ParsedMatchCollection(
    val seriesName: String,
    var seriesTitle: String? = null,
    var seasonNumbers: List<Int>? = null,
    var isMultiSeason: Boolean? = null,
    var episodeNumbers: List<Int>? = null,
    var isSpecial: Boolean? = null,
    var isSeasonExtra: Boolean? = null,
    var seasonPart: Int? = null,
    var isPartialSeason: Boolean? = null,
    var fullSeason: Boolean? = null,
    var airDate: LocalDateTime? = null,
    var releaseTokens: String? = null
)

@OptIn(ExperimentalTime::class)
fun parseMatchCollection(
    match: MatchResult,
    simpleTitle: String
): ParsedMatchCollection? {
    val groups = match.groups
    if (groups == null) {
        throw Exception("No match")
    }

    val title = groups.value("title")
    val seriesName = title
        .replace(".", " ")
        .replace("_", " ")
        .replace(requestInfoExp, "")
        .trim()

    val result = ParsedMatchCollection(seriesName = seriesName)

    var lastSeasonEpisodeStringIndex = indexOfEnd(simpleTitle, title)

    val airYear = groups.value("airyear").toIntOrNull() ?: 0
    if (airYear < 1900 || airYear == 0) {
        var seasons = listOfNotNull(groups.value("season"), groups.value("season1"))
            .filter { it.isNotEmpty() }
            .map {
                lastSeasonEpisodeStringIndex =
                    maxOf(indexOfEnd(simpleTitle, it), lastSeasonEpisodeStringIndex)
                it.toIntOrNull() ?: 0
            }

        if (seasons.size > 1) {
            seasons = completeRange(seasons)
        }

        result.seasonNumbers = seasons
        if (seasons.size > 1) {
            result.isMultiSeason = true
        }

        val episodeCaptures = listOfNotNull(groups.value("episode"), groups.value("episode1")).filter { it.isNotEmpty() }
        val absoluteEpisodeCaptures =
            listOfNotNull(groups.value("absoluteepisode"), groups.value("absoluteepisode1")).filter { it.isNotEmpty() }

        if (episodeCaptures.isNotEmpty()) {
            val first = episodeCaptures.first().toIntOrNull() ?: 0
            val last = episodeCaptures.last().toIntOrNull() ?: 0

            if (first > last) {
                return null
            }

            val count = last - first + 1
            result.episodeNumbers = (0 until count).map { it + first }
        }

        if (absoluteEpisodeCaptures.isNotEmpty()) {
            val first = absoluteEpisodeCaptures.first().toIntOrNull() ?: 0
            val last = absoluteEpisodeCaptures.last().toIntOrNull() ?: 0

            if (first % 1 != 0 || last % 1 != 0) {
                if (absoluteEpisodeCaptures.size != 1) {
                    return null
                }

                result.episodeNumbers = listOf(first)
                result.isSpecial = true

                lastSeasonEpisodeStringIndex = maxOf(
                    indexOfEnd(simpleTitle, absoluteEpisodeCaptures[0]),
                    lastSeasonEpisodeStringIndex
                )
            } else {
                val count = last - first + 1
                result.episodeNumbers = (0 until count).map { it + first }

                if (groups.value("special").isNotEmpty()) {
                    result.isSpecial = true
                }
            }
        }

        if (episodeCaptures.isEmpty() && absoluteEpisodeCaptures.isEmpty()) {
            if (groups.value("extras").isNotEmpty()) {
                result.isSeasonExtra = true
            }

            val seasonPart = groups.value("seasonpart")
            if (seasonPart.isNotEmpty()) {
                result.seasonPart = seasonPart.toIntOrNull() ?: 0
                result.isPartialSeason = true
            } else {
                result.fullSeason = true
            }
        }

        if (absoluteEpisodeCaptures.isNotEmpty() && result.episodeNumbers == null) {
            result.seasonNumbers = listOf(0)
        }
    } else {
        var airMonth = groups.value("airmonth").toIntOrNull() ?: 0
        var airDay = groups.value("airday").toIntOrNull() ?: 0

        if (airMonth > 12) {
            val tempDay = airDay
            airDay = airMonth
            airMonth = tempDay
        }

//        val airDate = Date(airYear - 1900, airMonth - 1, airDay)
        val airDate = LocalDateTime(year = airYear, month = airMonth, day = airDay, hour = 0, minute = 0)
        val nowDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val date1970 = LocalDateTime(year = 1970, month = 1, day = 1, hour = 0, minute = 0)


        if (airDate > nowDate) {
            throw Exception("Parsed date is in the future")
        }

        if (airDate < date1970) {
            throw Exception("Parsed date error")
        }

        lastSeasonEpisodeStringIndex = maxOf(
            indexOfEnd(simpleTitle, groups.value("airyear")),
            lastSeasonEpisodeStringIndex
        )
        lastSeasonEpisodeStringIndex = maxOf(
            indexOfEnd(simpleTitle, groups.value("airmonth")),
            lastSeasonEpisodeStringIndex
        )
        lastSeasonEpisodeStringIndex = maxOf(
            indexOfEnd(simpleTitle, groups.value("airday")),
            lastSeasonEpisodeStringIndex
        )
        result.airDate = airDate
    }

    result.releaseTokens =
        if (lastSeasonEpisodeStringIndex == simpleTitle.length || lastSeasonEpisodeStringIndex == -1) {
            simpleTitle
        } else {
            simpleTitle.substring(lastSeasonEpisodeStringIndex)
        }

    result.seriesTitle = seriesName

    return result
}

fun MatchGroupCollection.value(key: String): String {
    return try {
        this[key]?.value ?: ""
    } catch (e: Exception) {
        ""
    }
}