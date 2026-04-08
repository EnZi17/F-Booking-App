package com.example.f_booking.ui.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.f_booking.model.Field
import com.example.f_booking.network.ApiService
import com.example.f_booking.network.RetrofitClient
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val apiService = RetrofitClient.instance.create(ApiService::class.java)

    var fields = mutableStateOf<List<Field>>(emptyList())
    var isLoading = mutableStateOf(false)

    // Trạng thái bộ lọc (UC_U07)
    var selectedType = mutableStateOf<String?>(null)
    var minPrice = mutableStateOf<Int?>(null)

    init {
        fetchFields()
    }

    fun fetchFields() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                // Gọi API kèm theo các tham số lọc nếu có
                val response = apiService.getAllFields(
                    type = selectedType.value,
                    minPrice = minPrice.value
                )
                if (response.isSuccessful) {
                    fields.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                // Xử lý lỗi kết nối
            } finally {
                isLoading.value = false
            }
        }
    }

    // Hàm để thay đổi bộ lọc và tải lại dữ liệu
    fun applyFilter(type: String?, price: Int?) {
        selectedType.value = type
        minPrice.value = price
        fetchFields()
    }
}