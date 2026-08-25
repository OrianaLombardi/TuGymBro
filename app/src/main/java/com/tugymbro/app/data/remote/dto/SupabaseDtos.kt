package com.tugymbro.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Representa una fila de la tabla `users` (ver supabase/schema.sql).
 * `trainingTypes` se guarda como texto separado por comas para simplificar
 * la serialización inicial; se puede migrar a un array real de Postgres
 * (text[]) más adelante sin romper el resto de la app.
 */
@Serializable
data class UserRow(
    val id: String,
    val name: String,
    val age: Int,
    val bio: String,
    @SerialName("gym_name") val gymName: String,
    @SerialName("training_types") val trainingTypes: String,
    @SerialName("interaction_level") val interactionLevel: String,
    @SerialName("distance_meters") val distanceMeters: Int = 0,
    @SerialName("photo_url") val photoUrl: String? = null
)

@Serializable
data class MatchRequestRow(
    val id: String? = null,
    @SerialName("from_user_id") val fromUserId: String,
    @SerialName("to_user_id") val toUserId: String,
    val status: String = "pendiente"
)

@Serializable
data class MessageRow(
    val id: String? = null,
    @SerialName("match_id") val matchId: String,
    @SerialName("sender_id") val senderId: String,
    val text: String,
    @SerialName("created_at") val createdAt: String? = null
)
