package com.tugymbro.app.feature.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tugymbro.app.domain.model.ChatMessage
import com.tugymbro.app.domain.model.Icebreaker
import com.tugymbro.app.domain.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val icebreakers: List<Icebreaker> = emptyList(),
    val draft: String = ""
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val matchId = "match1" // TODO: recibir por navegación cuando haya matches reales

    private val _uiState = MutableStateFlow(ChatUiState(icebreakers = chatRepository.getIcebreakers()))
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            chatRepository.refreshMessages(matchId)
        }
        viewModelScope.launch {
            chatRepository.observeMessages(matchId).collect { messages ->
                _uiState.value = _uiState.value.copy(messages = messages)
            }
        }
    }

    fun onDraftChanged(text: String) {
        _uiState.value = _uiState.value.copy(draft = text)
    }

    fun onSend(text: String = _uiState.value.draft) {
        if (text.isBlank()) return
        viewModelScope.launch {
            chatRepository.sendMessage(matchId, text)
            _uiState.value = _uiState.value.copy(draft = "")
        }
    }
}
