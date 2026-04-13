package com.example.f_booking.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminBookingScreen(viewModel: AdminBookingViewModel = viewModel()) {
    val blueAdmin = Color(0xFF1565C0)
    val greenApprove = Color(0xFF2E7D32)
    val redReject = Color(0xFFD32F2F)

    LaunchedEffect(Unit) { viewModel.fetchPendingBookings() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Duyệt Đơn Đặt Sân", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = blueAdmin)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (viewModel.isLoading.value) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = blueAdmin)
            }

            viewModel.actionMessage.value?.let {
                Text(
                    text = it,
                    color = if (it.contains("thất bại") || it.contains("Lỗi")) redReject else greenApprove,
                    modifier = Modifier.padding(16.dp),
                    fontWeight = FontWeight.Bold
                )
            }

            if (!viewModel.isLoading.value && viewModel.pendingBookings.value.isEmpty()) {
                Text(
                    text = "Không có yêu cầu đặt lịch nào đang chờ.",
                    modifier = Modifier.padding(16.dp),
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(viewModel.pendingBookings.value) { booking ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(2.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Mã đơn: ${booking.id}", fontSize = 12.sp, color = Color.Gray)
                            Text("Sân: ${booking.fieldId?.name ?: "Sân không xác định"}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = blueAdmin)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Ngày: ${booking.bookingDate}")
                            Text("Khung giờ: ${booking.startTime} - ${booking.endTime}")
                            Text("Tổng tiền: ${booking.totalPrice} VNĐ", fontWeight = FontWeight.Bold, color = greenApprove)

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Nút Từ chối (UC_A09)
                                OutlinedButton(
                                    onClick = { viewModel.changeBookingStatus(booking.id, "REJECTED") },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = redReject),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, redReject)
                                ) {
                                    Text("Từ chối")
                                }

                                // Nút Duyệt (UC_A08)
                                Button(
                                    onClick = { viewModel.changeBookingStatus(booking.id, "APPROVED") },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = greenApprove)
                                ) {
                                    Text("Duyệt")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}