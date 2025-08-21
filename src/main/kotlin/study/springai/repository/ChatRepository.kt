package study.springai.repository

import org.springframework.data.jpa.repository.JpaRepository
import study.springai.entity.Chat

interface ChatRepository : JpaRepository<Chat, Long> {
    fun findAllByUserIdOrderById(userId: String): List<Chat>
}