package com.example.letmecook.util

import android.content.Context

object SearchHistoryManager {

    private const val PREFS_NAME = "search_history_prefs"
    private const val KEY_SEARCH_HISTORY = "search_history"

    fun saveSearch(context: Context, query: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        // Ambil riwayat yang sudah ada
        val history = getSearchHistory(context).toMutableSet()
        // Tambahkan query baru (Set akan otomatis menangani duplikat)
        history.add(query)
        // Simpan kembali
        prefs.edit().putStringSet(KEY_SEARCH_HISTORY, history).apply()
    }

    fun getSearchHistory(context: Context): List<String> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        // Ambil riwayat, urutkan agar yang terbaru muncul lebih dulu jika diinginkan
        return prefs.getStringSet(KEY_SEARCH_HISTORY, emptySet())?.toList()?.reversed() ?: emptyList()
    }
}