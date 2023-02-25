package com.example.muzz_task.di

import android.app.Application
import androidx.room.Room
import com.example.muzz_task.data.dao.ChatDao
import com.example.muzz_task.data.db.ChatDatabase
import com.example.muzz_task.util.ReplyGenerator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideReplyGenerator(): ReplyGenerator = ReplyGenerator()

    @Provides
    @Singleton
    fun provideChatDatabase(app: Application): ChatDatabase {
        return Room.databaseBuilder(
            app,
            ChatDatabase::class.java,
            "chat_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun providesChatDao(db: ChatDatabase): ChatDao = db.dao
}