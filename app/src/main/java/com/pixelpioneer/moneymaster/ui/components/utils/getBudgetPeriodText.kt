package com.pixelpioneer.moneymaster.ui.components.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.pixelpioneer.moneymaster.R
import com.pixelpioneer.moneymaster.data.enums.BudgetPeriod

/**
 * Returns a localized string representing the given budget period.
 *
 * @param period The budget period to be displayed.
 * @return The localized string for the specified period.
 */
@Composable
fun getBudgetPeriodText(period: BudgetPeriod): String {
    return when (period) {
        BudgetPeriod.DAILY -> stringResource(R.string.budget_period_daily_full)
        BudgetPeriod.WEEKLY -> stringResource(R.string.budget_period_weekly_full)
        BudgetPeriod.MONTHLY -> stringResource(R.string.budget_period_monthly_full)
        BudgetPeriod.YEARLY -> stringResource(R.string.budget_period_yearly_full)
    }
}