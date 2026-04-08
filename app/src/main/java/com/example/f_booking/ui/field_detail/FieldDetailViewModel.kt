package com.example.f_booking.ui.field_detail

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.f_booking.model.BookedSlot
import com.example.f_booking.model.BookingRequest
import com.example.f_booking.model.Field
import com.example.f_booking.network.ApiService
import com.example.f_booking.network.RetrofitClient
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FieldDetailViewModel : ViewModel() {
    private val apiService = RetrofitClient.instance.create(ApiService::class.java)

    var field = mutableStateOf<Field?>(null)
    var isLoading = mutableStateOf(false)
    var bookingMessage = mutableStateOf<String?>(null)

    // UC_U09: Dữ liệu lịch trống
    var bookedSlots = mutableStateOf<List<BookedSlot>>(emptyList())
    // Mặc định chọn ngày hôm nay
    var selectedDate = mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()))

    // UC_U10: Dữ liệu người dùng chọn
    var selectedTime = mutableStateOf<String?>(null)

    fun fetchFieldDetail(id: String) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = apiService.getFieldDetail(id)
                if (response.isSuccessful) {
                    field.value = response.body()
                    // Khi load sân thành công, tự động check lịch trống của ngày hôm nay
                    fetchBookedSlots(id, selectedDate.value)
                }
            } catch (e: Exception) {}
            finally { isLoading.value = false }
        }
    }

    fun fetchBookedSlots(fieldId: String, date: String) {
        viewModelScope.launch {
            try {
                val response = apiService.getBookedSlots(fieldId, date)
                if (response.isSuccessful) {
                    bookedSlots.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {}
        }
    }

    fun onDateChange(date: String) {
        selectedDate.value = date
        selectedTime.value = null // Xóa giờ đã chọn nếu đổi ngày
        field.value?.id?.let { fetchBookedSlots(it, date) } // Lấy lịch trống của ngày mới
    }

    fun bookField(onSuccess: () -> Unit) {
        val currentField = field.value ?: return
        val startTime = selectedTime.value ?: return

        // Tính End Time (mặc định cho thuê block 1 tiếng)
        val endHour = startTime.split(":")[0].toInt() + 1
        val endTime = "${endHour.toString().padStart(2, '0')}:00"

        viewModelScope.launch {
            isLoading.value = true
            bookingMessage.value = null
            try {
                val request = BookingRequest(
                    fieldId = currentField.id,
                    bookingDate = selectedDate.value,
                    startTime = startTime,
                    endTime = endTime,
                    totalPrice = currentField.pricePerHour
                )
                val response = apiService.createBooking(request)
                if (response.isSuccessful) {
                    bookingMessage.value = "Đặt sân thành công! Chờ Admin duyệt."
                    onSuccess()
                } else {
                    bookingMessage.value = "Lỗi: Khung giờ này vừa có người đặt."
                }
            } catch (e: Exception) {
                bookingMessage.value = "Lỗi kết nối tới server."
            } finally {
                isLoading.value = false
            }
        }
    }
}