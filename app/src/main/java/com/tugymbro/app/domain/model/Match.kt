package com.tugymbro.app.domain.model

enum class MatchStatus { PENDIENTE, ACEPTADO, RECHAZADO }

data class MatchRequest(
    val id: String,
    val fromUserId: String,
    val toUserId: String,
    val status: MatchStatus
)

data class ChatMessage(
    val id: String,
    val matchId: String,
    val senderId: String,
    val text: String,
    val isMine: Boolean,
    val timestampMillis: Long
)

/** Frase de rompehielo sugerida en el chat, para bajar la fricción de iniciar la charla. */
data class Icebreaker(val text: String)
