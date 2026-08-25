package com.tugymbro.app.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tugymbro.app.domain.model.NearbyMatch
import com.tugymbro.app.ui.components.HazardDivider
import com.tugymbro.app.ui.components.PlateBadge
import com.tugymbro.app.ui.theme.Chalk
import com.tugymbro.app.ui.theme.ChalkDim
import com.tugymbro.app.ui.theme.Iron
import com.tugymbro.app.ui.theme.IronAlt
import com.tugymbro.app.ui.theme.PlateYellow
import com.tugymbro.app.ui.theme.SteelLine

@Composable
fun HomeScreen(
    onMatchSelected: (NearbyMatch) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Iron)
    ) {
        Column(modifier = Modifier.padding(20.dp, 32.dp, 20.dp, 10.dp)) {
            Text("TU BARRA", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Chalk)
            Text(
                text = "${uiState.matches.size} personas compatibles cerca",
                fontSize = 12.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = ChalkDim,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        HazardDivider(thickness = 4.dp)

        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            if (uiState.isLoading) {
                CircularProgressIndicator(color = PlateYellow)
            } else {
                BarbellQueue(
                    matches = uiState.matches,
                    onPlateClick = onMatchSelected
                )
            }
        }
    }
}

/**
 * Elemento distintivo de la marca: una barra olímpica cargada con discos.
 * Cada disco es una persona compatible cerca. El tamaño del disco es
 * inversamente proporcional a la distancia (más cerca = más grande),
 * y el color corresponde a su nivel de interacción (ver InteractionLevel).
 */
@Composable
private fun BarbellQueue(
    matches: List<NearbyMatch>,
    onPlateClick: (NearbyMatch) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "CADA DISCO ES ALGUIEN CERCA TUYO",
            fontSize = 10.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = ChalkDim,
            modifier = Modifier.padding(bottom = 18.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp),
            contentAlignment = Alignment.Center
        ) {
            // Barra y manguitos
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.82f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(SteelLine)
            )
            Row(
                modifier = Modifier.fillMaxWidth(0.9f),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Sleeve()
                Sleeve()
            }

            // Discos cargados en el centro
            Row(
                horizontalArrangement = Arrangement.spacedBy((-4).dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val maxDistance = (matches.maxOfOrNull { it.profile.distanceMeters } ?: 1).coerceAtLeast(1)
                matches.take(6).forEach { match ->
                    val proximityRatio = 1f - (match.profile.distanceMeters.toFloat() / maxDistance) * 0.55f
                    val plateSize = (34 + 26 * proximityRatio).dp
                    PlateBadge(
                        color = match.profile.interactionLevel.plateColor,
                        size = plateSize,
                        modifier = Modifier.clickable { onPlateClick(match) }
                    )
                }
            }
        }

        Text(
            text = "${matches.size} DISCOS CARGADOS",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Chalk,
            modifier = Modifier.padding(top = 22.dp)
        )
        Text(
            text = "Más grande y cerca del centro = más cerca tuyo.\nTocá un disco para ver el perfil.",
            fontSize = 12.sp,
            color = ChalkDim,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp, start = 32.dp, end = 32.dp)
        )
    }
}

@Composable
private fun Sleeve() {
    Box(
        modifier = Modifier
            .size(16.dp)
            .clip(CircleShape)
            .background(IronAlt)
    )
}
