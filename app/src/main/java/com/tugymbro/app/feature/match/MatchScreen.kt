package com.tugymbro.app.feature.match

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tugymbro.app.domain.model.UserProfile
import com.tugymbro.app.ui.theme.Chalk
import com.tugymbro.app.ui.theme.ChalkDim
import com.tugymbro.app.ui.theme.ConcreteAlt
import com.tugymbro.app.ui.theme.Iron
import com.tugymbro.app.ui.theme.PlateBlueDark
import com.tugymbro.app.ui.theme.PlateRedDark
import com.tugymbro.app.ui.theme.PlateYellow
import com.tugymbro.app.ui.theme.SteelLine

@Composable
fun MatchScreen(
    onDismiss: () -> Unit,
    onRequestSent: () -> Unit,
    viewModel: MatchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val profile = uiState.profile

    Box(
        modifier = Modifier.fillMaxSize().background(Iron).padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        if (profile == null) {
            CircularProgressIndicator(color = PlateYellow)
            return@Box
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(ConcreteAlt)
                .border(2.dp, SteelLine, RoundedCornerShape(14.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(
                        androidx.compose.ui.graphics.Brush.linearGradient(
                            listOf(PlateRedDark, PlateBlueDark)
                        )
                    ),
                contentAlignment = Alignment.BottomStart
            ) {
                Text(
                    text = "${profile.name.uppercase()}, ${profile.age}",
                    color = Chalk,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(14.dp)
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Row {
                    Tag(
                        text = "Disco ${profile.interactionLevel.label.lowercase()}",
                        bg = Color(0xFF4A100D),
                        fg = Color(0xFFFFB4AE)
                    )
                    Tag(text = "${profile.distanceMeters} m", bg = Iron, fg = ChalkDim)
                }
                Text(
                    text = profile.bio,
                    fontSize = 13.sp,
                    color = ChalkDim,
                    lineHeight = 19.sp,
                    modifier = Modifier.padding(top = 12.dp, bottom = 16.dp)
                )

                if (uiState.requestSent) {
                    Text(
                        text = "SOLICITUD ENVIADA",
                        color = PlateYellow,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    )
                } else {
                    Row {
                        Text(
                            text = "AHORA NO",
                            color = Chalk,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.5.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .border(2.dp, SteelLine, RoundedCornerShape(6.dp))
                                .clickable(onClick = onDismiss)
                                .padding(vertical = 12.dp)
                        )
                        Text(
                            text = "SOLICITAR",
                            color = Iron,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.5.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(PlateYellow)
                                .clickable {
                                    viewModel.onSendRequest()
                                    onRequestSent()
                                }
                                .padding(vertical = 12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Tag(text: String, bg: Color, fg: Color) {
    Text(
        text = text.uppercase(),
        color = fg,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .padding(end = 6.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(bg)
            .padding(horizontal = 9.dp, vertical = 5.dp)
    )
}
