package com.nexal.app.ui.paywall

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

/** Paywall headline after setup, built from the answers the user just gave. */
internal fun setupHeadline(days: Int, goal: String?): String {
    val focus = goal?.trim()?.lowercase(Locale.ROOT)?.takeIf { it.isNotEmpty() } ?: "training"
    return if (days > 0) "Build your $days-day $focus plan" else "Build your $focus plan"
}

/** Whole percent saved by paying yearly, rounded down so the claim is never overstated. */
internal fun yearlySavingsPercent(monthlyMicros: Long, yearlyMicros: Long): Int? {
    if (monthlyMicros <= 0 || yearlyMicros <= 0) return null
    val fullYear = monthlyMicros * 12
    if (yearlyMicros >= fullYear) return null
    return ((fullYear - yearlyMicros) * 100 / fullYear).toInt().takeIf { it > 0 }
}

/** A yearly price shown per month, in the currency Play charges. */
internal fun yearlyPerMonthText(
    yearlyMicros: Long,
    currencyCode: String,
    locale: Locale = Locale.getDefault()
): String? {
    if (yearlyMicros <= 0) return null
    val format = NumberFormat.getCurrencyInstance(locale)
    return runCatching {
        format.currency = Currency.getInstance(currencyCode)
        format.format(yearlyMicros / 12 / 1_000_000.0)
    }.getOrNull()
}
