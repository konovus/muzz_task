package com.example.muzz_task.data.enities

import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class Converters {

    private val moshi: Moshi =  Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

        @TypeConverter
    fun fromMessagesList(data: List<Message>): String{
        val type = Types.newParameterizedType(List::class.java, Message::class.java)
        val adapter = moshi.adapter<List<Message>>(type)
        return adapter.toJson(data)
    }

    @TypeConverter
    fun toMessagesList(json: String): List<Message> {
        val type = Types.newParameterizedType(List::class.java, Message::class.java)
        val adapter = moshi.adapter<List<Message>>(type)
        return adapter.fromJson(json)!!
    }

}