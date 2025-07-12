package com.pixelpioneer.moneymaster.ui.components.budget

import androidx.compose.runtime.Composable
import com.pixelpioneer.moneymaster.data.enums.BudgetPeriod

@Composable
fun getBudgetPeriodText(period: BudgetPeriod): String {
    return when (period) {
        BudgetPeriod.DAILY -> "Daily Budget"
        BudgetPeriod.WEEKLY -> "Weekly Budget"
        BudgetPeriod.MONTHLY -> "Monthly Budget"
        BudgetPeriod.YEARLY -> "Yearly Budget"
    }
}