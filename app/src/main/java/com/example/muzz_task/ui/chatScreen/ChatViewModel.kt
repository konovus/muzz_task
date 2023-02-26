package com.example.muzz_task.ui.chatScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.muzz_task.data.dao.ChatDao
import com.example.muzz_task.data.enities.Chat
import com.example.muzz_task.data.enities.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val dao: ChatDao
): ViewModel() {

    private val chatFlow = MutableStateFlow(Chat())
    val chat: LiveData<Chat> = dao.getChat().map {
        chatFlow.value = it
        it
    }.asLiveData()

    fun updateChat(newMessages: List<Message>) = viewModelScope.launch {
        val messages = chatFlow.value.messages.toMutableList()
        messages.addAll(newMessages)
        dao.upsertChat(
            chatFlow.value.copy(messages = messages)
        )
    }


}