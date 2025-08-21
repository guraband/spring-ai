package study.springai.config

import org.springframework.ai.chat.memory.ChatMemoryRepository
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.transaction.support.AbstractPlatformTransactionManager
import javax.sql.DataSource

@Configuration
@EnableTransactionManagement
class AiConfig {
    @Bean
    fun transactionManager(dataSource: DataSource): PlatformTransactionManager {
        return JpaTransactionManager().apply {
            this.dataSource = dataSource
        }
    }
    @Bean
    fun chatMemoryRepository(
        jdbcTemplate: JdbcTemplate, 
        platformTransactionManager: PlatformTransactionManager,
    ): ChatMemoryRepository {
        // in-memory 방식 사용시
//        return InMemoryChatMemoryRepository()
        return JdbcChatMemoryRepository.builder()
            .jdbcTemplate(jdbcTemplate)
            .transactionManager(platformTransactionManager)
            .build()
    }
}