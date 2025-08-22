package study.springai.controller.api

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import study.springai.dto.ChatRequest
import study.springai.dto.ChatResponse
import study.springai.service.ChatService
import study.springai.service.OpenAiService

@RestController
@RequestMapping("/api/chat")
class ChatController(
    private val openAiService: OpenAiService,
    private val chatService: ChatService,
    private val objectMapper: ObjectMapper,
) {
    private fun createJsonResponse(key: String, value: Any): String {
        return objectMapper.writeValueAsString(mapOf(key to value)) + "\n"
    }

    @PostMapping("")
    fun chat(
        @RequestBody request: ChatRequest
    ): ResponseEntity<String> {
        return ResponseEntity.ok(
            openAiService.generate(request.message),
        )
    }

    @PostMapping("/stream", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun streamChat(@RequestBody request: ChatRequest): ResponseEntity<Flux<String>> {
        return ResponseEntity.ok()
            .header("Cache-Control", "no-cache")
            .header("Connection", "keep-alive")
            .body(
                openAiService.generateStream(userId = request.userId, text = request.message)
                    .filter { it.isNotEmpty() }
                    .map { content -> createJsonResponse("content", content) }
                    .concatWith(Flux.just(createJsonResponse("done", true)))
            )
    }

    @GetMapping("/history/{userId}")
    fun getChatHistory(
        @PathVariable userId: String
    ): ResponseEntity<List<ChatResponse>> {
        return ResponseEntity.ok(
            chatService.getChatHistory(userId)
        )
    }
}