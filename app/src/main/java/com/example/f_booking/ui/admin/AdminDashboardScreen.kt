package com.example.f_booking.ui.admin

import androidx.compose.foundation.background
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
fun AdminDashboardScreen(viewModel: AdminDashboardViewModel = viewModel()) {
    val blueAdmin = Color(0xFF1565C0)

    // Tự động tải dữ liệu khi vào tab Thống kê
    LaunchedEffect(Unit) { viewModel.fetchDashboard() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thống Kê Tổng Quan", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = blueAdmin)
            )
        }
    ) { padding ->
        val data = viewModel.dashboardData.value

        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (viewModel.isLoading.value) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = blueAdmin)
            }

            if (data != null) {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // UC_A11: Thống kê Doanh thu
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                        ) {
                            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Tổng Doanh Thu", color = blueAdmin, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("${data.totalRevenue} VNĐ", color = blueAdmin, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }

                    // UC_A12: Thống kê Lượt đặt
                    item {
                        Text("Lượt Đặt Theo Sân (Đã Duyệt)", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                    }
                    items(data.fieldStats) { stat ->
                        Row(
                            modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(8.dp)).padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(stat.fieldName, fontWeight = FontWeight.Medium)
                            Text("${stat.totalBookings} lượt", fontWeight = FontWeight.Bold, color = blueAdmin)
                        }
                    }

                    // UC_A10: Toàn bộ Lịch sử
                    item {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                        Text("Lịch Sử Giao Dịch Gần Đây", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                    items(data.history) { booking ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Mã: ${booking.id}", fontSize = 12.sp, color = Color.Gray)
                                Text("Sân: ${booking.fieldId?.name ?: "N/A"}", fontWeight = FontWeight.Bold)
                                Text("Ngày: ${booking.bookingDate} | Giờ: ${booking.startTime}-${booking.endTime}")

                                val statusColor = when(booking.status) {
                                    "APPROVED" -> Color(0xFF2E7D32)
                                    "REJECTED", "CANCELLED" -> Color.Red
                                    else -> Color(0xFFFFA000)
                                }
                                Text("Trạng thái: ${booking.status}", color = statusColor, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}