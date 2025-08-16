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
    companion object {
        private const val MODEL_NAME = "gpt-4o-mini"
        private const val TEMPERATURE = 0.7
    }

    fun generate(text: String): String? {
        val prompt = createPrompt(text)
        val response = openAiChatModel.call(prompt)
        return response.result.output.text
    }

    fun generateStream(text: String): Flux<String> {
        val prompt = createPrompt(text)
        return openAiChatModel.stream(prompt)
            .mapNotNull { it.result.output.text }
    }

    private fun createPrompt(text: String): Prompt {
        val systemMessage = SystemMessage("")
        val userMessage = UserMessage(text)
        val assistantMessage = AssistantMessage("")

        val options = OpenAiChatOptions.builder()
            .model(MODEL_NAME)
            .temperature(TEMPERATURE)
            .build()

        return Prompt(listOf(systemMessage, userMessage, assistantMessage), options)
    }
}