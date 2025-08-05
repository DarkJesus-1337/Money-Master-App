package com.pixelpioneer.moneymaster.data.mapper

import com.pixelpioneer.moneymaster.data.local.entity.CategoryEntity
import com.pixelpioneer.moneymaster.data.model.TransactionCategory

/**
 * Mapper class for converting between TransactionCategory domain models and database entities.
 *
 * This object provides utility methods to transform TransactionCategory objects to
 * CategoryEntity objects and vice versa.
 */
object CategoryMapper {
    /**
     * Converts a TransactionCategory domain model to a CategoryEntity database entity.
     *
     * @param category The TransactionCategory domain model to convert
     * @return A CategoryEntity database entity
     */
    fun toEntity(category: TransactionCategory): CategoryEntity {
        return CategoryEntity(
            id = category.id,
            name = category.name,
            color = category.color,
            iconResId = category.icon
        )
    }

    /**
     * Converts a CategoryEntity database entity to a TransactionCategory domain model.
     *
     * @param entity The CategoryEntity database entity to convert
     * @return A TransactionCategory domain model
     */
    fun fromEntity(entity: CategoryEntity): TransactionCategory {
        return TransactionCategory(
            id = entity.id,
            name = entity.name,
            color = entity.color,
            icon = entity.iconResId
        )
    }
}