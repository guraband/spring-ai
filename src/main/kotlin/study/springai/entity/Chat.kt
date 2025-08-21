package study.springai.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.springframework.ai.chat.messages.MessageType
import java.time.LocalDateTime

@Entity
@Table
class Chat(
    @Column
    var userId: String? = null,

    @Column(columnDefinition = "TEXT")
    var content: String,

    @Enumerated(EnumType.STRING)
    var type: MessageType = MessageType.USER,

    @CreationTimestamp
    @Column(updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set


}
