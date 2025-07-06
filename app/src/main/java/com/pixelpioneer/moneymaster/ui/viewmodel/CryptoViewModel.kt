package com.pixelpioneer.moneymaster.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pixelpioneer.moneymaster.data.model.Asset
import com.pixelpioneer.moneymaster.data.model.HistoryDataPoint
import com.pixelpioneer.moneymaster.data.repository.CoinCapRepository
import com.pixelpioneer.moneymaster.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CryptoViewModel(
    private val repository: CoinCapRepository
) : ViewModel() {

    private val _cryptoAssetsState = MutableStateFlow<UiState<List<Asset>>>(UiState.Loading)
    val cryptoAssetsState: StateFlow<UiState<List<Asset>>> = _cryptoAssetsState

    private val _cryptoHistoryState = MutableStateFlow<UiState<List<HistoryDataPoint>>>(UiState.Loading)
    val cryptoHistoryState: StateFlow<UiState<List<HistoryDataPoint>>> = _cryptoHistoryState

    private val _selectedAsset = MutableStateFlow<Asset?>(null)
    val selectedAsset: StateFlow<Asset?> = _selectedAsset

    fun loadCryptoAssets(limit: Int = 10) {
        viewModelScope.launch {
            try {
                _cryptoAssetsState.value = UiState.Loading
                val assets = repository.getAssets(limit)

                if (assets.isEmpty()) {
                    _cryptoAssetsState.value = UiState.Empty
                } else {
                    _cryptoAssetsState.value = UiState.Success(assets)
                    // Automatically select the first asset if none is selected
                    if (_selectedAsset.value == null) {
                        selectAsset(assets.first())
                    }
                }
            } catch (e: Exception) {
                _cryptoAssetsState.value = UiState.Error(e.message ?: "Fehler beim Laden der Krypto-Daten")
            }
        }
    }

    fun selectAsset(asset: Asset) {
        _selectedAsset.value = asset
        loadAssetHistory(asset.id)
    }

    private fun loadAssetHistory(assetId: String, interval: String = "h1", daysBack: Int = 7) {
        viewModelScope.launch {
            try {
                _cryptoHistoryState.value = UiState.Loading
                val historyData = repository.getAssetHistory(
                    assetId = assetId,
                    interval = interval,
                    daysBack = daysBack
                )

                if (historyData.isEmpty()) {
                    _cryptoHistoryState.value = UiState.Empty
                } else {
                    _cryptoHistoryState.value = UiState.Success(historyData)
                }
            } catch (e: Exception) {
                _cryptoHistoryState.value = UiState.Error(e.message ?: "Fehler beim Laden der Historie")
            }
        }
    }

    fun refreshData() {
        loadCryptoAssets()
        _selectedAsset.value?.let { asset ->
            loadAssetHistory(asset.id)
        }
    }

    fun changeTimeInterval(interval: String, daysBack: Int) {
        _selectedAsset.value?.let { asset ->
            loadAssetHistory(asset.id, interval, daysBack)
        }
    }
}