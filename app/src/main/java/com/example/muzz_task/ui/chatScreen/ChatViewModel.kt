package com.example.muzz_task.ui.chatScreen

import android.util.Log
import androidx.lifecycle.*
import com.example.muzz_task.TAG
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
    val chat: LiveData<Chat> = dao.getChat().filterNotNull().map { chat ->
        chatFlow.value = chat
        val chatSize = chat.messages.size
        if (chatSize > 30)
            _messagesPaged.value = chat.messages.subList(chatSize - 30, chatSize)
        else _messagesPaged.value = chat.messages
        chat
    }.asLiveData()

    private val _messagesPaged = MutableLiveData<List<Message>>(emptyList())
    val messagesPaged: LiveData<List<Message>> = _messagesPaged

    fun updateChat(newMessages: List<Message>) = viewModelScope.launch {
        val messages = chatFlow.value.messages.toMutableList()
        messages.addAll(newMessages)
        dao.upsertChat(
            chatFlow.value.copy(messages = messages)
        )
    }

    fun requestNextPage() {
        val chatSize = chatFlow.value.messages.size
        if (chatSize - _messagesPaged.value!!.size > 30)
            _messagesPaged.value = chatFlow.value.messages.subList(chatSize - _messagesPaged.value!!.size - 30, chatSize)
    }


}