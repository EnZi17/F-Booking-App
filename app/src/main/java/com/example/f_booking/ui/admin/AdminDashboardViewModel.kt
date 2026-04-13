package com.example.f_booking.ui.admin

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.f_booking.model.DashboardResponse
import com.example.f_booking.network.ApiService
import com.example.f_booking.network.RetrofitClient
import kotlinx.coroutines.launch

class AdminDashboardViewModel : ViewModel() {
    private val apiService = RetrofitClient.instance.create(ApiService::class.java)

    var dashboardData = mutableStateOf<DashboardResponse?>(null)
    var isLoading = mutableStateOf(false)

    fun fetchDashboard() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = apiService.getAdminDashboard()
                if (response.isSuccessful) {
                    dashboardData.value = response.body()
                }
            } catch (e: Exception) {
                println("LỖI DASHBOARD: ${e.message}")
            } finally {
                isLoading.value = false
            }
        }
    }
}