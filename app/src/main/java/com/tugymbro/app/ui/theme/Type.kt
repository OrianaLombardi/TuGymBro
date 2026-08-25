package com.tugymbro.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/*
 * NOTA SOBRE TIPOGRAFÍA
 * ----------------------
 * El diseño aprobado usa "Allerta Stencil" (títulos, estilo troquelado de placa)
 * y "Barlow Condensed" (texto e interfaz, estilo atlético condensado).
 * Por defecto este proyecto usa las fuentes del sistema para que compile sin
 * pasos extra. Para usar las fuentes reales de la marca:
 *
 * 1) Agregar en app/build.gradle.kts:
 *    implementation("androidx.compose.ui:ui-text-google-fonts:<version>")
 * 2) Crear res/values/font_certs.xml con el certificado de Google Fonts.
 * 3) Reemplazar FontFamily.Default de acá abajo por un GoogleFont(
 *    "Allerta Stencil") y GoogleFont("Barlow Condensed") respectivamente.
 */

val DisplayFontFamily = FontFamily.Default // -> reemplazar por Allerta Stencil
val BodyFontFamily = FontFamily.Default    // -> reemplazar por Barlow Condensed

val TuGymBroTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = DisplayFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp,
        letterSpacing = 0.5.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = DisplayFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        letterSpacing = 0.5.sp
    ),
    titleLarge = TextStyle(
        fontFamily = BodyFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp
    ),
    titleMedium = TextStyle(
        fontFamily = BodyFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = BodyFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 21.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = BodyFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp
    ),
    labelSmall = TextStyle(
        fontFamily = BodyFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp,
        letterSpacing = 0.6.sp
    )
)
