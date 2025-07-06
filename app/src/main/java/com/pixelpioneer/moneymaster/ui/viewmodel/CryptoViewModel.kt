package com.pixelpioneer.moneymaster.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pixelpioneer.moneymaster.data.model.Asset
import com.pixelpioneer.moneymaster.data.repository.CoinCapRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CryptoViewModel(private val repository: CoinCapRepository) : ViewModel() {

    private val _assets = MutableStateFlow<List<Asset>>(emptyList())
    val assets: StateFlow<List<Asset>> get() = _assets

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    fun loadAssets(limit: Int = 10) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getAssets(limit)
            _assets.value = result
            _isLoading.value = false
        }
    }
}