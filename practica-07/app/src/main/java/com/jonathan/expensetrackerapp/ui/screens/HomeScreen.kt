package com.jonathan.expensetrackerapp.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jonathan.expensetrackerapp.data.local.AppDatabase
import com.jonathan.expensetrackerapp.data.repository.TransactionRepository
import com.jonathan.expensetrackerapp.domain.model.Transaction
import com.jonathan.expensetrackerapp.export.CsvExporter
import com.jonathan.expensetrackerapp.ui.components.EmptyState
import com.jonathan.expensetrackerapp.ui.components.TransactionItem
import com.jonathan.expensetrackerapp.viewmodel.HomeViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAddClick: () -> Unit,
    onOpenFilter: () -> Unit,
    startFilterFlow: StateFlow<String?>,
    endFilterFlow: StateFlow<String?>,
    onClearFilter: () -> Unit
) {
    val ctx = LocalContext.current
    val vm: HomeViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val db = AppDatabase.getInstance(ctx)
            val repo = TransactionRepository(db.transactionDao())
            return HomeViewModel(repo) as T
        }
    })

    val txs by vm.transactions.collectAsState()
    val balance by vm.balance.collectAsState()

    val startFilter by startFilterFlow.collectAsState()
    val endFilter by endFilterFlow.collectAsState()

    val shownList = remember(txs, startFilter, endFilter) {
        val s = startFilter?.trim().orEmpty()
        val e = endFilter?.trim().orEmpty()
        if (s.isBlank() || e.isBlank()) {
            txs
        } else {
            val start = s.toLocalDateOrNull()
            val end = e.toLocalDateOrNull()
            if (start != null && end != null && !end.isBefore(start)) {
                txs.filter { it.date >= start && it.date <= end }
            } else txs
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val result = CsvExporter.exportListToCsv(ctx, uri, shownList)
                when (result) {
                    is CsvExporter.ExportResult.Success ->
                        snackbarHostState.showSnackbar("Exportado ${result.rows} filas.")
                    is CsvExporter.ExportResult.Error ->
                        snackbarHostState.showSnackbar("Error al exportar: ${result.message}")
                }
            }
        }
    }

    val hasActiveFilter = !startFilter.isNullOrBlank() && !endFilter.isNullOrBlank()
    var showResetDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis gastos") },
                actions = {
                    if (hasActiveFilter) {
                        TextButton(onClick = onClearFilter) { Text("Limpiar") }
                    } else {
                        TextButton(onClick = { showResetDialog = true }) { Text("Reset") }
                    }
                    TextButton(onClick = onOpenFilter) { Text("Filtrar") }
                    TextButton(onClick = {
                        val filename = "gastos_${LocalDate.now()}.csv"
                        exportLauncher.launch(filename)
                    }) { Text("Exportar") }
                }
            )
        },
        floatingActionButton = { FloatingActionButton(onClick = onAddClick) { Text("+") } },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text(
                text = "Balance: $${"%,.2f".format(balance)}",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
            if (hasActiveFilter) {
                Text(
                    text = "Filtro: ${startFilter!!.trim()} a ${endFilter!!.trim()}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(Modifier.height(12.dp))
            Divider()
            Spacer(Modifier.height(12.dp))

            if (shownList.isEmpty()) {
                EmptyState()
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(shownList, key = { it.id }) { t ->
                        SwipeToDeleteItem(
                            item = t,
                            onDeleteConfirmed = { tx ->
                                vm.deleteTransaction(tx.id) {
                                    scope.launch {
                                        val res = snackbarHostState.showSnackbar(
                                            message = "Transacción eliminada.",
                                            actionLabel = "Deshacer",
                                            withDismissAction = true
                                        )
                                        if (res == SnackbarResult.ActionPerformed) {
                                            vm.reAddTransactionCopy(tx) {
                                                scope.launch { snackbarHostState.showSnackbar("Restaurada.") }
                                            }
                                        }
                                    }
                                }
                            }
                        ) {
                            TransactionItem(transaction = t)
                        }
                    }
                }
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reiniciar datos") },
            text = { Text("Se eliminarán todas las transacciones. ¿Deseas continuar?") },
            confirmButton = {
                TextButton(onClick = {
                    showResetDialog = false
                    vm.clearAll {
                        onClearFilter()
                        scope.launch { snackbarHostState.showSnackbar("Datos reiniciados.") }
                    }
                }) { Text("Sí, borrar") }
            },
            dismissButton = { TextButton(onClick = { showResetDialog = false }) { Text("Cancelar") } }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> SwipeToDeleteItem(
    item: T,
    onDeleteConfirmed: (T) -> Unit,
    content: @Composable () -> Unit
) {
    val state = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDeleteConfirmed(item)
                false
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = state,
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            Surface(
                color = MaterialTheme.colorScheme.error,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 2.dp, vertical = 2.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        text = "Eliminar",
                        color = MaterialTheme.colorScheme.onError,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(end = 24.dp)
                    )
                }
            }
        },
        content = { content() }
    )
}

private fun String.toLocalDateOrNull(): LocalDate? = try {
    LocalDate.parse(this)
} catch (_: Throwable) {
    null
}
