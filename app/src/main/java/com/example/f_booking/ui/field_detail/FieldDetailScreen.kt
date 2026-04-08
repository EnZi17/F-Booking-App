package com.example.f_booking.ui.field_detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FieldDetailScreen(
    fieldId: String,
    onBack: () -> Unit,
    viewModel: FieldDetailViewModel = viewModel()
) {
    val greenPrimary = Color(0xFF2E7D32)

    // Gọi API lấy chi tiết khi màn hình mở ra
    LaunchedEffect(fieldId) {
        viewModel.fetchFieldDetail(fieldId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết sân", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = greenPrimary)
            )
        }
    ) { padding ->
        val field = viewModel.field.value
        if (field != null) {
            Column(modifier = Modifier.padding(padding).fillMaxSize()) {
                AsyncImage(
                    model = field.imageUrl ?: "https://via.placeholder.com/400",
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(250.dp),
                    contentScale = ContentScale.Crop
                )

                Column(modifier = Modifier.padding(16.dp)) {
                    Text(field.name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text("Loại sân: ${field.type}", color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "${field.pricePerHour} VNĐ/Giờ",
                        fontSize = 20.sp,
                        color = greenPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Mô tả:", fontWeight = FontWeight.Bold)
                    Text(field.description ?: "Không có mô tả.")

                    Spacer(modifier = Modifier.weight(1f))

                    // Thông báo trạng thái đặt sân
                    viewModel.bookingMessage.value?.let {
                        Text(it, color = if (it.contains("thành công")) greenPrimary else Color.Red)
                    }

                    Button(
                        onClick = { viewModel.bookField { /* Có thể chuyển về Home */ } },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = greenPrimary),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !viewModel.isLoading.value
                    ) {
                        Text("ĐẶT SÂN NGAY", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else if (viewModel.isLoading.value) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator(color = greenPrimary)
            }
        }
    }
}