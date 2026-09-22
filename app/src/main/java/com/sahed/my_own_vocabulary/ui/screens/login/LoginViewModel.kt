package com.sahed.my_own_vocabulary.ui.screens.login

import android.app.Activity
import android.app.Application
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.sahed.my_own_vocabulary.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    val authRepository = AuthRepository(application)

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _loginSuccessEvent = MutableSharedFlow<Unit>()
    val loginSuccessEvent: SharedFlow<Unit> = _loginSuccessEvent.asSharedFlow()

    val currentUser: StateFlow<FirebaseUser?> = authRepository.currentUser

    fun signInWithGoogle(activity: Activity) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = authRepository.signInWithGoogle(activity)

            if (result.isSuccess) {
                _uiState.update { it.copy(isLoading = false, errorMessage = null) }
                _loginSuccessEvent.emit(Unit)
            } else {
                val exception = result.exceptionOrNull()
                if (exception is GetCredentialCancellationException) {
                    _uiState.update { it.copy(isLoading = false) }
                } else {
                    val message = exception?.localizedMessage
                        ?: "Google Sign-In failed. Please try again or explore as guest."
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = message
                        )
                    }
                }
            }
        }
    }

    fun continueAsGuest() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = authRepository.signInAsGuest()
            _uiState.update { it.copy(isLoading = false) }
            _loginSuccessEvent.emit(Unit)
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
