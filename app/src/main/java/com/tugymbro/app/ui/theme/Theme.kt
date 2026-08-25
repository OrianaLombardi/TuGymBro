package com.tugymbro.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

// La app siempre corre en modo oscuro por diseño (estética "fierro/cemento"),
// independientemente del tema del sistema.
private val TuGymBroColorScheme = darkColorScheme(
    primary = PlateYellow,
    onPrimary = Iron,
    secondary = PlateRed,
    onSecondary = Chalk,
    tertiary = PlateBlue,
    background = Concrete,
    onBackground = Chalk,
    surface = IronAlt,
    onSurface = Chalk,
    surfaceVariant = ConcreteAlt,
    onSurfaceVariant = ChalkDim,
    outline = SteelLine,
    error = PlateRed,
    onError = Chalk
)

@Composable
fun TuGymBroTheme(
    // parámetro reservado por si más adelante se agrega modo claro
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = TuGymBroColorScheme,
        typography = TuGymBroTypography,
        content = content
    )
}
