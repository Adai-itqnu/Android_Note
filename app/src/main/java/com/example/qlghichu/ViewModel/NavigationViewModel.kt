package com.example.qlghichu.ViewModel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NavigationViewModel : ViewModel() {
    private val _selectedTab = MutableStateFlow("Ghi chú")
    val selectedTab: StateFlow<String> = _selectedTab

    fun setSelectedTab(tab: String) {
        _selectedTab.value = tab
    }
}