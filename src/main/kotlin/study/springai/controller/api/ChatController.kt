package study.springai.controller.api

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import study.springai.dto.ChatRequest
import study.springai.service.OpenAiService

@RestController
@RequestMapping("/api/chat")
class ChatController(
    private val openAiService: OpenAiService,
) {
    @PostMapping("")
    fun chat(
        @RequestBody request: ChatRequest
    ): ResponseEntity<String> {
        return ResponseEntity.ok(
            openAiService.generate(request.message),
        )
    }

    @PostMapping("/stream")
    fun streamChat(prompt: String): ResponseEntity<Flux<String>> {
        return ResponseEntity.ok(
            openAiService.generateStream(prompt),
        )
    }
}