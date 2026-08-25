package com.tugymbro.app.feature.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tugymbro.app.domain.model.ChatMessage
import com.tugymbro.app.domain.model.Icebreaker
import com.tugymbro.app.ui.theme.Chalk
import com.tugymbro.app.ui.theme.ChalkDim
import com.tugymbro.app.ui.theme.ConcreteAlt
import com.tugymbro.app.ui.theme.Iron
import com.tugymbro.app.ui.theme.PlateBlue
import com.tugymbro.app.ui.theme.PlateYellow
import com.tugymbro.app.ui.theme.SteelLine

@Composable
fun ChatScreen(
    contactName: String,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(Iron)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 28.dp, 16.dp, 14.dp)
                .border(width = 0.dp, color = Color.Transparent), // placeholder para futura sombra
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(PlateBlue)
            )
            Column(modifier = Modifier.padding(start = 10.dp)) {
                Text(contactName, color = Chalk, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text("Match aceptado", color = ChalkDim, fontSize = 11.sp)
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.messages) { message ->
                MessageBubble(message)
            }
        }

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.icebreakers) { ice ->
                IcebreakerChip(ice) { viewModel.onSend(ice.text) }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = uiState.draft,
                onValueChange = viewModel::onDraftChanged,
                textStyle = TextStyle(color = Chalk, fontSize = 14.sp),
                cursorBrush = androidx.compose.ui.graphics.SolidColor(PlateYellow),
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(ConcreteAlt)
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            )
            Text(
                text = "ENVIAR",
                color = Iron,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(PlateYellow)
                    .clickable { viewModel.onSend() }
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }
    }
}

@Composable
private fun MessageBubble(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isMine) Arrangement.End else Arrangement.Start
    ) {
        Text(
            text = message.text,
            fontSize = 13.sp,
            color = if (message.isMine) Iron else Chalk,
            fontWeight = if (message.isMine) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(if (message.isMine) PlateYellow else ConcreteAlt)
                .border(
                    width = if (message.isMine) 0.dp else 1.dp,
                    color = SteelLine,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 12.dp, vertical = 9.dp)
        )
    }
}

@Composable
private fun IcebreakerChip(icebreaker: Icebreaker, onClick: () -> Unit) {
    Text(
        text = icebreaker.text.uppercase(),
        fontSize = 10.5.sp,
        fontWeight = FontWeight.Bold,
        color = ChalkDim,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(ConcreteAlt)
            .border(1.dp, SteelLine, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    )
}
