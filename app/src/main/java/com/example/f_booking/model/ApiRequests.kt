package com.example.f_booking.model

// Dùng chung cho các API chỉ trả về mỗi câu thông báo { "message": "..." }
data class ApiResponse(
    val message: String
)

data class RegisterRequest(
    val fullName: String,
    val phoneNumber: String,
    val password: String
)

data class LoginRequest(
    val phoneNumber: String,
    val password: String
)

data class UpdateProfileRequest(
    val fullName: String?,
    val password: String?
)

data class BookingRequest(
    val fieldId: String,
    val bookingDate: String,
    val startTime: String,
    val endTime: String,
    val totalPrice: Int
)

data class UpdateBookingStatusRequest(
    val status: String // "APPROVED" hoặc "REJECTED"
)

data class AdminFieldRequest(
    val name: String,
    val type: String,
    val pricePerHour: Int,
    val description: String?,
    val imageUrl: String?
)

data class FieldRequest(
    val name: String,
    val type: String,
    val pricePerHour: Int,
    val description: String,
    val imageUrl: String,
    val isActive: Boolean = true
)

data class UpdateStatusRequest(
    val status: String
)
data class FieldStat(
    val fieldName: String,
    val totalBookings: Int
)

data class DashboardResponse(
    val totalRevenue: Int,
    val fieldStats: List<FieldStat>,
    val history: List<com.example.f_booking.model.BookingHistory> // Dùng đúng model BookingHistory của bạn
)