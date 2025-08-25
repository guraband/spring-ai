package study.springai.service

import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor
import org.springframework.ai.chat.memory.ChatMemoryRepository
import org.springframework.ai.chat.memory.MessageWindowChatMemory
import org.springframework.ai.chat.messages.AssistantMessage
import org.springframework.ai.chat.messages.MessageType
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.embedding.EmbeddingRequest
import org.springframework.ai.image.ImagePrompt
import org.springframework.ai.openai.*
import org.springframework.ai.vectorstore.SearchRequest
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers
import study.springai.dto.CityResponse
import study.springai.entity.Chat
import study.springai.repository.ChatRepository

@Service
@Transactional(readOnly = true)
class OpenAiService(
    private val openAiChatModel: OpenAiChatModel,
    private val openAiEmbeddingModel: OpenAiEmbeddingModel,
    private val openAiImageModel: OpenAiImageModel,
    private val chatMemoryRepository: ChatMemoryRepository,
    private val chatRepository: ChatRepository,
    private val openAiChatClient: ChatClient,
    private val vectorStore: VectorStore
) {
    companion object {
        private const val DEFAULT_CHAT_MODEL = "gpt-4o-mini"
        private const val DEFAULT_TEMPERATURE = 0.5
        private const val DEFAULT_EMBEDDING_MODEL = "text-embedding-3-small"
        private const val DEFAULT_IMAGE_MODEL = "gpt-image-1"
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

    fun generateUsingOutputConverter(
        text: String,
        model: String = DEFAULT_CHAT_MODEL,
        temperature: Double = DEFAULT_TEMPERATURE
    ): CityResponse? {
        val options = createChatOptions(model, temperature)
        val prompt = Prompt(listOf(UserMessage(text)), options)
        val response = openAiChatModel.call(prompt)

        return openAiChatClient.prompt(prompt)
            .call()
            .entity(CityResponse::class.java)
    }

    @Transactional
    fun generateStream(
        text: String,
        model: String = DEFAULT_CHAT_MODEL,
        temperature: Double = DEFAULT_TEMPERATURE,
        userId: String = "1",
    ): Flux<String> {
        // 전체 대화 저장용
        val userChat = Chat(
            userId = userId,
            content = text,
        )

        val chatMemory = createChatMemory()
        chatMemory.add(userId, UserMessage(text))

        // 옵션
        val options = createChatOptions(model, temperature)

        val ragAdvisor = QuestionAnswerAdvisor.builder(vectorStore)
            .searchRequest(
                SearchRequest.builder()
                    .similarityThreshold(0.8)
                    .topK(6)
                    .build()
            )
            .build()

        // 프롬프트
        val prompt = Prompt(chatMemory.get(userId), options)

        // 응답 메시지를 저장할 임시 버퍼
        val responseBuffer = StringBuilder()

        return openAiChatClient.prompt(prompt)
            .advisors(ragAdvisor)
            .stream()
            .content()
            .map { response ->
                // 응답 메시지를 버퍼에 추가
                responseBuffer.append(response)
                response
            }
            .publishOn(Schedulers.boundedElastic())
            .doOnComplete {
                saveChatHistory(userId, userChat, responseBuffer.toString())
            }
        /*
        return openAiChatModel.stream(prompt)
            .mapNotNull { response ->
                val responseMessage = response.result.output.text ?: ""
                responseBuffer.append(responseMessage)
                responseMessage
            }
            .publishOn(Schedulers.boundedElastic())
            .doOnComplete {
                saveChatHistory(userId, userChat, responseBuffer.toString())
            }
        */
    }

    @Transactional
    fun saveChatHistory(userId: String, userChat: Chat, finalResponse: String) {
        val chatMemory = createChatMemory()
        chatMemory.add(userId, AssistantMessage(finalResponse))
        chatMemoryRepository.saveAll(userId, chatMemory.get(userId))

        val assistantChat = Chat(
            userId = userId,
            type = MessageType.ASSISTANT,
            content = finalResponse,
        )

        chatRepository.saveAll(listOf(userChat, assistantChat))
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