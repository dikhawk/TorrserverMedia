package com.dik.videofilenameparser

enum class Language(val value: String) {
    English("English"),
    French("French"),
    Spanish("Spanish"),
    German("German"),
    Italian("Italian"),
    Danish("Danish"),
    Dutch("Dutch"),
    Japanese("Japanese"),
    Cantonese("Cantonese"),
    Mandarin("Mandarin"),
    Russian("Russian"),
    Polish("Polish"),
    Vietnamese("Vietnamese"),
    Nordic("Nordic"),
    Swedish("Swedish"),
    Norwegian("Norwegian"),
    Finnish("Finnish"),
    Turkish("Turkish"),
    Portuguese("Portuguese"),
    Flemish("Flemish"),
    Greek("Greek"),
    Korean("Korean"),
    Hungarian("Hungarian"),
    Persian("Persian"),
    Bengali("Bengali"),
    Bulgarian("Bulgarian"),
    Brazilian("Brazilian"),
    Hebrew("Hebrew"),
    Czech("Czech"),
    Ukrainian("Ukrainian"),
    Catalan("Catalan"),
    Chinese("Chinese"),
    Thai("Thai"),
    Hindi("Hindi"),
    Tamil("Tamil"),
    Arabic("Arabic"),
    Estonian("Estonian"),
    Icelandic("Icelandic"),
    Latvian("Latvian"),
    Lithuanian("Lithuanian"),
    Romanian("Romanian"),
    Slovak("Slovak"),
    Serbian("Serbian")
}

