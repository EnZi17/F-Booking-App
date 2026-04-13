package com.example.f_booking

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.*
import com.example.f_booking.ui.home.HomeScreen
import com.example.f_booking.ui.login.LoginScreen
import com.example.f_booking.ui.login.RegisterScreen
import com.example.f_booking.ui.profile.ProfileScreen
import com.example.f_booking.ui.profile.ProfileViewModel
import com.example.f_booking.ui.profile.EditProfileScreen
import com.example.f_booking.ui.field_detail.FieldDetailScreen
import com.example.f_booking.ui.bookings.BookingHistoryScreen
import com.example.f_booking.utils.TokenManager

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }

    val profileViewModel: ProfileViewModel = viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            return ProfileViewModel(tokenManager) as T
        }
    })

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                // BƯỚC QUAN TRỌNG CHO UC_A01: Kiểm tra Role để rẽ nhánh
                onLoginSuccess = { _, role ->
                    if (role == "ADMIN") {
                        navController.navigate("admin_main") { popUpTo("login") { inclusive = true } }
                    } else {
                        navController.navigate("main") { popUpTo("login") { inclusive = true } }
                    }
                },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }
        composable("register") {
            RegisterScreen(onRegisterSuccess = { navController.navigate("login") }, onNavigateBack = { navController.popBackStack() })
        }

        // --- Giao diện Khách hàng ---
        composable("main") {
            MainContainer(navController, profileViewModel)
        }

        // --- Giao diện Admin (MỚI) ---
        composable("admin_main") {
            AdminContainer(navController, profileViewModel)
        }

        // --- Các màn hình phụ ---
        composable("field_detail/{fieldId}") { backStackEntry ->
            val fieldId = backStackEntry.arguments?.getString("fieldId") ?: ""
            FieldDetailScreen(fieldId = fieldId, onBack = { navController.popBackStack() })
        }
        composable("edit_profile") {
            EditProfileScreen(viewModel = profileViewModel, onBack = { navController.popBackStack() })
        }
    }
}

// ... [GIỮ NGUYÊN HÀM MainContainer CỦA KHÁCH HÀNG Ở ĐÂY] ...
@Composable
fun MainContainer(rootNavController: androidx.navigation.NavHostController, profileViewModel: ProfileViewModel) {
    val bottomNavController = rememberNavController()
    val greenPrimary = Color(0xFF2E7D32)
    val adminFieldViewModel: com.example.f_booking.ui.admin.AdminFieldViewModel = viewModel()

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Trang chủ") },
                    selected = currentDestination?.hierarchy?.any { it.route == "home" } == true,
                    onClick = { bottomNavController.navigate("home") { launchSingleTop = true } },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = greenPrimary, selectedTextColor = greenPrimary)
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.List, contentDescription = null) },
                    label = { Text("Lịch sử") },
                    selected = currentDestination?.hierarchy?.any { it.route == "history" } == true,
                    onClick = { bottomNavController.navigate("history") { launchSingleTop = true } },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = greenPrimary, selectedTextColor = greenPrimary)
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Hồ sơ") },
                    selected = currentDestination?.hierarchy?.any { it.route == "profile" } == true,
                    onClick = { bottomNavController.navigate("profile") { launchSingleTop = true } },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = greenPrimary, selectedTextColor = greenPrimary)
                )
            }
        }
    ) { innerPadding ->
        NavHost(bottomNavController, startDestination = "home", modifier = Modifier.padding(innerPadding)) {
            composable("home") { HomeScreen(onFieldClick = { id -> rootNavController.navigate("field_detail/$id") }) }
            composable("history") { BookingHistoryScreen() }
            composable("profile") {
                ProfileScreen(
                    viewModel = profileViewModel,
                    onLogout = { rootNavController.navigate("login") { popUpTo("main") { inclusive = true } } },
                    onEditProfile = { rootNavController.navigate("edit_profile") }
                )
            }
        }
    }
}

