package com.example.f_booking.ui.admin

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.f_booking.model.Field
import com.example.f_booking.model.FieldRequest
import com.example.f_booking.network.ApiService
import com.example.f_booking.network.RetrofitClient
import kotlinx.coroutines.launch

class AdminFieldViewModel : ViewModel() {
    private val apiService = RetrofitClient.instance.create(ApiService::class.java)

    var fields = mutableStateOf<List<Field>>(emptyList())
    var isLoading = mutableStateOf(false)
    var actionMessage = mutableStateOf<String?>(null) // Dùng để hiện thông báo

    // Lấy danh sách sân
    fun fetchAllFields() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = apiService.getAllFields(null, null, null)
                if (response.isSuccessful) fields.value = response.body() ?: emptyList()
            } catch (e: Exception) { }
            finally { isLoading.value = false }
        }
    }

    // UC_A04: Thêm sân mới
    fun createField(request: FieldRequest, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = apiService.createField(request)
                if (response.isSuccessful) {
                    actionMessage.value = "Thêm sân thành công!"
                    fetchAllFields() // Tải lại danh sách
                    onSuccess()
                } else actionMessage.value = "Thêm sân thất bại!"
            } catch (e: Exception) { actionMessage.value = "Lỗi kết nối!" }
            finally { isLoading.value = false }
        }
    }

    // UC_A05: Cập nhật thông tin sân
    fun updateField(id: String, request: FieldRequest, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = apiService.updateField(id, request)
                if (response.isSuccessful) {
                    actionMessage.value = "Cập nhật thành công!"
                    fetchAllFields()
                    onSuccess()
                } else actionMessage.value = "Cập nhật thất bại!"
            } catch (e: Exception) { actionMessage.value = "Lỗi kết nối!" }
            finally { isLoading.value = false }
        }
    }

    // Lấy dữ liệu 1 sân để điền vào Form khi chọn Sửa
    fun getFieldById(id: String, onLoaded: (Field) -> Unit) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = apiService.getFieldDetail(id)
                if (response.isSuccessful) response.body()?.let { onLoaded(it) }
            } catch (e: Exception) { }
            finally { isLoading.value = false }
        }
    }
}