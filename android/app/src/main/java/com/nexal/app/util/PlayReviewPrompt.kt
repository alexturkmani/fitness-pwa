package com.nexal.app.util

import android.app.Activity
import android.content.Context
import com.google.android.play.core.review.ReviewManagerFactory

/**
 * Asks for a Play review after a clear first win. Play may still suppress the
 * dialog; we only attempt once per install.
 */
object PlayReviewPrompt {
    private const val PREFS = "nexal_review_prefs"
    private const val KEY_ASKED = "review_prompted"

    fun maybeRequest(activity: Activity) {
        val prefs = activity.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (prefs.getBoolean(KEY_ASKED, false)) return
        prefs.edit().putBoolean(KEY_ASKED, true).apply()

        val manager = ReviewManagerFactory.create(activity)
        manager.requestReviewFlow().addOnCompleteListener { task ->
            if (!task.isSuccessful) return@addOnCompleteListener
            manager.launchReviewFlow(activity, task.result)
        }
    }
}
