package com.example.letmecook.util

import android.content.Context

object SearchHistoryManager {

    private const val PREFS_NAME = "search_history_prefs"
    private const val KEY_SEARCH_HISTORY = "search_history"

    fun saveSearch(context: Context, query: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val history = getSearchHistory(context).toMutableSet()
        history.add(query)
        prefs.edit().putStringSet(KEY_SEARCH_HISTORY, history).apply()
    }

    fun getSearchHistory(context: Context): List<String> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getStringSet(KEY_SEARCH_HISTORY, emptySet())?.toList()?.reversed() ?: emptyList()
    }

    // FUNGSI BARU: Untuk menghapus satu item dari riwayat
    fun removeSearch(context: Context, query: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val history = getSearchHistory(context).toMutableSet()
        history.remove(query) // Hapus item yang dipilih
        prefs.edit().putStringSet(KEY_SEARCH_HISTORY, history).apply() // Simpan kembali
    }
}