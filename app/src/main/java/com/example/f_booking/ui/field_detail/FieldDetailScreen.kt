package com.example.f_booking.ui.field_detail

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FieldDetailScreen(
    fieldId: String,
    onBack: () -> Unit,
    viewModel: FieldDetailViewModel = viewModel()
) {
    val context = LocalContext.current
    val greenPrimary = Color(0xFF2E7D32)
    val calendar = Calendar.getInstance()

    // Khung giờ mẫu một sân thường hoạt động
    val timeBlocks = listOf("15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00")

    // UC_U09: Khởi tạo DatePicker
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val formattedDate = String.format("%d-%02d-%02d", year, month + 1, dayOfMonth)
            viewModel.onDateChange(formattedDate)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
    // Chặn chọn ngày trong quá khứ (Trừ đi 1 giây hiện tại)
    datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000

    LaunchedEffect(fieldId) { viewModel.fetchFieldDetail(fieldId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết & Đặt sân", color = Color.White) },
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
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                AsyncImage(
                    model = field.imageUrl ?: "https://via.placeholder.com/400",
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(220.dp),
                    contentScale = ContentScale.Crop
                )

                Column(modifier = Modifier.padding(16.dp)) {
                    Text(field.name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text("${field.pricePerHour} VNĐ/Giờ", color = greenPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)

                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                    Text("CHỌN NGÀY VÀ GIỜ ĐÁ", fontWeight = FontWeight.Bold, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))

                    // Ô Chọn Ngày
                    OutlinedTextField(
                        value = viewModel.selectedDate.value,
                        onValueChange = {},
                        label = { Text("Ngày đặt sân") },
                        modifier = Modifier.fillMaxWidth().clickable { datePickerDialog.show() },
                        enabled = false,
                        leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = Color.Black,
                            disabledBorderColor = greenPrimary,
                            disabledLabelColor = greenPrimary,
                            disabledLeadingIconColor = greenPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // UC_U09: Lưới giờ hiển thị trạng thái Real-time
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier.height(150.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(timeBlocks) { time ->
                            // Kiểm tra giờ đã bị đặt (ngoại trừ bị CANCELLED)
                            val isBooked = viewModel.bookedSlots.value.any { it.startTime == time && it.status != "CANCELLED" }
                            val isSelected = viewModel.selectedTime.value == time

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp)
                                    .background(
                                        color = when {
                                            isBooked -> Color.LightGray // Làm mờ giờ đã đặt
                                            isSelected -> greenPrimary
                                            else -> Color.White
                                        },
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable(enabled = !isBooked) {
                                        viewModel.selectedTime.value = time
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = time,
                                    color = if (isSelected) Color.White else if (isBooked) Color.DarkGray else Color.Black,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    viewModel.bookingMessage.value?.let {
                        Text(it, color = if (it.contains("thành công")) greenPrimary else Color.Red, modifier = Modifier.padding(bottom = 8.dp))
                    }

                    // UC_U10: Nút Đặt sân
                    Button(
                        onClick = { viewModel.bookField { /* Thành công */ } },
                        modifier = Modifier.fillMaxWidth().height(55.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = greenPrimary),
                        shape = RoundedCornerShape(12.dp),
                        // Chỉ cho phép bấm khi không tải dữ liệu và ĐÃ chọn giờ
                        enabled = !viewModel.isLoading.value && viewModel.selectedTime.value != null
                    ) {
                        if (viewModel.isLoading.value) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("XÁC NHẬN ĐẶT SÂN", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else if (viewModel.isLoading.value) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = greenPrimary)
            }
        }
    }
}