package com.example.muzz_task.ui.chatScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.muzz_task.data.dao.ChatDao
import com.example.muzz_task.data.enities.Chat
import com.example.muzz_task.data.enities.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val dao: ChatDao
): ViewModel() {

    val chat: LiveData<Chat> = dao.getChat().asLiveData()

    fun updateChat(message: Message) = viewModelScope.launch {
        val updatedChat = dao.getChat().first() ?: Chat()
        val messages = updatedChat.messages.toMutableList()
        messages.add(message)
        dao.upsertChat(
            updatedChat.copy(messages = messages)
        )
    }


}