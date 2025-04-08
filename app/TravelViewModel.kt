package com.example.myapplication

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

data class JournalEntry(
    val text: String,
    val imageUrl: String = "",
    val date: String = ""
)

class TravelViewModel(application: Application) : AndroidViewModel(application) {
    var entries = mutableStateOf(listOf<JournalEntry>())
        private set

    fun addEntry(entry: JournalEntry) {
        entries.value = entries.value + entry
    }

    fun deleteEntry(entry: JournalEntry) {
        entries.value = entries.value - entry
    }

    fun updateEntry(oldEntry: JournalEntry, newEntry: JournalEntry) {
        entries.value = entries.value.map {
            if (it == oldEntry) newEntry else it
        }
    }
}
