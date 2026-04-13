package com.example.f_booking.network

import com.example.f_booking.model.*
import com.example.f_booking.model.Field
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ================= 1. NHÓM AUTH & USERS =================
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @GET("users/profile")
    suspend fun getProfile(): Response<User>

    @PUT("users/profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<ApiResponse>


    // ================= 2. NHÓM SÂN BÓNG (Khách hàng) =================
    @GET("fields")
    suspend fun getAllFields(
        @Query("type") type: String? = null,
        @Query("minPrice") minPrice: Int? = null,
        @Query("maxPrice") maxPrice: Int? = null
    ): Response<List<Field>>

    @GET("fields/{id}")
    suspend fun getFieldDetail(@Path("id") id: String): Response<Field>

    @GET("fields/{id}/slots")
    suspend fun getBookedSlots(
        @Path("id") id: String,
        @Query("date") date: String
    ): Response<List<BookedSlot>>


    // ================= 3. NHÓM ĐẶT LỊCH (Khách hàng) =================
// UC_U10: Đặt sân
// UC_U10: Đặt sân
    @POST("bookings")
    suspend fun createBooking(@Body request: BookingRequest): Response<Booking>

    // UC_U11: Xem lịch sử đặt sân của tôi (SỬA CHỖ NÀY)
    @GET("bookings/my-history")
    suspend fun getMyBookings(): Response<List<Booking>>

    // Lấy chi tiết 1 đơn
    @GET("bookings/{id}")
    suspend fun getBookingDetail(@Path("id") id: String): Response<BookingHistory>

    // UC_U12: Hủy đặt sân
    @PUT("bookings/{id}/cancel")
    suspend fun cancelBooking(@Path("id") id: String): Response<ApiResponse>


    // ================= 4. NHÓM QUẢN TRỊ (ADMIN) =================
    @POST("admin/fields")
    suspend fun addField(@Body request: AdminFieldRequest): Response<ApiResponse>

    @PUT("admin/fields/{id}")
    suspend fun updateField(
        @Path("id") id: String,
        @Body request: AdminFieldRequest
    ): Response<ApiResponse>

    @GET("admin/bookings")
    suspend fun getAllBookings(@Query("status") status: String? = null): Response<List<BookingHistory>>

    @PUT("admin/bookings/{id}/status")
    suspend fun updateBookingStatus(
        @Path("id") id: String,
        @Body request: UpdateBookingStatusRequest
    ): Response<ApiResponse>

    // UC_A04: Admin thêm sân mới
    @POST("admin/fields")
    suspend fun createField(@Body request: FieldRequest): Response<Field>

    // UC_A05: Admin cập nhật thông tin sân
    @PUT("admin/fields/{id}")
    suspend fun updateField(@Path("id") id: String, @Body request: FieldRequest): Response<Field>

    // UC_A07: Lấy danh sách đơn đặt sân (có thể lọc theo trạng thái PENDING)
    @GET("admin/bookings")
    suspend fun getAdminBookings(@Query("status") status: String? = null): Response<List<com.example.f_booking.model.Booking>>

    // UC_A08 & UC_A09: Cập nhật trạng thái đơn (Duyệt / Từ chối)
    @PUT("admin/bookings/{id}/status")
    suspend fun updateBookingStatus(
        @Path("id") id: String,
        @Body request: com.example.f_booking.model.UpdateStatusRequest
    ): Response<Any>
    // UC_A10, A11, A12: Lấy dữ liệu thống kê Dashboard
    @GET("admin/dashboard")
    suspend fun getAdminDashboard(): Response<com.example.f_booking.model.DashboardResponse>
}