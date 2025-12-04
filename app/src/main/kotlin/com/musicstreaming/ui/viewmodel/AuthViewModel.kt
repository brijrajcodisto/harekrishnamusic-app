package com.musicstreaming.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.musicstreaming.data.auth.AuthManager
import com.musicstreaming.data.local.dao.UserDao
import com.musicstreaming.data.local.entity.UserEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authManager: AuthManager,
    private val userDao: UserDao
) : ViewModel() {

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        _isLoggedIn.value = authManager.isLoggedIn()
    }

    fun signup(username: String, email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Simulate API call - replace with actual API call
                val userId = System.currentTimeMillis().toString()
                val user = UserEntity(
                    id = userId,
                    username = username,
                    email = email,
                    profileImage = null,
                    accessToken = "token_$userId",
                    refreshToken = null
                )

                authManager.saveToken(user.accessToken)
                authManager.saveUserId(userId)
                userDao.insertUser(user)

                _isLoggedIn.value = true
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Simulate API call - replace with actual API call
                val userId = System.currentTimeMillis().toString()
                authManager.saveToken("token_$userId")
                authManager.saveUserId(userId)

                _isLoggedIn.value = true
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authManager.logout()
            userDao.clearAll()
            _isLoggedIn.value = false
        }
    }

    fun continueAsGuest() {
        _isLoggedIn.value = false
    }
}
