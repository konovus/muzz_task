package com.example.muzz_task.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.muzz_task.data.dao.ChatDao
import com.example.muzz_task.data.enities.Chat

@Database(entities = [Chat::class], version = 1)
abstract class ChatDatabase: RoomDatabase() {

    abstract val dao: ChatDao
}