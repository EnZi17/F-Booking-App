package com.example.f_booking.ui.admin

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.f_booking.model.BookingHistory // Đổi thành BookingHistory
import com.example.f_booking.model.UpdateBookingStatusRequest // Dùng đúng Request trong ApiRequests.kt của bạn
import com.example.f_booking.network.ApiService
import com.example.f_booking.network.RetrofitClient
import kotlinx.coroutines.launch

class AdminBookingViewModel : ViewModel() {
    private val apiService = RetrofitClient.instance.create(ApiService::class.java)

    // ĐỔI SANG BookingHistory
    var pendingBookings = mutableStateOf<List<BookingHistory>>(emptyList())
    var isLoading = mutableStateOf(false)
    var actionMessage = mutableStateOf<String?>(null)

    fun fetchPendingBookings() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                // Gọi ĐÚNG tên hàm trong ApiService.kt của bạn
                val response = apiService.getAllBookings(status = "PENDING")
                if (response.isSuccessful) {
                    pendingBookings.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                println("LỖI LẤY ĐƠN ADMIN: ${e.message}")
                e.printStackTrace()
            } finally {
                isLoading.value = false
            }
        }
    }

    fun changeBookingStatus(bookingId: String, newStatus: String) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = apiService.updateBookingStatus(bookingId, UpdateBookingStatusRequest(newStatus))
                if (response.isSuccessful) {
                    actionMessage.value = if (newStatus == "APPROVED") "Đã duyệt đơn!" else "Đã từ chối đơn!"
                    fetchPendingBookings()
                } else {
                    actionMessage.value = "Xử lý thất bại!"
                }
            } catch (e: Exception) {
                actionMessage.value = "Lỗi kết nối!"
            } finally {
                isLoading.value = false
            }
        }
    }
}