// ========================================================
// HỆ THỐNG GIAO DIỆN QUẢN TRỊ VIÊN (ADMIN CONTAINER)
// ========================================================
@Composable
fun AdminContainer(rootNavController: androidx.navigation.NavHostController, profileViewModel: ProfileViewModel) {
    val bottomNavController = rememberNavController()
    val blueAdmin = Color(0xFF1565C0) // Dùng màu Xanh dương để phân biệt với App Khách
    val adminFieldViewModel: com.example.f_booking.ui.admin.AdminFieldViewModel = viewModel()
    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                // Mục 1: Thống kê (Màn hình chính)
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Thống kê") },
                    selected = currentDestination?.hierarchy?.any { it.route == "admin_dashboard" } == true,
                    onClick = { bottomNavController.navigate("admin_dashboard") { launchSingleTop = true } },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = blueAdmin, selectedTextColor = blueAdmin)
                )
                // Mục 2: Quản lý sân
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Place, contentDescription = null) },
                    label = { Text("Sân bóng") },
                    selected = currentDestination?.hierarchy?.any { it.route == "admin_fields" } == true,
                    onClick = { bottomNavController.navigate("admin_fields") { launchSingleTop = true } },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = blueAdmin, selectedTextColor = blueAdmin)
                )
                // Mục 3: Duyệt lịch
                NavigationBarItem(
                    icon = { Icon(Icons.Default.DateRange, contentDescription = null) },
                    label = { Text("Duyệt đơn") },
                    selected = currentDestination?.hierarchy?.any { it.route == "admin_bookings" } == true,
                    onClick = { bottomNavController.navigate("admin_bookings") { launchSingleTop = true } },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = blueAdmin, selectedTextColor = blueAdmin)
                )
                // Mục 4: Hồ sơ & Đăng xuất (UC_A02)
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Cá nhân") },
                    selected = currentDestination?.hierarchy?.any { it.route == "admin_profile" } == true,
                    onClick = { bottomNavController.navigate("admin_profile") { launchSingleTop = true } },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = blueAdmin, selectedTextColor = blueAdmin)
                )
            }
        }
    ) { innerPadding ->
        NavHost(bottomNavController, startDestination = "admin_dashboard", modifier = Modifier.padding(innerPadding)) {
            composable("admin_dashboard") {
                com.example.f_booking.ui.admin.AdminDashboardScreen()
            }
            // 1. Màn hình Danh sách Sân
            composable("admin_fields") {
                com.example.f_booking.ui.admin.AdminFieldsScreen(
                    viewModel = adminFieldViewModel,
                    onNavigateToAddField = { bottomNavController.navigate("admin_add_field") },
                    onNavigateToEditField = { fieldId -> bottomNavController.navigate("admin_edit_field/$fieldId") }
                )
            }

            // 2. Màn hình Thêm Sân mới
            composable("admin_add_field") {
                com.example.f_booking.ui.admin.AdminFieldFormScreen(
                    fieldId = null,
                    viewModel = adminFieldViewModel,
                    onBack = { bottomNavController.popBackStack() }
                )
            }

            // 3. Màn hình Sửa Sân
            composable("admin_edit_field/{fieldId}") { backStackEntry ->
                val fieldId = backStackEntry.arguments?.getString("fieldId")
                com.example.f_booking.ui.admin.AdminFieldFormScreen(
                    fieldId = fieldId,
                    viewModel = adminFieldViewModel,
                    onBack = { bottomNavController.popBackStack() }
                )
            }

            composable("admin_bookings") {
                com.example.f_booking.ui.admin.AdminBookingScreen()
            }
            composable("admin_profile") {
                ProfileScreen(
                    viewModel = profileViewModel,
                    onLogout = { rootNavController.navigate("login") { popUpTo("admin_main") { inclusive = true } } },
                    onEditProfile = { rootNavController.navigate("edit_profile") }
                )
            }
        }
    }
}