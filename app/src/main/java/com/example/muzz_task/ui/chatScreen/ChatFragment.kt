package com.example.muzz_task.ui.chatScreen

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.muzz_task.R
import com.example.muzz_task.databinding.ChatFragmentBinding
import com.example.muzz_task.util.ReplyGenerator

class ChatFragment: Fragment(R.layout.chat_fragment) {

    private var _binding: ChatFragmentBinding? = null
    private val binding: ChatFragmentBinding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = ChatFragmentBinding.bind(view)

        bindClickListeners()
    }

    private fun bindClickListeners() {
        binding.sendButtonIb.setOnClickListener {

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}