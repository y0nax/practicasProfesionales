package com.jonathan.expensetrackerapp.domain.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DateFormats {
    private val df: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    fun format(date: LocalDate): String = date.format(df)
}