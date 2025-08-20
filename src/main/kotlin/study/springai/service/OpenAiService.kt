package study.springai.service

import org.springframework.ai.chat.memory.ChatMemoryRepository
import org.springframework.ai.chat.memory.MessageWindowChatMemory
import org.springframework.ai.chat.messages.AssistantMessage
import org.springframework.ai.chat.messages.SystemMessage
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.embedding.EmbeddingRequest
import org.springframework.ai.image.ImagePrompt
import org.springframework.ai.openai.*
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class OpenAiService(
    private val openAiChatModel: OpenAiChatModel,
    private val openAiEmbeddingModel: OpenAiEmbeddingModel,
    private val openAiImageModel: OpenAiImageModel,
    private val chatMemoryRepository: ChatMemoryRepository,
) {
    companion object {
        private const val DEFAULT_MODEL_NAME = "gpt-4o-mini"
        private const val DEFAULT_TEMPERATURE = 0.5
        private const val DEFAULT_EMBEDDING_MODEL_NAME = "gpt-4o-mini"
        private const val DEFAULT_IMAGE_MODEL_NAME = "gpt-image-1"
    }

    // 1. Chat generation methods
    fun generate(
        text: String,
        model: String = DEFAULT_MODEL_NAME,
        temperature: Double = DEFAULT_TEMPERATURE
    ): String? {
        val prompt = createPrompt(text, model, temperature)
        val response = openAiChatModel.call(prompt)
        return response.result.output.text
    }

    fun generateStream(
        text: String,
        model: String = DEFAULT_MODEL_NAME,
        temperature: Double = DEFAULT_TEMPERATURE
    ): Flux<String> {

        // TODO userId와 channelId를 이용해 채팅 메모리 저장
        val chatId = "tempUser_1"

        val chatMemory = MessageWindowChatMemory.builder()
            .maxMessages(10)
            .chatMemoryRepository(chatMemoryRepository)
            .build()
        chatMemory.add(chatId, UserMessage(text))

        val options = OpenAiChatOptions.builder()
            .model(model)
            .temperature(temperature)
            .build()

        val prompt = Prompt(chatMemory.get(chatId), options)

        // 응답 메시지를 저장할 임시 버퍼
        val responseBuffer = StringBuilder()

        return openAiChatModel.stream(prompt)
            .mapNotNull { response ->
                val responseMessage = response.result.output.text ?: ""
                responseBuffer.append(responseMessage)
                responseMessage
            }
            .doOnComplete {
                val finalResponse = responseBuffer.toString()
                chatMemory.add(chatId, AssistantMessage(finalResponse))
                chatMemoryRepository.saveAll(chatId, chatMemory.get(chatId))
            }
    }

    private fun createPrompt(
        text: String,
        model: String,
        temperature: Double,
    ): Prompt {
        val systemMessage = SystemMessage("")
        val userMessage = UserMessage(text)
        val assistantMessage = AssistantMessage("")

        val options = OpenAiChatOptions.builder()
            .model(model)
            .temperature(temperature)
            .build()

        return Prompt(listOf(systemMessage, userMessage, assistantMessage), options)
    }

    // 2. Embedding generation
    fun generateEmbedding(
        texts: List<String>,
        model: String = DEFAULT_EMBEDDING_MODEL_NAME
    ): List<FloatArray> {
        val options = OpenAiEmbeddingOptions.builder()
            .model(model)
            .build()

        val prompt = EmbeddingRequest(texts, options)

        val response = openAiEmbeddingModel.call(prompt)
        return response.results.stream().map { it.output }.toList()
    }

    // 3. Image generation
    fun generateImage(
        text: String,
        count: Int = 1,
        width: Int = 1024,
        height: Int = 1024,
        quality: String = "high",
        model: String = DEFAULT_IMAGE_MODEL_NAME
    ): List<String> {
        val options = OpenAiImageOptions.builder()
            .N(count)
            .width(width)
            .height(height)
            .quality(quality)
            .model(model)
            .build()

        val prompt = ImagePrompt(text, options)

        val response = openAiImageModel.call(prompt)

        return response.results.stream()
            .map { it.output.url }
            .toList()
    }
}