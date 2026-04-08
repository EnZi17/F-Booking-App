package com.example.f_booking.ui.login

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.f_booking.model.LoginRequest
import com.example.f_booking.network.ApiService
import com.example.f_booking.network.RetrofitClient
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    // Khởi tạo ApiService
    private val apiService = RetrofitClient.instance.create(ApiService::class.java)

    // Các biến trạng thái của giao diện
    var phoneNumber = mutableStateOf("")
    var password = mutableStateOf("")
    var isLoading = mutableStateOf(false)
    var errorMessage = mutableStateOf<String?>(null)

    // Hàm gọi API đăng nhập
    fun login(onSuccess: (String, String) -> Unit) {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                val request = LoginRequest(phoneNumber.value, password.value)
                val response = apiService.login(request)

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    val token = body.token ?: ""
                    val role = body.user?.role ?: "USER"

                    // Gắn Token vào cấu hình mạng ngay lập tức để gọi các API khác
                    RetrofitClient.authToken = token

                    // Báo thành công ra ngoài màn hình
                    onSuccess(token, role)
                } else {
                    errorMessage.value = "Sai số điện thoại hoặc mật khẩu!"
                }
            } catch (e: Exception) {
                errorMessage.value = "Lỗi kết nối: Không thể kết nối tới Server!"
            } finally {
                isLoading.value = false
            }
        }
    }
}