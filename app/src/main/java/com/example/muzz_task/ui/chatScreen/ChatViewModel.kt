package com.example.muzz_task.ui.chatScreen

import androidx.lifecycle.ViewModel
import com.example.muzz_task.data.dao.ChatDao
import com.example.muzz_task.data.enities.Chat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    dao: ChatDao
): ViewModel() {
    
    val chatFlow: Flow<Chat> = dao.getChat()

}