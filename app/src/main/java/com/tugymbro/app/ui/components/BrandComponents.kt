package com.tugymbro.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tugymbro.app.ui.theme.Iron

/**
 * Disco de powerlifting usado como badge visual: representa un nivel de
 * interacción (ver InteractionLevel) o, en HomeScreen, a una persona
 * cercana compatible.
 */
@Composable
fun PlateBadge(
    color: Color,
    size: Dp,
    modifier: Modifier = Modifier,
    holeColor: Color = Iron
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(color)
            .border(width = (size.value * 0.06f).dp, color = Color.Black.copy(alpha = 0.35f), shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(size * 0.32f)
                .clip(CircleShape)
                .background(holeColor)
        )
    }
}

/**
 * Franja de cinta de peligro (amarilla/negra), como el borde de una
 * plataforma de levantamiento. Se usa como separador de secciones en vez
 * de una línea simple — es el elemento de marca más reconocible.
 */
@Composable
fun HazardDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = 6.dp
) {
    val yellow = com.tugymbro.app.ui.theme.PlateYellow
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(thickness)
            .background(
                Brush.linearGradient(
                    colors = listOf(yellow, Iron, yellow, Iron),
                    start = Offset.Zero,
                    end = Offset(60f, 60f),
                    tileMode = androidx.compose.ui.graphics.TileMode.Repeated
                )
            )
    )
}
