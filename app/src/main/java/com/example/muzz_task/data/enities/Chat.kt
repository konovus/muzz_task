package com.example.muzz_task.data.enities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_table")
data class Chat(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val messages: List<Message> = emptyList()
)
