package com.example.muzz_task.ui.chatScreen

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.muzz_task.*
import com.example.muzz_task.data.enities.Chat
import com.example.muzz_task.data.enities.Message
import com.example.muzz_task.databinding.ChatFragmentBinding
import com.example.muzz_task.util.Constants.ONE_HOUR
import com.example.muzz_task.util.Constants.TWENTY_SECONDS
import com.example.muzz_task.util.ReplyGenerator
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ChatFragment: Fragment(R.layout.chat_fragment) {

    private var _binding: ChatFragmentBinding? = null
    private val binding: ChatFragmentBinding get() = _binding!!
    private val viewModel by viewModels<ChatViewModel>()
    @Inject
    lateinit var replyGenerator: ReplyGenerator
    var replyOffset = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = ChatFragmentBinding.bind(view)

        observeChat()
        bindListeners()
        setupInitialLayoutPreferences()
    }


    private fun observeChat() = binding.apply {
        viewModel.chat.observe(viewLifecycleOwner) { chat ->
            if (chat == null) return@observe

            chatRecyclerview.withModels {
                chat.messages.forEachIndexed { index, message ->
                    val prevMessage = if (index > 0) chat.messages[index - 1] else message

                    if (index == 0 || (message.timestamp.minus(prevMessage.timestamp) > ONE_HOUR))
                        timestampItem{
                            id(message.timestamp + 1)
                            val date = Date(message.timestamp)
                            val format = SimpleDateFormat("EEEE HH:mm", Locale.UK)
                            messageTimestamp(format.format(date))
                        }
                    if (message.user == "Sender")
                        senderMessageItem {
                            id(message.timestamp)
                            text(message.text)
                            tail(bubbleTailCheck(index, message, prevMessage, chat))
                        }

                    if (message.user == "Replayer")
                        replayerMessageItem {
                            id(message.timestamp)
                            text(message.text)
                            tail(bubbleTailCheck(index, message, prevMessage, chat))
                        }
                }

                if (chatRecyclerview.adapter?.itemCount == 0)
                    chatRecyclerview.scrollToPosition(chat.messages.size - 1)
                else chatRecyclerview.layoutManager?.scrollToPosition(chatRecyclerview.adapter!!.itemCount + replyOffset )

                replyOffset = 0

            }
        }
    }

    private fun bindListeners() = binding.apply {
        sendButtonIb.setOnClickListener {
            val messages = mutableListOf<Message>()
            val message = Message(
                text = binding.sendTextEt.text.toString(),
                timestamp = System.currentTimeMillis(),
                user = "Sender"
            )
            messages.add(message)
            val replyMessage = replyGenerator.getReply()
            if (replyMessage != null) {
                messages.add(replyMessage)
                replyOffset = 1
            }

            sendTextEt.text.clear()
            viewModel.updateChat(messages)
        }

        backArrow.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }


        sendTextEt.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                sendTextEt.setBackgroundResource(R.drawable.send_text_background_shape_active)
            else
                sendTextEt.setBackgroundResource(R.drawable.send_text_background_shape_inactive)

        }

        sendTextEt.doOnTextChanged { text, _, _, _ ->
            if (text.toString().isNotEmpty()) {
                sendButtonIb.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.pink))
                sendButtonIb.isEnabled = true
            } else {
                sendButtonIb.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.gray_metal))
                sendButtonIb.isEnabled = false
            }

        }

        bindKeyboardOpeningListener()
    }

    private fun setupInitialLayoutPreferences() {
        binding.sendButtonIb.isEnabled = false
    }

    private fun bubbleTailCheck(index: Int, message: Message, prevMessage: Message, chat: Chat): Boolean {
        return if (index == chat.messages.size - 1)
            true
        else if (message.user != prevMessage.user)
            true
        else message.timestamp.minus(prevMessage.timestamp) > TWENTY_SECONDS
    }

    private fun bindKeyboardOpeningListener() = binding.apply {
        chatFragmentWrap.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            chatFragmentWrap.getWindowVisibleDisplayFrame(r);
            val screenHeight = chatFragmentWrap.rootView.height;
            val keypadHeight = screenHeight - r.bottom;

            if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is enough to determine keypad height.
                chatRecyclerview.smoothScrollToPosition(chatRecyclerview.adapter?.itemCount ?: 0)
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}