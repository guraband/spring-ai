package study.springai.controller.api

import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
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

    @PostMapping("/stream", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun streamChat(@RequestBody request: ChatRequest): ResponseEntity<Flux<String>> {
        return ResponseEntity.ok()
            .header("Cache-Control", "no-cache")
            .header("Connection", "keep-alive")
            .body(
                openAiService.generateStream(request.message)
                    .filter { it.isNotBlank() }
                    .map { content -> "{\"content\": \"$content\"}\n" }
                    .concatWith(Flux.just("{\"done\": true}\n"))
            )
    }
}