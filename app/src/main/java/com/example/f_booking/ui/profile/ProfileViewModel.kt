package com.example.f_booking.ui.profile

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.f_booking.model.User
import com.example.f_booking.network.ApiService
import com.example.f_booking.network.RetrofitClient
import com.example.f_booking.utils.TokenManager
import kotlinx.coroutines.launch

class ProfileViewModel(private val tokenManager: com.example.f_booking.utils.TokenManager) : ViewModel() {
    private val apiService = RetrofitClient.instance.create(ApiService::class.java)

    var userProfile = mutableStateOf<User?>(null)
    var isLoading = mutableStateOf(false)

    // UC_U04: Lấy thông tin cá nhân từ API
    fun fetchProfile() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = apiService.getProfile()
                if (response.isSuccessful) {
                    userProfile.value = response.body()
                }
            } catch (e: Exception) { /* Xử lý lỗi kết nối */ }
            finally { isLoading.value = false }
        }
    }

    // UC_U03: Đăng xuất - Xóa Token và điều hướng về Login
    fun logout(onLogoutSuccess: () -> Unit) {
        viewModelScope.launch {
            tokenManager.clearToken() // Xóa token khỏi DataStore
            RetrofitClient.authToken = null // Xóa token trong bộ nhớ tạm
            onLogoutSuccess()
        }
    }
    // Biến lưu thông báo cập nhật
    var updateMessage = mutableStateOf<String?>(null)

    fun updateProfile(fullName: String, password: String?, onUpdateSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading.value = true
            updateMessage.value = null
            try {
                val request = com.example.f_booking.model.UpdateProfileRequest(
                    fullName = fullName,
                    password = if (password.isNullOrBlank()) null else password
                )
                val response = apiService.updateProfile(request)
                if (response.isSuccessful) {
                    updateMessage.value = "Cập nhật thành công!"
                    fetchProfile() // Tải lại thông tin mới để hiển thị (UC_U04)
                    onUpdateSuccess()
                } else {
                    updateMessage.value = "Cập nhật thất bại!"
                }
            } catch (e: Exception) {
                updateMessage.value = "Lỗi kết nối server!"
            } finally {
                isLoading.value = false
            }
        }
    }
}