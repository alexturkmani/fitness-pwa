package com.nexal.app.ui.paywall

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/** Remembers that a new free user should see Premium once, right after finishing setup. */
@Singleton
class PaywallPrompt @Inject constructor(@ApplicationContext context: Context) {
    private val prefs = context.getSharedPreferences("paywall_prompt", Context.MODE_PRIVATE)

    fun queue() {
        prefs.edit().putBoolean(KEY_PENDING, true).apply()
    }

    /** True once per queue, so the paywall never repeats. */
    fun consume(): Boolean {
        val pending = prefs.getBoolean(KEY_PENDING, false)
        if (pending) prefs.edit().remove(KEY_PENDING).apply()
        return pending
    }

    private companion object {
        const val KEY_PENDING = "after_setup"
    }
}
