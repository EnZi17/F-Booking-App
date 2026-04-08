package com.example.f_booking.model

import com.google.gson.annotations.SerializedName

data class Booking(
    @SerializedName("_id") val id: String,
    val fieldId: Field, // Backend trả về object Field đầy đủ
    val userId: String,
    val bookingDate: String,
    val startTime: String,
    val endTime: String,
    val totalPrice: Int,
    val status: String // "PENDING", "APPROVED", "REJECTED"
)
