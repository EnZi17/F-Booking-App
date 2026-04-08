package com.example.f_booking.ui.bookings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingHistoryScreen(viewModel: BookingViewModel = viewModel()) {
    LaunchedEffect(Unit) { viewModel.fetchMyBookings() }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Lịch sử đặt sân") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (viewModel.isLoading.value) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            // Hiển thị thông báo khi hủy thành công
            viewModel.actionMessage.value?.let {
                Text(
                    text = it,
                    color = if (it.contains("thành công")) Color(0xFF2E7D32) else Color.Red,
                    modifier = Modifier.padding(16.dp),
                    fontWeight = FontWeight.Bold
                )
            }

            LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                items(viewModel.bookings.value) { booking ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Chú ý: Cần an toàn null cho tên sân lỡ DB có lỗi mất liên kết
                            Text("Sân: ${booking.fieldId?.name ?: "Sân đã bị xóa"}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Text("Mã đơn: ${booking.id}", fontSize = 12.sp, color = Color.Gray)
                            Text("Ngày: ${booking.bookingDate}")
                            Text("Giờ: ${booking.startTime} - ${booking.endTime}")
                            Text("Tổng tiền: ${booking.totalPrice}đ", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)

                            val statusColor = when(booking.status) {
                                "APPROVED" -> Color(0xFF2E7D32) // Xanh lá
                                "REJECTED", "CANCELLED" -> Color.Red // Đỏ
                                else -> Color(0xFFFFA000) // Vàng cam cho PENDING
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Trạng thái: ${booking.status}",
                                    color = statusColor,
                                    fontWeight = FontWeight.ExtraBold
                                )

                                // Chỉ hiện nút Hủy nếu đơn đang chờ duyệt (PENDING)
                                if (booking.status == "PENDING") {
                                    OutlinedButton(
                                        onClick = { viewModel.cancelBooking(booking.id) },
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                    ) {
                                        Text("Hủy đơn", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}