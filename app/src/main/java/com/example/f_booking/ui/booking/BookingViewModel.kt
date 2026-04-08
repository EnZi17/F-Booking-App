package com.example.f_booking.ui.bookings

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.f_booking.model.Booking
import com.example.f_booking.network.ApiService
import com.example.f_booking.network.RetrofitClient
import kotlinx.coroutines.launch

class BookingViewModel : ViewModel() {
    private val apiService = RetrofitClient.instance.create(ApiService::class.java)
    var bookings = mutableStateOf<List<Booking>>(emptyList())
    var isLoading = mutableStateOf(false)
    var actionMessage = mutableStateOf<String?>(null) // Chứa thông báo hủy

    fun fetchMyBookings() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = apiService.getMyBookings()
                if (response.isSuccessful) {
                    bookings.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                println("LỖI LẤY LỊCH SỬ: ${e.message}")
            } finally {
                isLoading.value = false
            }
        }
    }

    // UC_U12: Gọi API hủy sân
    fun cancelBooking(bookingId: String) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = apiService.cancelBooking(bookingId)
                if (response.isSuccessful) {
                    actionMessage.value = "Hủy đơn thành công!"
                    fetchMyBookings() // Tải lại danh sách để cập nhật trạng thái
                } else {
                    actionMessage.value = "Không thể hủy đơn này!"
                }
            } catch (e: Exception) {
                actionMessage.value = "Lỗi kết nối!"
            } finally {
                isLoading.value = false
            }
        }
    }
}