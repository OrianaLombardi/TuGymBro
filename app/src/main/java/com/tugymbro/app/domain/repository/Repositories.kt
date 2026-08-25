package com.tugymbro.app.domain.repository

import com.tugymbro.app.domain.model.ChatMessage
import com.tugymbro.app.domain.model.Icebreaker
import com.tugymbro.app.domain.model.InteractionLevel
import com.tugymbro.app.domain.model.MatchRequest
import com.tugymbro.app.domain.model.NearbyMatch
import com.tugymbro.app.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

/**
 * Estas interfaces son el contrato que usa toda la capa de presentación
 * (ViewModels). La implementación real (Supabase) vive en data/repository
 * y se inyecta con Hilt (ver di/AppModule.kt). Mientras no se cargan las
 * credenciales de Supabase, se usa MockRepositoryModule con datos de ejemplo,
 * así la app funciona y se puede navegar de punta a punta sin backend.
 */
interface UserRepository {
    suspend fun getCurrentUserProfile(): UserProfile
    suspend fun updateInteractionLevel(level: InteractionLevel)
}

interface DiscoveryRepository {
    /** Personas compatibles cerca, ordenadas por score de matching (ver doc técnico 4.3). */
    suspend fun getNearbyMatches(): List<NearbyMatch>

    suspend fun getNearbyMatchById(userId: String): NearbyMatch?
}

interface MatchRepository {
    suspend fun sendMatchRequest(toUserId: String): MatchRequest
    suspend fun respondToRequest(requestId: String, accept: Boolean)
}

interface ChatRepository {
    fun observeMessages(matchId: String): Flow<List<ChatMessage>>
    suspend fun refreshMessages(matchId: String)
    suspend fun sendMessage(matchId: String, text: String)
    fun getIcebreakers(): List<Icebreaker>
}
