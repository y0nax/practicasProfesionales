package com.jonathan.expensetrackerapp.domain.util

object Validation {

    fun isValidAmount(amount: String): Boolean {
        val value = amount.toDoubleOrNull()
        return value != null && value > 0.0
    }

    fun isValidCategory(category: String): Boolean {
        return category.isNotBlank()
    }
}