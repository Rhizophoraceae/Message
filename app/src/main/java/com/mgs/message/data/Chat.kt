package com.mgs.message.data

data class Chat (
    val id: Int = 0,
    val target: Target = User(),
    val message: Message = Message()
)