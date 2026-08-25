package com.tugymbro.app.domain.model

data class UserProfile(
    val id: String,
    val name: String,
    val age: Int,
    val bio: String,
    val gymName: String,
    val trainingTypes: List<String>,
    val interactionLevel: InteractionLevel,
    val distanceMeters: Int,
    val photoUrl: String? = null
)

/**
 * Persona compatible mostrada en la barra ("disco"). distanceMeters determina
 * el tamaño y la cercanía al centro de la barra en HomeScreen.
 */
data class NearbyMatch(
    val profile: UserProfile
)
