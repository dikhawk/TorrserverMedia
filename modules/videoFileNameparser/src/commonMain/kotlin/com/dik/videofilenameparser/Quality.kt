import com.dik.videofilenameparser.Resolution
import com.dik.videofilenameparser.Source
import com.dik.videofilenameparser.VideoCodec
import com.dik.videofilenameparser.parseResolution
import com.dik.videofilenameparser.parseSource
import com.dik.videofilenameparser.parseSourceGroups
import com.dik.videofilenameparser.parseVideoCodec
import java.util.Locale


enum class QualityModifier(val value: String) {
    REMUX("REMUX"), BRDISK("BRDISK"), RAWHD("RAWHD")
}

data class Revision(
    var version: Int = 1, var real: Int = 0
)

data class QualityModel(
    var sources: List<Source> = emptyList(),
    var modifier: QualityModifier? = null,
    var resolution: Resolution? = null,
    var revision: Revision
)

val properRegex = Regex("\\b(proper|repack|rerip)\\b", RegexOption.IGNORE_CASE)
val realRegex = Regex("\\bREAL\\b")
val versionExp = Regex("(v\\d\\b|\\[v\\d])", RegexOption.IGNORE_CASE)
val remuxExp = Regex("\\b(BD|UHD)?Remux\\b", RegexOption.IGNORE_CASE)
val bdiskExp = Regex("\\b(COMPLETE|ISO|BDISO|BDMux|BD25|BD50|BR.?DISK)\\b", RegexOption.IGNORE_CASE)
val rawHdExp =
    Regex("\\b(RawHD|1080i[-_. ]HDTV|Raw[-_. ]HD|MPEG[-_. ]?2)\\b", RegexOption.IGNORE_CASE)
val highDefPdtvRegex = Regex("hr[-_. ]ws", RegexOption.IGNORE_CASE)

fun parseQualityModifiers(title: String): Revision {
    val normalizedTitle = title.trim().replace("_", " ").lowercase(Locale.getDefault())
    val result = Revision()

    if (properRegex.containsMatchIn(normalizedTitle)) {
        result.version = 2
    }

    val versionResult = versionExp.find(normalizedTitle)
    versionResult?.groups?.get(1)?.value?.let {
        val digits = Regex("\\d").find(it)?.value
        digits?.toIntOrNull()?.let { value ->
            result.version = value
        }
    }

    val realCount = realRegex.findAll(title).count()
    result.real = realCount

    return result
}

fun parseQuality(title: String): QualityModel {
    val normalizedTitle = title.trim()
        .replace("_", " ")
        .replace("[", " ")
        .replace("]", " ")
        .trim()
        .lowercase(Locale.getDefault())

    val revision = parseQualityModifiers(title)
    val resolution = parseResolution(normalizedTitle).resolution
    val sourceGroups = parseSourceGroups(normalizedTitle)
    val source = parseSource(normalizedTitle)
    val codec = parseVideoCodec(title).codec

    val result = QualityModel(
        sources = source, resolution = resolution, revision = revision
    )

    if (bdiskExp.containsMatchIn(normalizedTitle) && sourceGroups.bluray) {
        result.modifier = QualityModifier.BRDISK
        result.sources = listOf(Source.BLURAY)
    } else if (remuxExp.containsMatchIn(normalizedTitle) && !sourceGroups.webdl && !sourceGroups.hdtv) {
        result.modifier = QualityModifier.REMUX
        result.sources = listOf(Source.BLURAY)
    } else if (rawHdExp.containsMatchIn(normalizedTitle) && result.modifier != QualityModifier.BRDISK && result.modifier != QualityModifier.REMUX) {
        result.modifier = QualityModifier.RAWHD
        result.sources = listOf(Source.TV)
    }

    when {
        sourceGroups.bluray -> {
            result.sources = listOf(Source.BLURAY)
            if (codec == VideoCodec.XVID) {
                result.resolution = Resolution.R480P
                result.sources = listOf(Source.DVD)
            } else if (result.resolution == null) {
                result.resolution = Resolution.R720P
            }

            if (result.resolution == null && result.modifier == QualityModifier.BRDISK) {
                result.resolution = Resolution.R1080P
            } else if (result.resolution == null && result.modifier == QualityModifier.REMUX) {
                result.resolution = Resolution.R2160P
            }
            return result
        }

        sourceGroups.webdl || sourceGroups.webrip -> {
            result.sources = source
            result.resolution = resolution

            if (resolution == Resolution.UNKNOWN) {
                result.resolution = if (title.contains("[WEBDL]")) Resolution.R720P else Resolution.R480P
            }
            return result
        }

        sourceGroups.hdtv -> {
            result.sources = listOf(Source.TV)
            result.resolution = resolution

            if (resolution == Resolution.UNKNOWN) {
                result.resolution = if (title.contains("[HDTV]")) Resolution.R720P else Resolution.R480P
            }
            return result
        }

        sourceGroups.pdtv || sourceGroups.sdtv || sourceGroups.dsr || sourceGroups.tvrip -> {
            result.sources = listOf(Source.TV)
            result.resolution =
                if (highDefPdtvRegex.containsMatchIn(normalizedTitle)) Resolution.R720P else Resolution.R480P
            return result
        }

        sourceGroups.bdrip || sourceGroups.brrip -> {
            if (codec == VideoCodec.XVID) {
                result.resolution = Resolution.R480P
                result.sources = listOf(Source.DVD)
            } else {
                result.resolution = Resolution.R480P
                result.sources = listOf(Source.BLURAY)
            }
            return result
        }

        sourceGroups.workprint -> {
            result.sources = listOf(Source.WORKPRINT)
            return result
        }

        sourceGroups.cam -> {
            result.sources = listOf(Source.CAM)
            return result
        }

        sourceGroups.ts -> {
            result.sources = listOf(Source.TELESYNC)
            return result
        }

        sourceGroups.tc -> {
            result.sources = listOf(Source.TELECINE)
            return result
        }
    }

    if (result.modifier == null && (resolution == Resolution.R2160P || resolution == Resolution.R1080P || resolution == Resolution.R720P)) {
        result.sources = listOf(Source.WEBDL)
    }

    return result
}