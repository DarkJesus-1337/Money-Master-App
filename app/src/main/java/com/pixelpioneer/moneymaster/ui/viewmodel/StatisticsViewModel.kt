package com.pixelpioneer.moneymaster.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pixelpioneer.moneymaster.R
import com.pixelpioneer.moneymaster.core.util.UiState
import com.pixelpioneer.moneymaster.data.model.CategoryStats
import com.pixelpioneer.moneymaster.data.model.MonthlyTrend
import com.pixelpioneer.moneymaster.data.model.StatisticsOverview
import com.pixelpioneer.moneymaster.data.repository.CategoryRepository
import com.pixelpioneer.moneymaster.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

/**
 * ViewModel for managing and calculating statistics related to transactions and categories.
 *
 * Loads statistical overviews, category statistics, and monthly trends.
 * Provides UI state flows for statistics and trends.
 *
 * @property transactionRepository Repository for transaction data access.
 * @property categoryRepository Repository for category data access.
 * @property context Application context for accessing resources.
 */
@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _statisticsState = MutableStateFlow<UiState<StatisticsOverview>>(UiState.Loading)
    val statisticsState: StateFlow<UiState<StatisticsOverview>> = _statisticsState

    private val _categoryStatsState =
        MutableStateFlow<UiState<List<CategoryStats>>>(UiState.Loading)
    val categoryStatsState: StateFlow<UiState<List<CategoryStats>>> = _categoryStatsState

    private val _monthlyTrendsState = MutableStateFlow<UiState<List<MonthlyTrend>>>(UiState.Loading)
    val monthlyTrendsState: StateFlow<UiState<List<MonthlyTrend>>> = _monthlyTrendsState

    init {
        loadStatistics()
        loadCategoryStats()
        loadMonthlyTrends()
    }

    /**
     * Loads general statistics data including monthly income, expenses, and balance.
     * Calculates daily and weekly expense averages.
     */
    private fun loadStatistics() {
        viewModelScope.launch {
            try {
                _statisticsState.value = UiState.Loading

                val calendar = Calendar.getInstance()
                val currentMonth = calendar.apply {
                    set(Calendar.DAY_OF_MONTH, 1)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis

                val nextMonth = calendar.apply {
                    add(Calendar.MONTH, 1)
                }.timeInMillis

                transactionRepository.getTransactionsWithCategoryByDateRange(
                    currentMonth,
                    nextMonth
                )
                    .catch { e ->
                        _statisticsState.value =
                            UiState.Error(
                                e.message ?: context.getString(R.string.error_loading_statistics)
                            )
                    }
                    .collect { transactions ->
                        transactionRepository.getTotalIncomeSync()
                        transactionRepository.getTotalExpensesSync()
                        val totalTransactions = transactionRepository.getTransactionCountSync()

                        val monthlyIncome = transactions
                            .filter { !it.transaction.isExpense }
                            .sumOf { it.transaction.amount }

                        val monthlyExpenses = transactions
                            .filter { it.transaction.isExpense }
                            .sumOf { it.transaction.amount }

                        val monthlyBalance = monthlyIncome - monthlyExpenses

                        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                        val avgDailyExpenses = monthlyExpenses / daysInMonth
                        val avgWeeklyExpenses = avgDailyExpenses * 7

                        val overview = StatisticsOverview(
                            monthlyIncome = monthlyIncome,
                            monthlyExpenses = monthlyExpenses,
                            monthlyBalance = monthlyBalance,
                            avgDailyExpenses = avgDailyExpenses,
                            avgWeeklyExpenses = avgWeeklyExpenses,
                            totalTransactions = totalTransactions,
                            monthlyTransactions = transactions.size
                        )

                        _statisticsState.value = UiState.Success(overview)
                    }
            } catch (e: Exception) {
                _statisticsState.value =
                    UiState.Error(e.message ?: context.getString(R.string.error_unknown))
            }
        }
    }

    /**
     * Loads statistics for each category, including total amount and transaction count.
     */
    private fun loadCategoryStats() {
        viewModelScope.launch {
            try {
                _categoryStatsState.value = UiState.Loading

                combine(
                    categoryRepository.allCategories,
                    transactionRepository.allTransactionsWithCategory
                ) { categories, transactions ->
                    val expenseTransactions = transactions.filter { it.isExpense }

                    categories.mapNotNull { category ->
                        val categoryTransactions =
                            expenseTransactions.filter { it.category.id == category.id }
                        if (categoryTransactions.isNotEmpty()) {
                            CategoryStats(
                                category = category,
                                amount = categoryTransactions.sumOf { it.amount.toDouble() },
                                transactionCount = categoryTransactions.size
                            )
                        } else null
                    }.sortedByDescending { it.amount }
                }
                    .catch { e ->
                        _categoryStatsState.value = UiState.Error(
                            e.message
                                ?: context.getString(R.string.error_loading_category_statistics)
                        )
                    }
                    .collect { categoryStats ->
                        if (categoryStats.isEmpty()) {
                            _categoryStatsState.value = UiState.Empty
                        } else {
                            _categoryStatsState.value = UiState.Success(categoryStats)
                        }
                    }
            } catch (e: Exception) {
                _categoryStatsState.value =
                    UiState.Error(e.message ?: context.getString(R.string.error_unknown))
            }
        }
    }

    /**
     * Loads monthly trends data showing income, expenses, and transaction count by month.
     */
    private fun loadMonthlyTrends() {
        viewModelScope.launch {
            try {
                _monthlyTrendsState.value = UiState.Loading

                transactionRepository.allTransactionsWithCategory
                    .catch { e ->
                        _monthlyTrendsState.value =
                            UiState.Error(
                                e.message ?: context.getString(R.string.error_loading_trend_data)
                            )
                    }
                    .collect { transactions ->
                        val monthlyTrends = transactions
                            .groupBy { transactionWithCategory ->
                                val calendar = Calendar.getInstance()
                                calendar.timeInMillis = transactionWithCategory.date
                                "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH)}"
                            }
                            .map { (_, monthTransactions) ->
                                val calendar = Calendar.getInstance()
                                calendar.timeInMillis = monthTransactions.first().date

                                val monthlyIncome = transactions
                                    .filter { !it.isExpense }
                                    .sumOf { it.amount }

                                val monthlyExpenses = transactions
                                    .filter { it.isExpense }
                                    .sumOf { it.amount }

                                val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.GERMAN)
                                val monthYear = dateFormat.format(calendar.time)

                                MonthlyTrend(
                                    monthYear = monthYear,
                                    income = monthlyIncome,
                                    expenses = monthlyExpenses,
                                    balance = monthlyIncome - monthlyExpenses,
                                    transactionCount = monthTransactions.size
                                )
                            }
                            .sortedWith(compareByDescending<MonthlyTrend> { trend ->
                                SimpleDateFormat(
                                    "MMMM yyyy",
                                    Locale.GERMAN
                                ).parse(trend.monthYear)?.time ?: 0
                            })
                            .take(12)

                        if (monthlyTrends.isEmpty()) {
                            _monthlyTrendsState.value = UiState.Empty
                        } else {
                            _monthlyTrendsState.value = UiState.Success(monthlyTrends)
                        }
                    }
            } catch (e: Exception) {
                _monthlyTrendsState.value =
                    UiState.Error(e.message ?: context.getString(R.string.error_unknown))
            }
        }
    }
}