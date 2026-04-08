package com.example.f_booking.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onLogout: () -> Unit,       // Tham số cho UC_U03 (Đăng xuất)
    onEditProfile: () -> Unit    // Tham số mới cho UC_U05 (Cập nhật thông tin)
) {
    val greenPrimary = Color(0xFF2E7D32)

    // UC_U04: Tự động lấy thông tin khi màn hình hiển thị
    LaunchedEffect(Unit) {
        viewModel.fetchProfile()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Biểu tượng cá nhân
        Icon(
            Icons.Default.Person,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = greenPrimary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Hiển thị thông tin người dùng (UC_U04)
        if (viewModel.isLoading.value) {
            CircularProgressIndicator(color = greenPrimary)
        } else {
            viewModel.userProfile.value?.let { user ->
                Text(text = user.fullName, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text(text = user.phoneNumber, fontSize = 16.sp, color = Color.Gray)

                Spacer(modifier = Modifier.height(8.dp))

                // Nút Chỉnh sửa hồ sơ (Mở màn hình UC_U05)
                OutlinedButton(
                    onClick = onEditProfile,
                    modifier = Modifier.padding(top = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, greenPrimary)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp), tint = greenPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Chỉnh sửa thông tin", color = greenPrimary)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Nút Đăng xuất (UC_U03)
        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("ĐĂNG XUẤT", fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}