package com.example.muzz_task.ui.chatScreen

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.muzz_task.R
import com.example.muzz_task.TAG
import com.example.muzz_task.data.enities.Chat
import com.example.muzz_task.data.enities.Message
import com.example.muzz_task.databinding.ChatFragmentBinding
import com.example.muzz_task.replayerMessageItem
import com.example.muzz_task.senderMessageItem
import com.example.muzz_task.util.ReplyGenerator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChatFragment: Fragment(R.layout.chat_fragment) {

    private var _binding: ChatFragmentBinding? = null
    private val binding: ChatFragmentBinding get() = _binding!!
    private val viewModel by viewModels<ChatViewModel>()
    @Inject
    lateinit var replyGenerator: ReplyGenerator

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = ChatFragmentBinding.bind(view)


        observeChat()
        bindClickListeners()
    }

    private fun observeChat() = binding.apply {
        viewModel.chat.observe(viewLifecycleOwner) { chat ->
            if (chat == null) return@observe

            chatRecyclerview.withModels {
                chat.messages.forEachIndexed { index, message ->
                    val prevMessage = if (index > 0) chat.messages[index - 1] else message
                    if (message.user == "Sender")
                        senderMessageItem {
                            id(message.timestamp)
                            text(message.text)
                            tail(tailCheck(index, message, prevMessage, chat))
                        }

                    if (message.user == "Replayer")
                        replayerMessageItem {
                            id(message.timestamp)
                            text(message.text)
                            tail(tailCheck(index, message, prevMessage, chat))
                        }

                }
            }
        }
    }

    private fun tailCheck(index: Int, message: Message, prevMessage: Message, chat: Chat,): Boolean {
        return if (index == chat.messages.size - 1)
            true
        else if (message.user != prevMessage.user)
            true
        else message.timestamp.minus(prevMessage.timestamp) > 20_000
    }


    private fun bindClickListeners() = binding.apply {
        sendButtonIb.setOnClickListener {
            val messages = mutableListOf<Message>()
            val message = Message(
                text = binding.sendTextEt.text.toString(),
                timestamp = System.currentTimeMillis(),
                user = "Sender"
            )
            messages.add(message)
            val replyMessage = replyGenerator.getReply()
            if (replyMessage != null)
                messages.add(replyMessage)

            viewModel.updateChat(messages)
        }

        backArrow.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}