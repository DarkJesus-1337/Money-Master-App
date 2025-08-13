package com.pixelpioneer.moneymaster.preview

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pixelpioneer.moneymaster.data.enums.BudgetPeriod
import com.pixelpioneer.moneymaster.preview.SampleData
import com.pixelpioneer.moneymaster.ui.components.common.items.BudgetItem
import com.pixelpioneer.moneymaster.ui.theme.MoneyMasterTheme

/**
 * Preview für die BudgetItem-Komponente
 */
@Preview(name = "Budget Item - Normal", showBackground = false)
@Composable
fun BudgetItemPreview() {
    MoneyMasterTheme {
        Surface(modifier = Modifier.padding(8.dp)) {
            BudgetItem(
                budget = SampleData.sampleBudgets[0],
                onClick = {}
            )
        }
    }
}

@Preview(name = "Budget Item - Fast aufgebraucht", showBackground = false)
@Composable
fun BudgetItemHighUsagePreview() {
    val highUsageBudget = SampleData.sampleBudgets[0].copy(
        spent = SampleData.sampleBudgets[0].amount * 0.85
    )
    
    MoneyMasterTheme {
        Surface(modifier = Modifier.padding(8.dp)) {
            BudgetItem(
                budget = highUsageBudget,
                onClick = {},
                onEdit = {},
                onDelete = {}
            )
        }
    }
}

@Preview(name = "Budget Item - Kritisch", showBackground = false)
@Composable
fun BudgetItemCriticalUsagePreview() {
    val criticalBudget = SampleData.sampleBudgets[0].copy(
        spent = SampleData.sampleBudgets[0].amount * 0.95
    )
    
    MoneyMasterTheme {
        Surface(modifier = Modifier.padding(8.dp)) {
            BudgetItem(
                budget = criticalBudget,
                onClick = {},
                onEdit = {},
                onDelete = {}
            )
        }
    }
}

@Preview(name = "Budget Item - Überschritten", showBackground = false)
@Composable
fun BudgetItemOverBudgetPreview() {
    val overBudget = SampleData.sampleBudgets[0].copy(
        spent = SampleData.sampleBudgets[0].amount * 1.1
    )
    
    MoneyMasterTheme(darkTheme = true) {
        Surface(modifier = Modifier.padding(8.dp)) {
            BudgetItem(
                budget = overBudget,
                onClick = {},
                onEdit = {},
                onDelete = {}
            )
        }
    }
}