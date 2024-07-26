package com.dik.uikit.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import torrservermedia.core.uikit.generated.resources.Res
import torrservermedia.core.uikit.generated.resources.opensans_bold
import torrservermedia.core.uikit.generated.resources.opensans_bolditalic
import torrservermedia.core.uikit.generated.resources.opensans_italic
import torrservermedia.core.uikit.generated.resources.opensans_regular

data class AppTypography(
    val toolbar: TextStyle,
    val normalText: TextStyle,
    val normalBoldText: TextStyle,
    val normalItalicText: TextStyle,
    val normalBoldItalicText: TextStyle,
    val smallText: TextStyle,
    val smallBoldText: TextStyle,
    val smallItalicText: TextStyle,
    val smallBoldItalicText: TextStyle
)

val LocalAppTypography =
    staticCompositionLocalOf<AppTypography> { error("Typography not provided") }

@Composable
private fun AppFontFamily() = FontFamily(
    Font(Res.font.opensans_regular, FontWeight.Normal, FontStyle.Normal),
    Font(Res.font.opensans_bold, FontWeight.Bold, FontStyle.Normal),
    Font(Res.font.opensans_italic, FontWeight.Normal, FontStyle.Italic),
    Font(Res.font.opensans_bolditalic, FontWeight.Bold, FontStyle.Italic),
)

@Composable
private fun ToolbarLigth() = NormalTextLigth().copy(
    fontSize = 20.sp
)

@Composable
private fun ToolbarDark() = ToolbarLigth()

@Composable
private fun NormalTextLigth() = TextStyle(
    fontFamily = AppFontFamily(),
    fontSize = 16.sp
)

@Composable
private fun NormalTextDark() = NormalTextLigth()

@Composable
private fun NormalBoldTextLigth() = NormalTextLigth().copy(fontWeight = FontWeight.Bold)

@Composable
private fun NormalBoldTextDark() = NormalBoldTextLigth()

@Composable
private fun NormalItalicTextLigth() = NormalTextLigth().copy(fontStyle = FontStyle.Italic)

@Composable
private fun NormalItalicTextDark() = NormalItalicTextLigth()

@Composable
private fun NormalBoldItalicTextLigth() =
    NormalTextLigth().copy(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic)

@Composable
private fun NormalBoldItalicTextDark() = NormalBoldItalicTextLigth()

@Composable
private fun SmallTextLigth() = NormalTextLigth().copy(fontSize = 12.sp)

@Composable
private fun SmallTextDark() = SmallTextLigth()

@Composable
private fun SmallBoldTextLigth() = SmallTextLigth().copy(fontWeight = FontWeight.Bold)

@Composable
private fun SmallBoldTextDark() = SmallBoldTextLigth()

@Composable
private fun SmallItalicTextLigth() = SmallTextLigth().copy(fontStyle = FontStyle.Italic)

@Composable
private fun SmallItalicTextDark() = SmallItalicTextLigth()

@Composable
private fun SmallBoldItalicTextLigth() =
    SmallTextLigth().copy(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic)

@Composable
private fun SmallBoldItalicTextDark() = SmallBoldItalicTextLigth()

@Composable
internal fun AppTypographyLight() = AppTypography(
    toolbar = ToolbarLigth(),
    normalText = NormalTextLigth(),
    normalBoldText = NormalBoldTextLigth(),
    normalItalicText = NormalItalicTextLigth(),
    normalBoldItalicText = NormalBoldItalicTextLigth(),
    smallText = SmallTextLigth(),
    smallBoldText = SmallBoldTextLigth(),
    smallItalicText = SmallItalicTextLigth(),
    smallBoldItalicText = SmallBoldItalicTextLigth()
)

@Composable
internal fun AppTypographyDark() = AppTypography(
    toolbar = ToolbarDark(),
    normalText = NormalTextDark(),
    normalBoldText = NormalBoldTextDark(),
    normalItalicText = NormalItalicTextDark(),
    normalBoldItalicText = NormalBoldItalicTextDark(),
    smallText = SmallTextDark(),
    smallBoldText = SmallBoldTextDark(),
    smallItalicText = SmallItalicTextDark(),
    smallBoldItalicText = SmallBoldItalicTextDark()
)