package com.jonathan.cleancity.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jonathan.cleancity.ui.model.User
import com.jonathan.cleancity.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadUser()
    }

    fun loadUser() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val u = userRepository.getCurrentUser()
                Log.d("HomeViewModel", "loadUser -> $u")
                _user.value = u
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error loading user", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
