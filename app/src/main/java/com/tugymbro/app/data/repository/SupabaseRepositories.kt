package com.tugymbro.app.data.repository

import com.tugymbro.app.data.remote.SupabaseClientProvider
import com.tugymbro.app.data.remote.dto.MatchRequestRow
import com.tugymbro.app.data.remote.dto.MessageRow
import com.tugymbro.app.data.remote.dto.UserRow
import com.tugymbro.app.domain.model.ChatMessage
import com.tugymbro.app.domain.model.Icebreaker
import com.tugymbro.app.domain.model.InteractionLevel
import com.tugymbro.app.domain.model.MatchRequest
import com.tugymbro.app.domain.model.MatchStatus
import com.tugymbro.app.domain.model.NearbyMatch
import com.tugymbro.app.domain.model.UserProfile
import com.tugymbro.app.domain.repository.ChatRepository
import com.tugymbro.app.domain.repository.DiscoveryRepository
import com.tugymbro.app.domain.repository.MatchRepository
import com.tugymbro.app.domain.repository.UserRepository
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/*
 * Implementaciones reales contra el proyecto de Supabase conectado.
 * Todas dependen de SupabaseClientProvider.ensureSignedIn() para tener un
 * auth.uid() antes de leer o escribir, porque las políticas de RLS en
 * supabase/schema.sql están escritas contra ese id.
 *
 * Nota: el chat NO usa Realtime todavía (queda documentado como próximo
 * paso). Hoy funciona con "traer mensajes" + "mandar y volver a traer",
 * que ya es un backend real, pero no push instantáneo entre dos celulares.
 */

private fun UserRow.toDomain(): UserProfile = UserProfile(
    id = id,
    name = name,
    age = age,
    bio = bio,
    gymName = gymName,
    trainingTypes = trainingTypes.split(",").map { it.trim() }.filter { it.isNotEmpty() },
    interactionLevel = InteractionLevel.entries.find { it.name == interactionLevel }
        ?: InteractionLevel.SILENCIOSO,
    distanceMeters = distanceMeters,
    photoUrl = photoUrl
)

@Singleton
class SupabaseUserRepository @Inject constructor() : UserRepository {

    private val client get() = SupabaseClientProvider.client

    override suspend fun getCurrentUserProfile(): UserProfile {
        val uid = SupabaseClientProvider.ensureSignedIn()

        val existing = client.from("users")
            .select {
                filter { eq("id", uid) }
            }
            .decodeSingleOrNull<UserRow>()

        if (existing != null) return existing.toDomain()

        // Primera vez que se abre la app en este dispositivo: se crea un
        // perfil base. El nombre y la bio se completan después en la
        // pantalla de edición de perfil (no incluida todavía).
        val newRow = UserRow(
            id = uid,
            name = "Vos",
            age = 0,
            bio = "",
            gymName = "",
            trainingTypes = "",
            interactionLevel = InteractionLevel.SPOTTER.name,
            distanceMeters = 0
        )
        client.from("users").insert(newRow)
        return newRow.toDomain()
    }

    override suspend fun updateInteractionLevel(level: InteractionLevel) {
        val uid = SupabaseClientProvider.ensureSignedIn()
        client.from("users")
            .update({ set("interaction_level", level.name) }) {
                filter { eq("id", uid) }
            }
    }
}

@Singleton
class SupabaseDiscoveryRepository @Inject constructor() : DiscoveryRepository {

    private val client get() = SupabaseClientProvider.client

    override suspend fun getNearbyMatches(): List<NearbyMatch> {
        val uid = SupabaseClientProvider.ensureSignedIn()

        val rows = client.from("users")
            .select(columns = Columns.ALL) {
                filter { neq("id", uid) }
            }
            .decodeList<UserRow>()

        return rows.map { NearbyMatch(it.toDomain()) }
    }

    override suspend fun getNearbyMatchById(userId: String): NearbyMatch? {
        val row = client.from("users")
            .select {
                filter { eq("id", userId) }
            }
            .decodeSingleOrNull<UserRow>()
        return row?.let { NearbyMatch(it.toDomain()) }
    }
}

@Singleton
class SupabaseMatchRepository @Inject constructor() : MatchRepository {

    private val client get() = SupabaseClientProvider.client

    override suspend fun sendMatchRequest(toUserId: String): MatchRequest {
        val uid = SupabaseClientProvider.ensureSignedIn()

        val inserted = client.from("match_requests")
            .insert(MatchRequestRow(fromUserId = uid, toUserId = toUserId)) {
                select()
            }
            .decodeSingle<MatchRequestRow>()

        return MatchRequest(
            id = inserted.id ?: "",
            fromUserId = inserted.fromUserId,
            toUserId = inserted.toUserId,
            status = MatchStatus.PENDIENTE
        )
    }

    override suspend fun respondToRequest(requestId: String, accept: Boolean) {
        client.from("match_requests")
            .update({ set("status", if (accept) "ACEPTADO" else "RECHAZADO") }) {
                filter { eq("id", requestId) }
            }
    }
}

@Singleton
class SupabaseChatRepository @Inject constructor() : ChatRepository {

    private val client get() = SupabaseClientProvider.client
    private val messagesFlow = MutableStateFlow<List<ChatMessage>>(emptyList())

    override fun observeMessages(matchId: String): Flow<List<ChatMessage>> {
        // TODO (próximo paso): reemplazar por una suscripción real con el
        // módulo Realtime de Supabase para que los mensajes lleguen sin
        // necesidad de refrescar. Por ahora, cada pantalla de chat debe
        // llamar a refreshMessages(matchId) al entrar y después de enviar.
        return messagesFlow.asStateFlow()
    }

    override suspend fun refreshMessages(matchId: String) {
        val uid = SupabaseClientProvider.ensureSignedIn()
        val rows = client.from("messages")
            .select {
                filter { eq("match_id", matchId) }
                order("created_at", io.github.jan.supabase.postgrest.query.Order.ASCENDING)
            }
            .decodeList<MessageRow>()

        messagesFlow.value = rows.map { row ->
            ChatMessage(
                id = row.id ?: "",
                matchId = row.matchId,
                senderId = row.senderId,
                text = row.text,
                isMine = row.senderId == uid,
                timestampMillis = 0L
            )
        }
    }

    override suspend fun sendMessage(matchId: String, text: String) {
        val uid = SupabaseClientProvider.ensureSignedIn()
        client.from("messages").insert(
            MessageRow(matchId = matchId, senderId = uid, text = text)
        )
        refreshMessages(matchId)
    }

    override fun getIcebreakers(): List<Icebreaker> = listOf(
        Icebreaker("¿Qué rutina hacés hoy?"),
        Icebreaker("Llego en 10 min"),
        Icebreaker("¿Nos vemos en la entrada?")
    )
}
