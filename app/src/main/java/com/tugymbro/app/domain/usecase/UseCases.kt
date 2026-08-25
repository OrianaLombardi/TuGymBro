package com.tugymbro.app.domain.usecase

import com.tugymbro.app.domain.model.InteractionLevel
import com.tugymbro.app.domain.model.NearbyMatch
import com.tugymbro.app.domain.repository.DiscoveryRepository
import com.tugymbro.app.domain.repository.MatchRepository
import com.tugymbro.app.domain.repository.UserRepository
import javax.inject.Inject

class GetNearbyMatchesUseCase @Inject constructor(
    private val discoveryRepository: DiscoveryRepository
) {
    suspend operator fun invoke(): List<NearbyMatch> =
        discoveryRepository.getNearbyMatches().sortedBy { it.profile.distanceMeters }
}

class SaveInteractionLevelUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(level: InteractionLevel) =
        userRepository.updateInteractionLevel(level)
}

class GetMatchByIdUseCase @Inject constructor(
    private val discoveryRepository: DiscoveryRepository
) {
    suspend operator fun invoke(userId: String): NearbyMatch? =
        discoveryRepository.getNearbyMatchById(userId)
}

class SendMatchRequestUseCase @Inject constructor(
    private val matchRepository: MatchRepository
) {
    suspend operator fun invoke(toUserId: String) =
        matchRepository.sendMatchRequest(toUserId)
}
