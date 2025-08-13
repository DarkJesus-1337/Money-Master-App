package com.pixelpioneer.moneymaster.preview

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pixelpioneer.moneymaster.preview.SampleData
import com.pixelpioneer.moneymaster.ui.components.common.items.BudgetOverviewItem
import com.pixelpioneer.moneymaster.ui.theme.MoneyMasterTheme

/**
 * Preview für die BudgetOverviewItem-Komponente
 */
@Preview(name = "Budget Item - Normale Nutzung", showBackground = false)
@Composable
fun BudgetOverviewItemPreview() {
    MoneyMasterTheme {
        Surface {
            BudgetOverviewItem(
                budget = SampleData.sampleBudgets[0]
            )
        }
    }
}

@Preview(name = "Budget Item - Fast voll", showBackground = false)
@Composable
fun BudgetOverviewItemHighUsagePreview() {
    val highUsageBudget = SampleData.sampleBudgets[0].copy(spent = 270.0)
    
    MoneyMasterTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            BudgetOverviewItem(
                budget = highUsageBudget
            )
        }
    }
}

@Preview(name = "Budget Item - Überschritten", showBackground = false)
@Composable
fun BudgetOverviewItemOverBudgetPreview() {
    val overBudget = SampleData.sampleBudgets[0].copy(spent = 320.0)
    
    MoneyMasterTheme(darkTheme = true) {
        Surface(modifier = Modifier.padding(16.dp)) {
            BudgetOverviewItem(
                budget = overBudget
            )
        }
    }
}