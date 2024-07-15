package com.dik.uikit.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import torrservermedia.core.uikit.generated.resources.OpenSans_Bold
import torrservermedia.core.uikit.generated.resources.OpenSans_BoldItalic
import torrservermedia.core.uikit.generated.resources.OpenSans_Italic
import torrservermedia.core.uikit.generated.resources.OpenSans_Regular
import torrservermedia.core.uikit.generated.resources.Res

data class AppTypography(
    val toolbar: TextStyle,
    val normalText: TextStyle,
    val normalBoldText: TextStyle,
    val normalItalicText: TextStyle,
    val normalBoldItalicText: TextStyle
)

val LocalAppTypography =
    staticCompositionLocalOf<AppTypography> { error("Typography not provided") }

@Composable
private fun AppFontFamily() = FontFamily(
    Font(Res.font.OpenSans_Regular, FontWeight.Normal, FontStyle.Normal),
    Font(Res.font.OpenSans_Bold, FontWeight.Bold, FontStyle.Normal),
    Font(Res.font.OpenSans_Italic, FontWeight.Normal, FontStyle.Italic),
    Font(Res.font.OpenSans_BoldItalic, FontWeight.Bold, FontStyle.Italic),
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
internal fun AppTypographyLight() = AppTypography(
    toolbar = ToolbarLigth(),
    normalText = NormalTextLigth(),
    normalBoldText = NormalBoldTextLigth(),
    normalItalicText = NormalItalicTextLigth(),
    normalBoldItalicText = NormalBoldItalicTextLigth()
)

@Composable
internal fun AppTypographyDark() = AppTypography(
    toolbar = ToolbarDark(),
    normalText = NormalTextDark(),
    normalBoldText = NormalBoldTextDark(),
    normalItalicText = NormalItalicTextDark(),
    normalBoldItalicText = NormalBoldItalicTextDark()
)