package com.tugymbro.app.data.repository

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Datos de ejemplo usados mientras no hay backend conectado.
 * Cuando se agreguen las credenciales de Supabase (ver README), estas clases
 * se reemplazan por SupabaseUserRepository / SupabaseDiscoveryRepository /
 * etc. sin tocar ningún ViewModel, porque todos dependen de las interfaces
 * en domain/repository, no de esta implementación.
 */

private val sampleMatches = listOf(
    UserProfile(
        id = "u1",
        name = "Fede",
        age = 27,
        bio = "Entreno de 19 a 21h en SportClub Once. Busco alguien para pierna, sin drama de charla.",
        gymName = "SportClub Once",
        trainingTypes = listOf("Fuerza"),
        interactionLevel = InteractionLevel.SPOTTER,
        distanceMeters = 450
    ),
    UserProfile(
        id = "u2",
        name = "Cami",
        age = 24,
        bio = "Voy temprano, antes del laburo. Prefiero entrenar en silencio con música.",
        gymName = "SportClub Once",
        trainingTypes = listOf("Funcional", "Cardio"),
        interactionLevel = InteractionLevel.SILENCIOSO,
        distanceMeters = 300
    ),
    UserProfile(
        id = "u3",
        name = "Nico",
        age = 31,
        bio = "Rutina full body, tardes. Buena onda para charlar entre series.",
        gymName = "Iron House",
        trainingTypes = listOf("Fuerza", "Hipertrofia"),
        interactionLevel = InteractionLevel.CHARLA,
        distanceMeters = 900
    ),
    UserProfile(
        id = "u4",
        name = "Ari",
        age = 29,
        bio = "Busco compañero fijo para armar rutina de 3 meses juntos.",
        gymName = "Iron House",
        trainingTypes = listOf("Powerlifting"),
        interactionLevel = InteractionLevel.FULL_TRAINING,
        distanceMeters = 1200
    )
)

@Singleton
class MockUserRepository @Inject constructor() : UserRepository {
    private val currentUser = MutableStateFlow(
        UserProfile(
            id = "me",
            name = "Vos",
            age = 26,
            bio = "",
            gymName = "SportClub Once",
            trainingTypes = listOf("Fuerza"),
            interactionLevel = InteractionLevel.SPOTTER,
            distanceMeters = 0
        )
    )

    override suspend fun getCurrentUserProfile(): UserProfile = currentUser.value

    override suspend fun updateInteractionLevel(level: InteractionLevel) {
        currentUser.update { it.copy(interactionLevel = level) }
    }
}

@Singleton
class MockDiscoveryRepository @Inject constructor() : DiscoveryRepository {
    override suspend fun getNearbyMatches(): List<NearbyMatch> =
        sampleMatches.map { NearbyMatch(it) }

    override suspend fun getNearbyMatchById(userId: String): NearbyMatch? =
        sampleMatches.find { it.id == userId }?.let { NearbyMatch(it) }
}

@Singleton
class MockMatchRepository @Inject constructor() : MatchRepository {
    override suspend fun sendMatchRequest(toUserId: String): MatchRequest =
        MatchRequest(
            id = UUID.randomUUID().toString(),
            fromUserId = "me",
            toUserId = toUserId,
            status = MatchStatus.PENDIENTE
        )

    override suspend fun respondToRequest(requestId: String, accept: Boolean) {
        // TODO: implementar contra Supabase (tabla matchRequests)
    }
}

@Singleton
class MockChatRepository @Inject constructor() : ChatRepository {
    private val messages: MutableStateFlow<List<ChatMessage>> = MutableStateFlow(
        listOf(
            ChatMessage("m1", "match1", "u1", "Dale, te acepté. ¿Mañana 19h en el gym?", isMine = false, timestampMillis = 0),
            ChatMessage("m2", "match1", "me", "Perfecto, ahí estoy.", isMine = true, timestampMillis = 1),
            ChatMessage("m3", "match1", "u1", "Tranqui, no hace falta hablar de más jaja, avisame cuando llegues.", isMine = false, timestampMillis = 2)
        )
    )

    override fun observeMessages(matchId: String): StateFlow<List<ChatMessage>> = messages

    override suspend fun refreshMessages(matchId: String) {
        // El mock ya es reactivo (StateFlow en memoria), no necesita refrescar.
    }

    override suspend fun sendMessage(matchId: String, text: String) {
        messages.update {
            it + ChatMessage(
                id = UUID.randomUUID().toString(),
                matchId = matchId,
                senderId = "me",
                text = text,
                isMine = true,
                timestampMillis = System.currentTimeMillis()
            )
        }
    }

    override fun getIcebreakers(): List<Icebreaker> = listOf(
        Icebreaker("¿Qué rutina hacés hoy?"),
        Icebreaker("Llego en 10 min"),
        Icebreaker("¿Nos vemos en la entrada?")
    )
}
