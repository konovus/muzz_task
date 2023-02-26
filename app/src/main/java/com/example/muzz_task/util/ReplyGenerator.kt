package com.example.muzz_task.util

import com.example.muzz_task.data.enities.Message

class ReplyGenerator {

    fun getReply(): Message? {
        val replyChance = (0..1).random()
        return if (replyChance == 0)
            Message(
                text = replies.random(),
                timestamp = System.currentTimeMillis(),
                user = "Replayer"
            )
        else null
    }
    companion object{
        val replies = listOf<String>(
            "Yes, sure as you say...",
            "No way, that's impossible",
            "Tell me more about what happened yesterday",
            "Hi, it's a pleasure to meet you",
            "Can't wait to go out sometime, we could watch a movie ..."
        )


    }
}