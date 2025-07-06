package com.pixelpioneer.moneymaster.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pixelpioneer.moneymaster.data.repository.CoinCapRepository

class CryptoViewModelFactory(private val repository: CoinCapRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CryptoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CryptoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}