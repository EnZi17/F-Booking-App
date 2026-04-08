package com.example.f_booking.ui.field_detail

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.f_booking.model.BookingRequest
import com.example.f_booking.model.Field
import com.example.f_booking.network.ApiService
import com.example.f_booking.network.RetrofitClient
import kotlinx.coroutines.launch

class FieldDetailViewModel : ViewModel() {
    private val apiService = RetrofitClient.instance.create(ApiService::class.java)

    var field = mutableStateOf<Field?>(null)
    var isLoading = mutableStateOf(false)
    var bookingMessage = mutableStateOf<String?>(null)

    // Dữ liệu đặt sân (tạm thời để mặc định, bạn có thể thêm picker sau)
    var selectedDate = mutableStateOf("2024-05-20")
    var startTime = mutableStateOf("17:00")
    var endTime = mutableStateOf("18:00")

    fun fetchFieldDetail(id: String) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = apiService.getFieldDetail(id)
                if (response.isSuccessful) {
                    field.value = response.body()
                }
            } catch (e: Exception) {
                // Xử lý lỗi
            } finally {
                isLoading.value = false
            }
        }
    }

    fun bookField(onSuccess: () -> Unit) {
        val currentField = field.value ?: return
        viewModelScope.launch {
            isLoading.value = true
            try {
                val request = BookingRequest(
                    fieldId = currentField.id,
                    bookingDate = selectedDate.value,
                    startTime = startTime.value,
                    endTime = endTime.value,
                    totalPrice = currentField.pricePerHour // Tạm tính 1 giờ
                )
                val response = apiService.createBooking(request)
                if (response.isSuccessful) {
                    bookingMessage.value = "Đặt sân thành công! Chờ Admin duyệt."
                    onSuccess()
                } else {
                    bookingMessage.value = "Lỗi: Sân đã bị trùng lịch hoặc dữ liệu sai."
                }
            } catch (e: Exception) {
                bookingMessage.value = "Lỗi kết nối tới server."
            } finally {
                isLoading.value = false
            }
        }
    }
}