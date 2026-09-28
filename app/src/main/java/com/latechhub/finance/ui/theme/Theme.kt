package com.latechhub.finance.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = LaTechBlue,
    secondary = LaTechOrange,
    tertiary = LaTechOrangeDark,
    background = LaTechBlack,
    surface = LaTechBlack,
    onPrimary = LaTechBlack,
    onSecondary = LaTechBlack,
    onTertiary = LaTechWhite,
    onBackground = LaTechWhite,
    onSurface = LaTechWhite
)

private val LightColorScheme = lightColorScheme(
    primary = LaTechBlue,
    secondary = LaTechOrange,
    tertiary = LaTechOrangeDark,
    background = LaTechWhite,
    surface = LaTechWhite,
    onPrimary = LaTechBlack,
    onSecondary = LaTechBlack,
    onTertiary = LaTechWhite,
    onBackground = LaTechBlack,
    onSurface = LaTechBlack
)

@Composable
fun FinanceTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
