package com.tugymbro.app.feature.match

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tugymbro.app.domain.model.UserProfile
import com.tugymbro.app.domain.usecase.GetMatchByIdUseCase
import com.tugymbro.app.domain.usecase.SendMatchRequestUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MatchUiState(
    val profile: UserProfile? = null,
    val requestSent: Boolean = false,
    val isSending: Boolean = false
)

@HiltViewModel
class MatchViewModel @Inject constructor(
    private val getMatchById: GetMatchByIdUseCase,
    private val sendMatchRequest: SendMatchRequestUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val userId: String = checkNotNull(savedStateHandle["userId"])

    private val _uiState = MutableStateFlow(MatchUiState())
    val uiState: StateFlow<MatchUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val match = getMatchById(userId)
            _uiState.value = _uiState.value.copy(profile = match?.profile)
        }
    }

    fun onSendRequest() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSending = true)
            sendMatchRequest(userId)
            _uiState.value = _uiState.value.copy(isSending = false, requestSent = true)
        }
    }
}
