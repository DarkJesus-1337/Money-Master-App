package com.pixelpioneer.moneymaster.data.mapper

import com.pixelpioneer.moneymaster.data.local.entity.TransactionEntity
import com.pixelpioneer.moneymaster.data.model.Transaction
import com.pixelpioneer.moneymaster.data.model.TransactionCategory
import com.pixelpioneer.moneymaster.data.local.relation.TransactionWithCategory

object TransactionMapper {
    fun toEntity(transaction: Transaction): TransactionEntity {
        return TransactionEntity(
            id = transaction.id,
            amount = transaction.amount,
            title = transaction.title,
            description = transaction.description,
            categoryId = transaction.category.id,
            date = transaction.date,
            isExpense = transaction.isExpense
        )
    }

    fun fromEntity(entity: TransactionWithCategory): Transaction {
        return Transaction(
            id = entity.transaction.id,
            amount = entity.transaction.amount,
            title = entity.transaction.title,
            description = entity.transaction.description,
            category = TransactionCategory(
                id = entity.category.id,
                name = entity.category.name,
                color = entity.category.color,
                icon = entity.category.iconResId
            ),
            date = entity.transaction.date,
            isExpense = entity.transaction.isExpense
        )
    }
}
