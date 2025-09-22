package com.jonathan.expensetrackerapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jonathan.expensetrackerapp.domain.model.Transaction
import com.jonathan.expensetrackerapp.domain.model.TransactionType
import com.jonathan.expensetrackerapp.domain.util.DateFormats

@Composable
fun TransactionItem(transaction: Transaction) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    if (transaction.type == TransactionType.INCOME) "Ingreso" else "Gasto",
                    style = MaterialTheme.typography.labelMedium
                )
                Text(transaction.categoryName, style = MaterialTheme.typography.titleMedium)
                transaction.note?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = (if (transaction.type == TransactionType.INCOME) "+" else "-") +
                            "$${"%,.2f".format(transaction.amount)}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(DateFormats.format(transaction.date), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}