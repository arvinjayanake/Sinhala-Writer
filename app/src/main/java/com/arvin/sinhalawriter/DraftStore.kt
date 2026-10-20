package com.arvin.sinhalawriter

import android.content.Context

/** Reads Flutter's existing shared_preferences draft when upgrading the Android app. */
class DraftStore(context: Context) {
    private val preferences = context.getSharedPreferences("writer_draft", Context.MODE_PRIVATE)
    private val flutterPreferences = context.getSharedPreferences("FlutterSharedPreferences", Context.MODE_PRIVATE)

    fun load(): String = preferences.getString("data", null)
        ?: flutterPreferences.getString("flutter.data", "").orEmpty()

    fun save(text: String) {
        preferences.edit().putString("data", text).apply()
    }
}
