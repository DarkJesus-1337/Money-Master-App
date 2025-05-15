package com.pixelpioneer.moneymaster.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pixelpioneer.moneymaster.data.model.Transaction
import com.pixelpioneer.moneymaster.util.FormatUtils
import java.util.Date

/**
 * A component for displaying a list of recent transactions
 */
@Composable
fun RecentTransactionsList(
    transactions: List<Transaction>,
    onTransactionClick: (Transaction) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        items(transactions) { transaction ->
            RecentTransactionItem(
                transaction = transaction,
                onClick = { onTransactionClick(transaction) }
            )
            
            Divider(
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

/**
 * A component for displaying a single transaction in the recent transactions list
 */
@Composable
fun RecentTransactionItem(
    transaction: Transaction,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Category color indicator
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(transaction.category.color)),
            contentAlignment = Alignment.Center
        ) {
            // If there's an icon for the category, display it here
            // For now, just showing the first letter of the category name
            Text(
                text = transaction.category.name.first().toString(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Transaction details
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = transaction.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = transaction.category.name,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = FormatUtils.formatDate(transaction.date),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Amount
        Column(
            horizontalAlignment = Alignment.End
        ) {
            val amountColor = if (transaction.isExpense) {
                MaterialTheme.colorScheme.error
            } else {
                Color.Green
            }
            
            val prefix = if (transaction.isExpense) "-" else "+"
            
            Text(
                text = "$prefix${FormatUtils.formatCurrency(transaction.amount)}",
                style = MaterialTheme.typography.titleMedium,
                color = amountColor,
                fontWeight = FontWeight.Bold
            )
            
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "View Details",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * A component for displaying a compact version of recent transactions (for dashboard)
 */
@Composable
fun BudgetOverview(
    budgets: List<com.pixelpioneer.moneymaster.data.model.Budget>,
    onBudgetsClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onBudgetsClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Display top 2 budgets by percentage used
            val topBudgets = budgets.sortedByDescending { it.spent / it.amount }.take(2)
            
            topBudgets.forEach { budget ->
                BudgetOverviewItem(budget = budget)
                
                if (budget != topBudgets.last()) {
                    Divider(modifier = Modifier.padding(vertical = 12.dp))
                }
            }
            
            // If there are more budgets, show a "View All" text
            if (budgets.size > 2) {
                Divider(modifier = Modifier.padding(vertical = 12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "View All Budgets",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "View All",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

/**
 * A component for displaying a single budget item in the budget overview
 */
@Composable
fun BudgetOverviewItem(
    budget: com.pixelpioneer.moneymaster.data.model.Budget
) {
    val progress = if (budget.amount > 0) {
        (budget.spent / budget.amount).coerceIn(0.0, 1.0)
    } else {
        0.0
    }
    
    val progressColor = when {
        progress >= 0.9 -> MaterialTheme.colorScheme.error
        progress >= 0.7 -> MaterialTheme.colorScheme.errorContainer
        else -> MaterialTheme.colorScheme.primary
    }
    
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category indicator
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(Color(budget.category.color))
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = budget.category.name,
                    style = MaterialTheme.typography.titleSmall
                )
            }
            
            // Percentage
            Text(
                text = FormatUtils.formatPercentage(progress),
                style = MaterialTheme.typography.titleSmall,
                color = progressColor
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Progress bar
        androidx.compose.material3.LinearProgressIndicator(
            progress = progress.toFloat(),
            modifier = Modifier.fillMaxWidth(),
            color = progressColor
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Amount information
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${FormatUtils.formatCurrency(budget.spent)} of ${FormatUtils.formatCurrency(budget.amount)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = "Remaining: ${FormatUtils.formatCurrency(budget.amount - budget.spent)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}