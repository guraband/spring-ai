package study.springai.service

import org.springframework.ai.chat.messages.AssistantMessage
import org.springframework.ai.chat.messages.SystemMessage
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.openai.OpenAiChatModel
import org.springframework.ai.openai.OpenAiChatOptions
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class OpenAiService(
    private val openAiChatModel: OpenAiChatModel
) {
    fun generate(text: String): String? {
        val systemMessage = SystemMessage("")
        val userMessage = UserMessage(text)
        val assistantMessage = AssistantMessage("")

        val options = OpenAiChatOptions.builder()
            .model("gpt-4o-mini")
            .temperature(0.7)
            .build()

        val prompt = Prompt(listOf(systemMessage, userMessage, assistantMessage), options)
        val response = openAiChatModel.call(prompt)
        return response.result.output.text
    }

    fun generateStream(text: String): Flux<String> {
        val systemMessage = SystemMessage("")
        val userMessage = UserMessage(text)
        val assistantMessage = AssistantMessage("")

        val options = OpenAiChatOptions.builder()
            .model("gpt-4o-mini")
            .temperature(0.7)
            .build()

        val prompt = Prompt(listOf(systemMessage, userMessage, assistantMessage), options)
        return openAiChatModel.stream(prompt)
            .mapNotNull { it.result.output.text }
    }
}