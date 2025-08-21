package study.springai.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.springframework.ai.chat.messages.MessageType
import java.time.LocalDateTime

@Entity
@Table
class Chat(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column
    var userId: String? = null,

    @Column(columnDefinition = "TEXT")
    var content: String,

    @Enumerated(EnumType.STRING)
    var type: MessageType,

    @CreationTimestamp
    @Column(updatable = false)
    var createdAt: LocalDateTime? = null
)
