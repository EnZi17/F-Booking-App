package com.example.f_booking.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.f_booking.model.Field

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onFieldClick: (String) -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val greenPrimary = Color(0xFF2E7D32)
    val fieldTypesMap = mapOf(
        "Sân 5" to "5_NGUOI",
        "Sân 7" to "7_NGUOI",
        "Sân 11" to "11_NGUOI"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("F-Booking", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = greenPrimary)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {

            // UC_U07: Thanh lọc theo loại sân
            Text(
                "Loại sân bóng",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                fontWeight = FontWeight.Bold
            )
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = viewModel.selectedType.value == null,
                        onClick = { viewModel.applyFilter(null, null) },
                        label = { Text("Tất cả") }
                    )
                }
                // 2. Duyệt qua các key của Map để hiển thị chữ "Sân 5", "Sân 7"
                items(fieldTypesMap.keys.toList()) { label ->
                    val dbValue = fieldTypesMap[label] // Lấy giá trị "5_NGUOI" tương ứng
                    FilterChip(
                        selected = viewModel.selectedType.value == dbValue,
                        onClick = { viewModel.applyFilter(dbValue, null) }, // Gửi "5_NGUOI" lên server
                        label = { Text(label) }
                    )
                }
            }

            if (viewModel.isLoading.value) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = greenPrimary)
            }

            // UC_U06: Danh sách sân bóng dưới dạng Card
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(viewModel.fields.value) { field ->
                    FieldCard(field = field, onClick = { onFieldClick(field.id) })
                }
            }
        }
    }
}

@Composable
fun FieldCard(field: Field, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            AsyncImage(
                model = field.imageUrl ?: "https://via.placeholder.com/400x200",
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(160.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(field.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(field.type, color = Color.Gray)
                    Text(
                        "${field.pricePerHour}đ/giờ",
                        color = Color(0xFF2E7D32),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}