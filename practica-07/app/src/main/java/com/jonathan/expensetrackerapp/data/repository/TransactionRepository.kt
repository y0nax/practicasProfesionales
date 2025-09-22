package com.jonathan.expensetrackerapp.data.repository

import com.jonathan.expensetrackerapp.data.local.dao.TransactionDao
import com.jonathan.expensetrackerapp.data.local.entity.TransactionEntity
import com.jonathan.expensetrackerapp.domain.model.Transaction
import com.jonathan.expensetrackerapp.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class TransactionRepository(
    private val dao: TransactionDao
) {

    fun getAll(): Flow<List<Transaction>> =
        dao.getAll().map { list ->
            list.map { e ->
                Transaction(
                    id = e.id,
                    amount = e.amount,
                    type = if (e.type == "INCOME") TransactionType.INCOME else TransactionType.EXPENSE,
                    categoryName = e.categoryName,
                    date = LocalDate.ofEpochDay(e.dateEpochDay),
                    note = e.note
                )
            }
        }

    suspend fun addTransaction(
        amount: Double,
        isIncome: Boolean,
        categoryName: String,
        date: LocalDate,
        note: String?
    ): Long {
        val entity = TransactionEntity(
            amount = amount,
            type = if (isIncome) "INCOME" else "EXPENSE",
            categoryName = categoryName.trim(),
            dateEpochDay = date.toEpochDay(),
            note = note?.takeIf { it.isNotBlank() }
        )
        return dao.insert(entity)
    }

    suspend fun deleteById(id: Long) = dao.deleteById(id)

    suspend fun clearAll() = dao.deleteAll()
}