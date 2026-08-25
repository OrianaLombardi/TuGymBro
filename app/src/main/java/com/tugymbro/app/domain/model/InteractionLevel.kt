package com.tugymbro.app.domain.model

import androidx.compose.ui.graphics.Color
import com.tugymbro.app.ui.theme.PlateBlue
import com.tugymbro.app.ui.theme.PlateGreen
import com.tugymbro.app.ui.theme.PlateRed
import com.tugymbro.app.ui.theme.PlateYellow

/**
 * Nivel de interacción social deseado durante el entrenamiento.
 * Cada nivel se mapea 1 a 1 con un color real de disco de powerlifting
 * (ver documento de diseño v3), para que el color no sea decorativo sino
 * un código que el usuario ya reconoce del gimnasio.
 */
enum class InteractionLevel(
    val label: String,
    val description: String,
    val plateColor: Color,
    val plateKg: Int
) {
    SILENCIOSO(
        label = "Silencioso",
        description = "Cerca, sin charla. Compañía sin presión.",
        plateColor = PlateGreen,
        plateKg = 10
    ),
    SPOTTER(
        label = "Solo spotter",
        description = "Ayuda en series pesadas, nada más.",
        plateColor = PlateRed,
        plateKg = 25
    ),
    CHARLA(
        label = "Charla ligera",
        description = "Algo de conversación entre series.",
        plateColor = PlateBlue,
        plateKg = 20
    ),
    FULL_TRAINING(
        label = "Full training",
        description = "Rutina armada juntos, de punta a punta.",
        plateColor = PlateYellow,
        plateKg = 15
    )
}
