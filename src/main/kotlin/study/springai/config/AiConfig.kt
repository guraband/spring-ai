package study.springai.config

import org.springframework.ai.chat.memory.ChatMemoryRepository
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.transaction.support.AbstractPlatformTransactionManager

@Configuration
class AiConfig {
    @Bean
    fun chatMemoryRepository(
        jdbcTemplate: JdbcTemplate,
        platformTransactionManager: AbstractPlatformTransactionManager,
    ): ChatMemoryRepository {
        // in-memory 방식 사용시
//        return InMemoryChatMemoryRepository()
        return JdbcChatMemoryRepository.builder()
            .jdbcTemplate(jdbcTemplate)
            .transactionManager(platformTransactionManager)
            .build()
    }
}