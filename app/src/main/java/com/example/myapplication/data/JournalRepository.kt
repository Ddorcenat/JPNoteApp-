package com.example.myapplication.data

import kotlinx.coroutines.flow.Flow

class JournalRepository(private val journalDao: JournalDao) {

    fun getAllEntries(): Flow<List<JournalEntryEntity>> {
        return journalDao.getAllEntries()
    }

    suspend fun insertEntry(entry: JournalEntryEntity) {
        journalDao.insert(entry)
    }

    suspend fun deleteEntry(entry: JournalEntryEntity) {
        journalDao.delete(entry)
    }

    fun getJournalById(id: Int): Flow<JournalEntryEntity?> {
        return journalDao.getById(id)
    }

    suspend fun update(oldEntry: JournalEntryEntity, newEntry: JournalEntryEntity) {
        journalDao.updateEntry(newEntry)
    }

}


