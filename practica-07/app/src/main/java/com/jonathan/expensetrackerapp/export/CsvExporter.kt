package com.jonathan.expensetrackerapp.export

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.jonathan.expensetrackerapp.data.repository.TransactionRepository
import com.jonathan.expensetrackerapp.domain.model.Transaction
import com.jonathan.expensetrackerapp.domain.model.TransactionType
import com.jonathan.expensetrackerapp.domain.util.DateFormats
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.IOException
import java.nio.charset.Charset
import java.time.LocalDate
import java.util.Locale

/**
 * Utilidad para exportar transacciones a CSV.
 * No hace UI: tú lanzas el intent de "Crear documento" y nos pasas el Uri.
 */
object CsvExporter {

    sealed class ExportResult {
        data class Success(val rows: Int) : ExportResult()
        data class Error(val message: String, val cause: Throwable? = null) : ExportResult()
    }

    /**
     * Crea un Intent para pedir al usuario la ubicación y nombre del archivo CSV.
     * Úsalo con Activity Result API.
     */
    fun createCreateDocumentIntent(
        suggestedFilename: String = "gastos_${LocalDate.now()}.csv"
    ): Intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
        addCategory(Intent.CATEGORY_OPENABLE)
        type = "text/csv"
        putExtra(Intent.EXTRA_TITLE, suggestedFilename)
    }

    /**
     * Lee TODAS las transacciones del repositorio y las exporta al Uri indicado.
     */
    suspend fun exportAllToCsv(
        context: Context,
        repo: TransactionRepository,
        outputUri: Uri
    ): ExportResult = withContext(Dispatchers.IO) {
        try {
            val transactions = repo.getAll().first()
            exportListToCsv(context, outputUri, transactions)
        } catch (t: Throwable) {
            ExportResult.Error("No se pudieron leer las transacciones.", t)
        }
    }

    /**
     * Exporta una lista proporcionada (p.ej. ya filtrada) al Uri indicado.
     */
    suspend fun exportListToCsv(
        context: Context,
        outputUri: Uri,
        transactions: List<Transaction>
    ): ExportResult = withContext(Dispatchers.IO) {
        val (csv, rows) = buildCsv(transactions)
        try {
            context.contentResolver.openOutputStream(outputUri, "w")?.use { os ->
                os.write(csv.toByteArray(Charset.forName("UTF-8")))
                os.flush()
            } ?: return@withContext ExportResult.Error("No se pudo abrir el destino para escribir (URI inválido).")
            ExportResult.Success(rows)
        } catch (io: IOException) {
            ExportResult.Error("Error de E/S al escribir el CSV.", io)
        } catch (t: Throwable) {
            ExportResult.Error("Fallo inesperado al exportar CSV.", t)
        }
    }

    /**
     * Construye el CSV en memoria. Retorna el texto y la cantidad de filas de datos (excluye encabezado).
     */
    private fun buildCsv(transactions: List<Transaction>): Pair<String, Int> {
        val sb = StringBuilder()
        // Encabezado
        sb.appendLine("date,type,category,amount,note")

        val rows = transactions.map { t ->
            val date = DateFormats.format(t.date)              // yyyy-MM-dd
            val type = if (t.type == TransactionType.INCOME) "INCOME" else "EXPENSE"
            val category = t.categoryName
            val amount = String.format(Locale.US, "%.2f", t.amount) // separador decimal '.'
            val note = t.note.orEmpty()

            listOf(date, type, category, amount, note)
                .joinToString(",") { escapeCsv(it) }
        }

        rows.forEach { sb.appendLine(it) }
        return sb.toString() to rows.size
    }

    /**
     * Escapa un campo para CSV (dobla comillas y envuelve en comillas si es necesario).
     */
    private fun escapeCsv(raw: String): String {
        val needsQuote = raw.contains(',') || raw.contains('\n') || raw.contains('\r') || raw.contains('"')
        val doubled = raw.replace("\"", "\"\"")
        return if (needsQuote) "\"$doubled\"" else doubled
    }
}
