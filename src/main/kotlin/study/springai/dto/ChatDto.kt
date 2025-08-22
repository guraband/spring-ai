package study.springai.dto

import study.springai.entity.Chat

data class ChatRequest(
    val message: String,
    val userId: String = "defaultUserId",
)

class ChatResponse(
    chat: Chat
) {
    val type = chat.type
    val content = chat.content
}
