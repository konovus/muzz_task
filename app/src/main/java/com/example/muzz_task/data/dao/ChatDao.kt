package com.example.muzz_task.data.dao

import androidx.room.*
import androidx.room.OnConflictStrategy.Companion.REPLACE
import com.example.muzz_task.data.enities.Chat
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {

    @Upsert
    suspend fun upsertChat(chat: Chat)

    @Query("select * from chat_table")
    fun getChat(): Flow<Chat>
}