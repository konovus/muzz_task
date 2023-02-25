package com.example.muzz_task.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.muzz_task.data.dao.ChatDao
import com.example.muzz_task.data.enities.Chat
import com.example.muzz_task.data.enities.Converters

@Database(entities = [Chat::class], version = 1)
@TypeConverters(Converters::class)
abstract class ChatDatabase: RoomDatabase() {

    abstract val dao: ChatDao
}