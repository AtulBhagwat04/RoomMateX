package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = ElectricCyan,
    onPrimary = Color.Black,
    primaryContainer = DeepViolet,
    onPrimaryContainer = Color.White,
    secondary = ElectricViolet,
    onSecondary = Color.White,
    secondaryContainer = DarkSurfaceVariant,
    onSecondaryContainer = ElectricCyan,
    tertiary = GoldXp,
    onTertiary = Color.Black,
    background = DarkBackground,
    onBackground = Color(0xFFF0EFF8),
    surface = DarkSurface,
    onSurface = Color(0xFFF0EFF8),
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFC7C5DC),
    outline = DarkCardBorder,
    error = CoralRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = DeepViolet,
    onPrimary = Color.White,
    primaryContainer = ElectricCyan.copy(alpha = 0.2f),
    onPrimaryContainer = DeepViolet,
    secondary = ElectricViolet,
    onSecondary = Color.White,
    tertiary = AmberOrange,
    background = LightBackground,
    onBackground = Color(0xFF161524),
    surface = LightSurface,
    onSurface = Color(0xFF161524),
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = Color(0xFF4A4765),
    outline = Color(0xFFD0CDDF),
    error = CoralRed
)

@Composable
fun RoomMateXTheme(
    darkTheme: Boolean = true, // Default dark-first as per prompt requirements
    dynamicColor: Boolean = false, // Preserve original RoomMateX electric branding
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Alias for backwards compatibility
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    RoomMateXTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}
