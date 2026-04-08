package com.example.f_booking.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.f_booking.model.RegisterRequest
import com.example.f_booking.network.ApiService
import com.example.f_booking.network.RetrofitClient
import kotlinx.coroutines.launch

// --- ViewModel cho Đăng ký ---
class RegisterViewModel : ViewModel() {
    private val apiService = RetrofitClient.instance.create(ApiService::class.java)
    var fullName = mutableStateOf("")
    var phoneNumber = mutableStateOf("")
    var password = mutableStateOf("")
    var isLoading = mutableStateOf(false)
    var message = mutableStateOf<String?>(null)

    fun register(onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = apiService.register(RegisterRequest(fullName.value, phoneNumber.value, password.value))
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    message.value = "Đăng ký thất bại hoặc SĐT đã tồn tại"
                }
            } catch (e: Exception) {
                message.value = "Lỗi kết nối server"
            } finally {
                isLoading.value = false
            }
        }
    }
}

// --- Giao diện Đăng ký ---
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: RegisterViewModel = viewModel()
) {
    val greenPrimary = Color(0xFF2E7D32)

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("TẠO TÀI KHOẢN", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = greenPrimary)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = viewModel.fullName.value,
            onValueChange = { viewModel.fullName.value = it },
            label = { Text("Họ và tên") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = viewModel.phoneNumber.value,
            onValueChange = { viewModel.phoneNumber.value = it },
            label = { Text("Số điện thoại") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = viewModel.password.value,
            onValueChange = { viewModel.password.value = it },
            label = { Text("Mật khẩu") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        viewModel.message.value?.let {
            Text(it, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { viewModel.register(onRegisterSuccess) },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = greenPrimary),
            shape = RoundedCornerShape(12.dp),
            enabled = !viewModel.isLoading.value
        ) {
            Text("ĐĂNG KÝ NGAY")
        }

        TextButton(onClick = onNavigateBack) {
            Text("Đã có tài khoản? Đăng nhập", color = greenPrimary)
        }
    }
}