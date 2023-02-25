package com.example.muzz_task.ui.chatScreen

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.muzz_task.R
import com.example.muzz_task.data.enities.Message
import com.example.muzz_task.databinding.ChatFragmentBinding
import com.example.muzz_task.replayerMessageItem
import com.example.muzz_task.senderMessageItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatFragment: Fragment(R.layout.chat_fragment) {

    private var _binding: ChatFragmentBinding? = null
    private val binding: ChatFragmentBinding get() = _binding!!
    private val viewModel by viewModels<ChatViewModel>()

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
                chat.messages.forEach { message ->
                    if (message.user == "Sender")
                        senderMessageItem {
                            id(message.timestamp)
                            text(message.text)
                        }
                    if (message.user == "Replayer")
                        replayerMessageItem {
                            id(message.timestamp)
                            text(message.text)
                        }

                }
            }
        }
    }


    private fun bindClickListeners() = binding.apply {
        sendButtonIb.setOnClickListener {
            val message = Message(
                text = binding.sendTextEt.text.toString(),
                timestamp = System.currentTimeMillis(),
                user = "Sender"
            )
            viewModel.updateChat(message)
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