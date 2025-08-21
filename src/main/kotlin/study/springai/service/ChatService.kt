package study.springai.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import study.springai.dto.ChatResponse
import study.springai.repository.ChatRepository

@Service
@Transactional(readOnly = true)
class ChatService(
    private val chatRepository: ChatRepository,
) {
    fun getChatHistory(userId: String): List<ChatResponse> {
        return chatRepository.findAllByUserIdOrderById(userId)
            .map { ChatResponse(it) }
    }
}