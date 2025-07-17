package com.pixelpioneer.moneymaster.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pixelpioneer.moneymaster.R
import com.pixelpioneer.moneymaster.data.model.Asset
import com.pixelpioneer.moneymaster.data.model.HistoryDataPoint
import com.pixelpioneer.moneymaster.data.repository.CoinCapRepository
import com.pixelpioneer.moneymaster.util.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for managing cryptocurrency assets and their historical data.
 *
 * Handles loading crypto assets, selecting an asset, and loading its history.
 * Provides UI state flows for assets and history.
 *
 * @property repository Repository for accessing CoinCap API data.
 */
class CryptoViewModel(
    private val repository: CoinCapRepository,
    private val context: Context
) : ViewModel() {

    private val _cryptoAssetsState = MutableStateFlow<UiState<List<Asset>>>(UiState.Loading)
    val cryptoAssetsState: StateFlow<UiState<List<Asset>>> = _cryptoAssetsState

    private val _cryptoHistoryState =
        MutableStateFlow<UiState<List<HistoryDataPoint>>>(UiState.Loading)
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
                    if (_selectedAsset.value == null) {
                        selectAsset(assets.first())
                    }
                }
            } catch (e: Exception) {
                _cryptoAssetsState.value =
                    UiState.Error(e.message ?: context.getString(R.string.error_loading_crypto))
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
                _cryptoHistoryState.value =
                    UiState.Error(e.message ?: context.getString(R.string.error_loading_history))
            }
        }
    }
}