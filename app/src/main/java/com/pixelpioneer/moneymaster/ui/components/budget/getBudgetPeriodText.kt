package com.pixelpioneer.moneymaster.ui.components.budget

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.pixelpioneer.moneymaster.R
import com.pixelpioneer.moneymaster.data.enums.BudgetPeriod

@Composable
fun getBudgetPeriodText(period: BudgetPeriod): String {
    return when (period) {
        BudgetPeriod.DAILY -> stringResource(R.string.budget_period_daily_budget)
        BudgetPeriod.WEEKLY -> stringResource(R.string.budget_period_weekly_budget)
        BudgetPeriod.MONTHLY -> stringResource(R.string.budget_period_monthly_budget)
        BudgetPeriod.YEARLY -> stringResource(R.string.budget_period_yearly_budget)
    }
}