fun parseLanguage(title: String): Set<Language> {
    val parsedTitle = parseTitleAndYear(title).title
    val languageTitle = title.replace(".", " ").replace(parsedTitle, "").lowercase()
    val languages = mutableSetOf<Language>()

    if (Regex("\\b(english|eng|EN|FI)\\b", RegexOption.IGNORE_CASE).containsMatchIn(languageTitle)) {
        languages.add(Language.English)
    }

    if (languageTitle.contains("spanish")) {
        languages.add(Language.Spanish)
    }

    if (Regex("\\b(DK|DAN|danish)\\b", RegexOption.IGNORE_CASE).containsMatchIn(languageTitle)) {
        languages.add(Language.Danish)
    }

    if (languageTitle.contains("japanese")) {
        languages.add(Language.Japanese)
    }

    if (languageTitle.contains("cantonese")) {
        languages.add(Language.Cantonese)
    }

    if (languageTitle.contains("mandarin")) {
        languages.add(Language.Mandarin)
    }

    if (languageTitle.contains("korean")) {
        languages.add(Language.Korean)
    }

    if (languageTitle.contains("vietnamese")) {
        languages.add(Language.Vietnamese)
    }

    if (Regex("\\b(SE|SWE|swedish)\\b", RegexOption.IGNORE_CASE).containsMatchIn(languageTitle)) {
        languages.add(Language.Swedish)
    }

    if (languageTitle.contains("finnish")) {
        languages.add(Language.Finnish)
    }

    if (languageTitle.contains("turkish")) {
        languages.add(Language.Turkish)
    }

    if (languageTitle.contains("portuguese")) {
        languages.add(Language.Portuguese)
    }

    if (languageTitle.contains("hebrew")) {
        languages.add(Language.Hebrew)
    }

    if (languageTitle.contains("czech")) {
        languages.add(Language.Czech)
    }

    if (languageTitle.contains("ukrainian")) {
        languages.add(Language.Ukrainian)
    }

    if (languageTitle.contains("catalan")) {
        languages.add(Language.Catalan)
    }

    if (languageTitle.contains("estonian")) {
        languages.add(Language.Estonian)
    }

    if (Regex("\\b(ice|Icelandic)\\b", RegexOption.IGNORE_CASE).containsMatchIn(languageTitle)) {
        languages.add(Language.Icelandic)
    }

    if (Regex("\\b(chi|chinese)\\b", RegexOption.IGNORE_CASE).containsMatchIn(languageTitle)) {
        languages.add(Language.Chinese)
    }

    if (languageTitle.contains("thai")) {
        languages.add(Language.Thai)
    }

    if (Regex("\\b(ita|italian)\\b", RegexOption.IGNORE_CASE).containsMatchIn(languageTitle)) {
        languages.add(Language.Italian)
    }

    if (Regex("\\b(german|videomann)\\b", RegexOption.IGNORE_CASE).containsMatchIn(languageTitle)) {
        languages.add(Language.German)
    }

    if (Regex("\\b(flemish)\\b", RegexOption.IGNORE_CASE).containsMatchIn(languageTitle)) {
        languages.add(Language.Flemish)
    }

    if (Regex("\\b(greek)\\b", RegexOption.IGNORE_CASE).containsMatchIn(languageTitle)) {
        languages.add(Language.Greek)
    }

    if (Regex("\\b(FR|FRENCH|VOSTFR|VO|VFF|VFQ|VF2|TRUEFRENCH|SUBFRENCH)\\b", RegexOption.IGNORE_CASE).containsMatchIn(languageTitle)) {
        languages.add(Language.French)
    }

    if (Regex("\\b(russian|rus)\\b", RegexOption.IGNORE_CASE).containsMatchIn(languageTitle)) {
        languages.add(Language.Russian)
    }

    if (Regex("\\b(norwegian|NO)\\b", RegexOption.IGNORE_CASE).containsMatchIn(languageTitle)) {
        languages.add(Language.Norwegian)
    }

    if (Regex("\\b(HUNDUB|HUN|hungarian)\\b", RegexOption.IGNORE_CASE).containsMatchIn(languageTitle)) {
        languages.add(Language.Hungarian)
    }

    if (Regex("\\b(HebDub)\\b", RegexOption.IGNORE_CASE).containsMatchIn(languageTitle)) {
        languages.add(Language.Hebrew)
    }

    if (Regex("\\b(CZ|SK)\\b", RegexOption.IGNORE_CASE).containsMatchIn(languageTitle)) {
        languages.add(Language.Czech)
    }

    if (Regex("(?<ukrainian>\\bukr\\b)", RegexOption.IGNORE_CASE).containsMatchIn(languageTitle)) {
        languages.add(Language.Ukrainian)
    }

    if (Regex("\\b(PL|PLDUB|POLISH)\\b", RegexOption.IGNORE_CASE).containsMatchIn(languageTitle)) {
        languages.add(Language.Polish)
    }

    if (Regex("\\b(nl|dutch)\\b", RegexOption.IGNORE_CASE).containsMatchIn(languageTitle)) {
        languages.add(Language.Dutch)
    }

    if (Regex("\\b(HIN|Hindi)\\b", RegexOption.IGNORE_CASE).containsMatchIn(languageTitle)) {
        languages.add(Language.Hindi)
    }

    if (Regex("\\b(TAM|Tamil)\\b", RegexOption.IGNORE_CASE).containsMatchIn(languageTitle)) {
        languages.add(Language.Tamil)
    }

    if (Regex("\\b(Arabic)\\b", RegexOption.IGNORE_CASE).containsMatchIn(languageTitle)) {
        languages.add(Language.Arabic)
    }

    if (Regex("\\b(Latvian)\\b", RegexOption.IGNORE_CASE).containsMatchIn(languageTitle)) {
        languages.add(Language.Latvian)
    }

    if (Regex("\\b(Lithuanian)\\b", RegexOption.IGNORE_CASE).containsMatchIn(languageTitle)) {
        languages.add(Language.Lithuanian)
    }

    if (Regex("\\b(RO|Romanian|rodubbed)\\b", RegexOption.IGNORE_CASE).containsMatchIn(languageTitle)) {
        languages.add(Language.Romanian)
    }

    if (Regex("\\b(SK|Slovak)\\b", RegexOption.IGNORE_CASE).containsMatchIn(languageTitle)) {
        languages.add(Language.Slovak)
    }

    if (Regex("\\b(Brazilian)\\b", RegexOption.IGNORE_CASE).containsMatchIn(languageTitle)) {
        languages.add(Language.Brazilian)
    }

    if (Regex("\\b(Persian)\\b", RegexOption.IGNORE_CASE).containsMatchIn(languageTitle)) {
        languages.add(Language.Persian)
    }

    if (Regex("\\b(Bengali)\\b", RegexOption.IGNORE_CASE).containsMatchIn(languageTitle)) {
        languages.add(Language.Bengali)
    }

    if (Regex("\\b(Bulgarian)\\b", RegexOption.IGNORE_CASE).containsMatchIn(languageTitle)) {
        languages.add(Language.Bulgarian)
    }

    if (Regex("\\b(Serbian)\\b", RegexOption.IGNORE_CASE).containsMatchIn(languageTitle)) {
        languages.add(Language.Serbian)
    }

    if (Regex("\\b(nordic|NORDICSUBS)\\b", RegexOption.IGNORE_CASE).containsMatchIn(languageTitle)) {
        languages.add(Language.Nordic)
    }

    if (isMulti(languageTitle)) {
        languages.add(Language.English)
    }

    if (languages.isEmpty()) {
        languages.add(Language.English)
    }

    return languages
}

val multiExp = Regex("(?<!(WEB-))\\b(MULTi|DUAL|DL)\\b", RegexOption.IGNORE_CASE)
fun isMulti(title: String): Boolean {
    val noWebTitle = title.replace("\\bWEB-?DL\\b".toRegex(RegexOption.IGNORE_CASE), "")
    return multiExp.containsMatchIn(noWebTitle)
}