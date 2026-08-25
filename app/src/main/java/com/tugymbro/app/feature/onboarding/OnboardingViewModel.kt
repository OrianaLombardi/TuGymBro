package com.tugymbro.app.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tugymbro.app.domain.model.InteractionLevel
import com.tugymbro.app.domain.usecase.SaveInteractionLevelUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnboardingUiState(
    val selectedLevel: InteractionLevel = InteractionLevel.SPOTTER,
    val isSaving: Boolean = false
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val saveInteractionLevel: SaveInteractionLevelUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun onLevelSelected(level: InteractionLevel) {
        _uiState.value = _uiState.value.copy(selectedLevel = level)
    }

    fun onContinueClicked(onDone: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            saveInteractionLevel(_uiState.value.selectedLevel)
            _uiState.value = _uiState.value.copy(isSaving = false)
            onDone()
        }
    }
}
