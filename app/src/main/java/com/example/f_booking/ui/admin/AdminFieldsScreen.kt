package com.example.f_booking.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
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
fun AdminFieldsScreen(
    onNavigateToAddField: () -> Unit,
    onNavigateToEditField: (String) -> Unit,
    viewModel: AdminFieldViewModel = viewModel()
) {
    val blueAdmin = Color(0xFF1565C0)

    // Tự động lấy danh sách khi vào màn hình
    LaunchedEffect(Unit) { viewModel.fetchAllFields() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quản lý Sân bóng", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = blueAdmin)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddField,
                containerColor = blueAdmin,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Thêm sân")
            }
        }
    ) { padding ->
        if (viewModel.isLoading.value) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = blueAdmin)
        }

        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(viewModel.fields.value) { field ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(field.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text("Loại: ${field.type} - Giá: ${field.pricePerHour}đ/h", color = Color.Gray, fontSize = 14.sp)
                            // Trạng thái sân
                            Text(
                                text = if (field.isActive != false) "Đang hoạt động" else "Đang bảo trì",
                                color = if (field.isActive != false) Color(0xFF2E7D32) else Color.Red,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        // Nút chỉnh sửa
                        IconButton(onClick = { onNavigateToEditField(field.id) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Sửa", tint = blueAdmin)
                        }
                    }
                }
            }
        }
    }
}