package com.example.myapplication.data


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "journal_entries")
data class JournalEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val text: String,
    val imageUrl: String = "",
    val date: String = "",
    val mood: String = "",
    val name: String = "",
    val location: String = ""
)
