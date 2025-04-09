package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.JournalEntryEntity
import com.example.myapplication.data.JournalRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.Flow

class TravelViewModel(private val repository: JournalRepository) : ViewModel() {

    val allJournalEntries: StateFlow<List<JournalEntryEntity>> =
        repository.getAllEntries().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    fun addEntry(entry: JournalEntryEntity) {
        viewModelScope.launch {
            repository.insertEntry(entry)
        }
    }

    fun deleteEntry(entry: JournalEntryEntity) {
        viewModelScope.launch {
            repository.deleteEntry(entry)
        }
    }

    fun getJournalById(id: Int): Flow<JournalEntryEntity?> {
        return repository.getJournalById(id)
    }

    fun updateEntry(oldEntry: JournalEntryEntity, newEntry: JournalEntryEntity) {
        viewModelScope.launch {
            repository.update(oldEntry, newEntry)
        }
    }

}


