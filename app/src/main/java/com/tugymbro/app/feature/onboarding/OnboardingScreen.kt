package com.tugymbro.app.feature.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tugymbro.app.domain.model.InteractionLevel
import com.tugymbro.app.ui.components.HazardDivider
import com.tugymbro.app.ui.components.PlateBadge
import com.tugymbro.app.ui.theme.ChalkDim
import com.tugymbro.app.ui.theme.ConcreteAlt
import com.tugymbro.app.ui.theme.Iron
import com.tugymbro.app.ui.theme.PlateYellow
import com.tugymbro.app.ui.theme.SteelLine

@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Iron)
            .padding(bottom = 24.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp, 32.dp, 20.dp, 12.dp)) {
            Text(
                text = "¿CÓMO ENTRENÁS?",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = com.tugymbro.app.ui.theme.Chalk
            )
            Text(
                text = "Elegí tu nivel de interacción",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = ChalkDim,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        HazardDivider(modifier = Modifier.padding(bottom = 16.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            InteractionLevel.entries.forEach { level ->
                LevelCard(
                    level = level,
                    selected = level == uiState.selectedLevel,
                    onClick = { viewModel.onLevelSelected(level) }
                )
            }
        }

        Text(
            text = "CONTINUAR",
            color = Iron,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 20.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(PlateYellow)
                .clickable { viewModel.onContinueClicked(onFinished) }
                .padding(vertical = 15.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
private fun LevelCard(
    level: InteractionLevel,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(ConcreteAlt)
            .border(
                width = 2.dp,
                color = if (selected) PlateYellow else SteelLine,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(13.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        PlateBadge(color = level.plateColor, size = 28.dp)
        Column(modifier = Modifier.padding(start = 12.dp)) {
            Text(
                text = level.label.uppercase(),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = com.tugymbro.app.ui.theme.Chalk
            )
            Text(
                text = level.description,
                fontSize = 11.5.sp,
                color = ChalkDim,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
