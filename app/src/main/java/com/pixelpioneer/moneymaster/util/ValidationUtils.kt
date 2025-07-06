package com.pixelpioneer.moneymaster.util

/**
 * Utility class for input validation
 */
object ValidationUtils {

    /**
     * Validate an amount value
     *
     * @param amount The amount to validate
     * @param allowZero Whether to allow zero values (default is false)
     * @param allowNegative Whether to allow negative values (default is false)
     * @return A validation result with an error message if validation fails
     */
    fun validateAmount(
        amount: Double,
        allowZero: Boolean = false,
        allowNegative: Boolean = false
    ): ValidationResult {
        return when {
            amount == 0.0 && !allowZero -> ValidationResult(
                isValid = false,
                errorMessage = "Amount cannot be zero"
            )

            amount < 0 && !allowNegative -> ValidationResult(
                isValid = false,
                errorMessage = "Amount cannot be negative"
            )

            else -> ValidationResult(isValid = true)
        }
    }

    /**
     * Validate a text field to ensure it's not empty
     *
     * @param text The text to validate
     * @param fieldName The name of the field for the error message
     * @return A validation result with an error message if validation fails
     */
    fun validateRequired(text: String, fieldName: String): ValidationResult {
        return if (text.isBlank()) {
            ValidationResult(
                isValid = false,
                errorMessage = "$fieldName cannot be empty"
            )
        } else {
            ValidationResult(isValid = true)
        }
    }

    /**
     * Validate a text field with a minimum length
     *
     * @param text The text to validate
     * @param minLength The minimum length required
     * @param fieldName The name of the field for the error message
     * @return A validation result with an error message if validation fails
     */
    fun validateMinLength(text: String, minLength: Int, fieldName: String): ValidationResult {
        return if (text.length < minLength) {
            ValidationResult(
                isValid = false,
                errorMessage = "$fieldName must be at least $minLength characters"
            )
        } else {
            ValidationResult(isValid = true)
        }
    }

    /**
     * Validate a text field with a maximum length
     *
     * @param text The text to validate
     * @param maxLength The maximum length allowed
     * @param fieldName The name of the field for the error message
     * @return A validation result with an error message if validation fails
     */
    fun validateMaxLength(text: String, maxLength: Int, fieldName: String): ValidationResult {
        return if (text.length > maxLength) {
            ValidationResult(
                isValid = false,
                errorMessage = "$fieldName cannot exceed $maxLength characters"
            )
        } else {
            ValidationResult(isValid = true)
        }
    }

    /**
     * Validate that a selected category is not null
     *
     * @param categoryId The ID of the selected category
     * @return A validation result with an error message if validation fails
     */
    fun validateCategorySelected(categoryId: Long?): ValidationResult {
        return if (categoryId == null || categoryId == 0L) {
            ValidationResult(
                isValid = false,
                errorMessage = "Please select a category"
            )
        } else {
            ValidationResult(isValid = true)
        }
    }
}

/**
 * Data class to hold validation results
 */
data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null
)