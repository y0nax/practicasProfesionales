package com.jonathan.expensetrackerapp.domain.model

import java.time.LocalDate

enum class TransactionType { INCOME, EXPENSE }

data class Transaction(
    val id: Long,
    val amount: Double,
    val type: TransactionType,
    val categoryName: String,
    val date: LocalDate,
    val note: String?
)
