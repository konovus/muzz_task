package com.example.muzz_task.ui.chatScreen

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.example.muzz_task.*
import com.example.muzz_task.data.enities.Message
import com.example.muzz_task.databinding.ChatFragmentBinding
import com.example.muzz_task.util.Constants.ONE_HOUR
import com.example.muzz_task.util.Constants.TWENTY_SECONDS
import com.example.muzz_task.util.ReplyGenerator
import dagger.hilt.android.AndroidEntryPoint
import java.text.FieldPosition
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
    private var replyOffset = 0
    private var userHasScrolled = false


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = ChatFragmentBinding.bind(view)

        observeChat()
        bindListeners()
        setupInitialLayoutPreferences()
    }


    private fun observeChat() = binding.apply {
        viewModel.messagesPaged.observe(viewLifecycleOwner) { messages ->
            if (messages == null) return@observe

            chatRecyclerview.withModels {
                messages.forEachIndexed { index, message ->
                    val prevMessage = if (index > 0) messages[index - 1] else message

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
                            tail(bubbleTailCheck(index, message, prevMessage, messages))
                        }

                    if (message.user == "Replayer")
                        replayerMessageItem {
                            id(message.timestamp)
                            text(message.text)
                            tail(bubbleTailCheck(index, message, prevMessage, messages))
                        }
                }

                scrollToBottom(messages.size)
            }
        }

        viewModel.chat.observe(viewLifecycleOwner) {
            //todo
        }
    }

    private fun scrollToBottom(position: Int) = binding.apply {
        if (!userHasScrolled || !chatRecyclerview.layoutManager!!.canScrollVertically()) {
            if (chatRecyclerview.adapter?.itemCount == 0)
                chatRecyclerview.scrollToPosition(position)
            else chatRecyclerview.scrollToPosition(chatRecyclerview.adapter!!.itemCount + replyOffset)
            replyOffset = 0
        }
    }

    private fun bindListeners() = binding.apply {
        chatRecyclerview.addOnScrollListener(object: OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                userHasScrolled = true
                if (chatRecyclerview.computeVerticalScrollOffset() < 1000)
                    viewModel.requestNextPage()
            }
        }) 
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
        addOnKeyboardIsShownListener()
    }

    private fun setupInitialLayoutPreferences() {
        binding.sendButtonIb.isEnabled = false
    }

    private fun bubbleTailCheck(index: Int, message: Message, prevMessage: Message, messages: List<Message>): Boolean {
        return if (index == messages.size - 1)
            true
        else if (message.user != prevMessage.user)
            true
        else message.timestamp.minus(prevMessage.timestamp) > TWENTY_SECONDS
    }

    private fun addOnKeyboardIsShownListener() = binding.apply {
        chatFragmentWrap.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            chatFragmentWrap.getWindowVisibleDisplayFrame(r);
            val screenHeight = chatFragmentWrap.rootView.height;
            val keypadHeight = screenHeight - r.bottom;

            if (keypadHeight > screenHeight * 0.15 && chatRecyclerview.canScrollVertically(1)) { // 0.15 ratio is enough to determine keypad height.
                chatRecyclerview.layoutManager!!.scrollToPosition(chatRecyclerview.adapter!!.itemCount - 1)
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}