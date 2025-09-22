package com.jonathan.expensetrackerapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jonathan.expensetrackerapp.data.repository.TransactionRepository
import com.jonathan.expensetrackerapp.domain.model.Transaction
import com.jonathan.expensetrackerapp.domain.model.TransactionType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repo: TransactionRepository
) : ViewModel() {

    val transactions: StateFlow<List<Transaction>> =
        repo.getAll().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val balance: StateFlow<Double> =
        transactions.map { txs ->
            txs.fold(0.0) { acc, t ->
                acc + if (t.type == TransactionType.INCOME) t.amount else -t.amount
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    fun clearAll(onDone: (() -> Unit)? = null) {
        viewModelScope.launch {
            repo.clearAll()
            onDone?.invoke()
        }
    }

    fun deleteTransaction(id: Long, onDone: (() -> Unit)? = null) {
        viewModelScope.launch {
            repo.deleteById(id)
            onDone?.invoke()
        }
    }

    fun reAddTransactionCopy(t: com.jonathan.expensetrackerapp.domain.model.Transaction, onDone: (() -> Unit)? = null) {
        viewModelScope.launch {
            repo.addTransaction(
                amount = t.amount,
                isIncome = (t.type == com.jonathan.expensetrackerapp.domain.model.TransactionType.INCOME),
                categoryName = t.categoryName,
                date = t.date,
                note = t.note
            )
            onDone?.invoke()
        }
    }
}
