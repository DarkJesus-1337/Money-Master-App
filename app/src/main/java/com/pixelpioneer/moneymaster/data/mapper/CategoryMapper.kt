package com.pixelpioneer.moneymaster.data.mapper

import com.pixelpioneer.moneymaster.data.local.entity.CategoryEntity
import com.pixelpioneer.moneymaster.data.model.TransactionCategory

object CategoryMapper {
    fun toEntity(category: TransactionCategory): CategoryEntity {
        return CategoryEntity(
            id = category.id,
            name = category.name,
            color = category.color,
            iconResId = category.icon
        )
    }

    fun fromEntity(entity: CategoryEntity): TransactionCategory {
        return TransactionCategory(
            id = entity.id,
            name = entity.name,
            color = entity.color,
            icon = entity.iconResId
        )
    }
}