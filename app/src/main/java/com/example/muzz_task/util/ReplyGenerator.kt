package com.example.muzz_task.util

class ReplyGenerator {

    fun getReply(): String? {
        val replyChance = (0..3).random()
        return if (replyChance == 0)
            replies.random()
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