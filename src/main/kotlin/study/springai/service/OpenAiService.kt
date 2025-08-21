package study.springai.service

import org.springframework.ai.chat.memory.ChatMemoryRepository
import org.springframework.ai.chat.memory.MessageWindowChatMemory
import org.springframework.ai.chat.messages.AssistantMessage
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.embedding.EmbeddingRequest
import org.springframework.ai.image.ImagePrompt
import org.springframework.ai.openai.*
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import study.springai.repository.ChatRepository

@Service
class OpenAiService(
    private val openAiChatModel: OpenAiChatModel,
    private val openAiEmbeddingModel: OpenAiEmbeddingModel,
    private val openAiImageModel: OpenAiImageModel,
    private val chatMemoryRepository: ChatMemoryRepository,
    private val chatRepository: ChatRepository,
) {
    companion object {
        private const val DEFAULT_CHAT_MODEL = "gpt-4o-mini"
        private const val DEFAULT_TEMPERATURE = 0.5
        private const val DEFAULT_EMBEDDING_MODEL = "text-embedding-3-small"
        private const val DEFAULT_IMAGE_MODEL = "gpt-image-1"
        private const val DEFAULT_CHAT_ID_PREFIX = "user_"
        private const val DEFAULT_MAX_MESSAGES = 10
    }

    // 1. Chat generation methods
    fun generate(
        text: String,
        model: String = DEFAULT_CHAT_MODEL,
        temperature: Double = DEFAULT_TEMPERATURE
    ): String? {
        val options = createChatOptions(model, temperature)
        val prompt = Prompt(listOf(UserMessage(text)), options)
        val response = openAiChatModel.call(prompt)
        return response.result.output.text
    }

    fun generateStream(
        text: String,
        model: String = DEFAULT_CHAT_MODEL,
        temperature: Double = DEFAULT_TEMPERATURE,
        userId: String = "defaultUser"
    ): Flux<String> {

        // TODO 개선 필요
        val chatId = DEFAULT_CHAT_ID_PREFIX + userId

        val chatMemory = createChatMemory()
        chatMemory.add(chatId, UserMessage(text))

        val options = createChatOptions(model, temperature)
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

    // 공통 메소드들
    private fun createChatOptions(model: String, temperature: Double): OpenAiChatOptions {
        return OpenAiChatOptions.builder()
            .model(model)
            .temperature(temperature)
            .build()
    }

    private fun createChatMemory(): MessageWindowChatMemory {
        return MessageWindowChatMemory.builder()
            .maxMessages(DEFAULT_MAX_MESSAGES)
            .chatMemoryRepository(chatMemoryRepository)
            .build()
    }

    // 2. Embedding generation
    fun generateEmbedding(
        texts: List<String>,
        model: String = DEFAULT_EMBEDDING_MODEL
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
        model: String = DEFAULT_IMAGE_MODEL
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