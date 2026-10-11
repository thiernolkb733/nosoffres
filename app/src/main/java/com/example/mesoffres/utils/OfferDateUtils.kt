
package com.example.mesoffres.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.util.Locale

object OfferDateUtils {

    private val frenchShortDateFormatter =
        DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern("d MMM yyyy")
            .toFormatter(Locale.FRENCH)

    private val frenchLongDateFormatter =
        DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern("d MMMM yyyy")
            .toFormatter(Locale.FRENCH)

    private val supportedFormats = listOf(
        DateTimeFormatter.ofPattern("d/M/yyyy", Locale.FRENCH),
        DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.FRENCH),
        frenchShortDateFormatter,
        frenchLongDateFormatter,
        DateTimeFormatter.ISO_LOCAL_DATE
    )

    fun parseDate(value: String): LocalDate? {
        val cleanedValue = value.trim()
            .replace(".", "")
            .lowercase(Locale.FRENCH)

        if (cleanedValue.isBlank()) return null

        for (formatter in supportedFormats) {
            try {
                return LocalDate.parse(cleanedValue, formatter)
            } catch (_: Exception) {
                // On essaie le format suivant.
            }
        }

        return null
    }
}
