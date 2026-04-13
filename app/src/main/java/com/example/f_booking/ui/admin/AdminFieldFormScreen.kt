package com.example.f_booking.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.f_booking.model.FieldRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminFieldFormScreen(
    fieldId: String?, // Null hoặc Rỗng là Thêm Mới, có giá trị là Sửa
    viewModel: AdminFieldViewModel,
    onBack: () -> Unit
) {
    val isEditMode = !fieldId.isNullOrEmpty()
    val blueAdmin = Color(0xFF1565C0)

    // Các biến lưu dữ liệu Form
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("5_NGUOI") } // Mặc định là sân 5
    var pricePerHour by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var isActive by remember { mutableStateOf(true) }

    // Nếu là chế độ Sửa, gọi API tải dữ liệu sân đó về điền vào Form
    LaunchedEffect(fieldId) {
        if (isEditMode && fieldId != null) {
            viewModel.getFieldById(fieldId) { field ->
                name = field.name
                type = field.type
                pricePerHour = field.pricePerHour.toString()
                description = field.description ?: ""
                imageUrl = field.imageUrl ?: ""
                isActive = field.isActive ?: true
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Sửa thông tin sân" else "Thêm sân bóng", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = blueAdmin)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize().verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = name, onValueChange = { name = it },
                label = { Text("Tên sân bóng *") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Chọn loại sân
            Text("Loại sân *", fontWeight = FontWeight.Bold, color = Color.Gray)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 8.dp)) {
                listOf("5_NGUOI", "7_NGUOI", "11_NGUOI").forEach { fieldType ->
                    FilterChip(
                        selected = type == fieldType,
                        onClick = { type = fieldType },
                        label = { Text(fieldType.replace("_NGUOI", " Người")) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = blueAdmin, selectedLabelColor = Color.White)
                    )
                }
            }

            OutlinedTextField(
                value = pricePerHour, onValueChange = { pricePerHour = it },
                label = { Text("Giá thuê/Giờ (VNĐ) *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = description, onValueChange = { description = it },
                label = { Text("Mô tả sân") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = imageUrl, onValueChange = { imageUrl = it },
                label = { Text("Link ảnh sân (URL)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            // UC_A06: Thay đổi trạng thái sân (Đang hoạt động / Bảo trì)
            if (isEditMode) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Text("Trạng thái sân:", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Switch(checked = isActive, onCheckedChange = { isActive = it })
                    Text(if (isActive) "Hoạt động" else "Bảo trì", color = if (isActive) Color(0xFF2E7D32) else Color.Red, modifier = Modifier.padding(start = 8.dp))
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            viewModel.actionMessage.value?.let {
                Text(it, color = if (it.contains("thành công")) Color(0xFF2E7D32) else Color.Red)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val request = FieldRequest(
                        name = name, type = type, pricePerHour = pricePerHour.toIntOrNull() ?: 0,
                        description = description, imageUrl = imageUrl, isActive = isActive
                    )
                    if (isEditMode) viewModel.updateField(fieldId!!, request, onBack)
                    else viewModel.createField(request, onBack)
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = blueAdmin),
                shape = RoundedCornerShape(12.dp),
                enabled = name.isNotBlank() && pricePerHour.isNotBlank() && !viewModel.isLoading.value
            ) {
                if (viewModel.isLoading.value) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                else Text(if (isEditMode) "LƯU THAY ĐỔI" else "TẠO SÂN MỚI", fontWeight = FontWeight.Bold)
            }
        }
    }
}