package com.jonathan.expensetrackerapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jonathan.expensetrackerapp.data.repository.TransactionRepository
import kotlinx.coroutines.launch
import java.time.LocalDate

class AddEditViewModel(
    private val repo: TransactionRepository
) : ViewModel() {

    fun save(
        amount: Double,
        isIncome: Boolean,
        category: String,
        note: String?,
        onDone: () -> Unit
    ) {
        viewModelScope.launch {
            repo.addTransaction(
                amount = amount,
                isIncome = isIncome,
                categoryName = category,
                date = LocalDate.now(),
                note = note
            )
            onDone()
        }
    }
}