package com.example.f_booking

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List // Thêm Icon danh sách
import androidx.compose.material.icons.filled.Person
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
import com.example.f_booking.ui.bookings.BookingHistoryScreen // Thêm màn hình lịch sử
import com.example.f_booking.utils.TokenManager

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }

    // Khởi tạo ProfileViewModel để dùng chung cho cả xem và sửa profile
    val profileViewModel: ProfileViewModel = viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            return ProfileViewModel(tokenManager) as T
        }
    })

    NavHost(navController = navController, startDestination = "login") {
        // --- Nhóm 1: Xác thực (Auth) ---
        composable("login") {
            LoginScreen(
                onLoginSuccess = { _, _ ->
                    navController.navigate("main") { popUpTo("login") { inclusive = true } }
                },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }
        composable("register") {
            RegisterScreen(
                onRegisterSuccess = { navController.navigate("login") },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // --- Nhóm 2: Màn hình chính có Bottom Bar ---
        composable("main") {
            MainContainer(navController, profileViewModel)
        }

        // --- Nhóm 3: Các màn hình phụ không có Bottom Bar ---
        composable("field_detail/{fieldId}") { backStackEntry ->
            val fieldId = backStackEntry.arguments?.getString("fieldId") ?: ""
            FieldDetailScreen(fieldId = fieldId, onBack = { navController.popBackStack() })
        }

        composable("edit_profile") {
            EditProfileScreen(viewModel = profileViewModel, onBack = { navController.popBackStack() })
        }
    }
}

@Composable
fun MainContainer(rootNavController: androidx.navigation.NavHostController, profileViewModel: ProfileViewModel) {
    val bottomNavController = rememberNavController()
    val greenPrimary = Color(0xFF2E7D32)

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                // 1. Mục Trang chủ (UC_U06)
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Trang chủ") },
                    selected = currentDestination?.hierarchy?.any { it.route == "home" } == true,
                    onClick = {
                        bottomNavController.navigate("home") {
                            popUpTo(bottomNavController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = greenPrimary,
                        selectedTextColor = greenPrimary,
                        unselectedIconColor = Color.Gray
                    )
                )

                // 2. Mục Lịch sử (UC_U11) - MỚI THÊM
                NavigationBarItem(
                    icon = { Icon(Icons.Default.List, contentDescription = null) },
                    label = { Text("Lịch sử") },
                    selected = currentDestination?.hierarchy?.any { it.route == "history" } == true,
                    onClick = {
                        bottomNavController.navigate("history") {
                            popUpTo(bottomNavController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = greenPrimary,
                        selectedTextColor = greenPrimary,
                        unselectedIconColor = Color.Gray
                    )
                )

                // 3. Mục Hồ sơ (UC_U04)
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Hồ sơ") },
                    selected = currentDestination?.hierarchy?.any { it.route == "profile" } == true,
                    onClick = {
                        bottomNavController.navigate("profile") {
                            popUpTo(bottomNavController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = greenPrimary,
                        selectedTextColor = greenPrimary,
                        unselectedIconColor = Color.Gray
                    )
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(onFieldClick = { id -> rootNavController.navigate("field_detail/$id") })
            }
            // 4. Màn hình Lịch sử - MỚI THÊM
            composable("history") {
                BookingHistoryScreen()
            }
            composable("profile") {
                ProfileScreen(
                    viewModel = profileViewModel,
                    onLogout = {
                        rootNavController.navigate("login") {
                            popUpTo("main") { inclusive = true }
                        }
                    },
                    onEditProfile = {
                        rootNavController.navigate("edit_profile")
                    }
                )
            }
        }
    }
}