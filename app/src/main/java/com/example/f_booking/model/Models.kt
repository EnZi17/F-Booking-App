package com.example.f_booking.model

import com.google.gson.annotations.SerializedName

// 1. Model Người dùng (Dùng chung cho cả lúc Login và lấy Profile)
data class User(
    // alternate = ["id"] giúp hứng được cả "_id" (từ MongoDB) và "id" (từ API login)
    @SerializedName("_id", alternate = ["id"]) val id: String,
    val fullName: String,
    val phoneNumber: String,
    val role: String
)

// 2. Model Sân bóng
data class Field(
    @SerializedName("_id") val id: String,
    val name: String,
    val type: String,
    val pricePerHour: Int,
    val description: String?,
    val imageUrl: String?,
    val isActive: Boolean
)

// 3. Model hứng dữ liệu trả về khi Đăng nhập/Đăng ký
data class AuthResponse(
    val message: String,
    val token: String?,
    val user: User?
)

// 4. Model Khung giờ đã đặt (Để làm mờ trên App)
data class BookedSlot(
    val startTime: String,
    val endTime: String,
    val status: String
)

// 5. Model Lịch sử đặt sân (Hứng dữ liệu từ API get history đã được populate)
data class BookingHistory(
    @SerializedName("_id") val id: String,
    val fieldId: Field?, // Nhận object Field (chứa name, imageUrl) từ lệnh populate backend
    val bookingDate: String,
    val startTime: String,
    val endTime: String,
    val totalPrice: Int,
    val status: String
)