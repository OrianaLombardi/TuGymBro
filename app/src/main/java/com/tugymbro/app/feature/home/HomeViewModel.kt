package com.tugymbro.app.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tugymbro.app.domain.model.NearbyMatch
import com.tugymbro.app.domain.usecase.GetNearbyMatchesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = true,
    val matches: List<NearbyMatch> = emptyList(),
    val selectedMatch: NearbyMatch? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getNearbyMatches: GetNearbyMatchesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val matches = getNearbyMatches()
            _uiState.value = _uiState.value.copy(isLoading = false, matches = matches)
        }
    }

    fun onPlateSelected(match: NearbyMatch) {
        _uiState.value = _uiState.value.copy(selectedMatch = match)
    }

    fun onDismissSelection() {
        _uiState.value = _uiState.value.copy(selectedMatch = null)
    }
}
