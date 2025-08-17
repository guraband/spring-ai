package study.springai.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.test.StepVerifier

@SpringBootTest
class OpenAiServiceTest {

    @Autowired
    private lateinit var openAiService: OpenAiService

    @Test
    fun `generate chat response should return valid text`() {
        // Given
        val prompt = "안녕하세요! 간단한 인사말을 해주세요."

        // When
        val result = openAiService.generate(prompt)

        // Then
        assertNotNull(result)
        assertTrue(result!!.isNotEmpty())
        println("Generated response: $result")
    }

    @Test
    fun `generate chat response with custom model and temperature`() {
        // Given
        val prompt = "0.9와 0.11중 큰 수는 무엇인가요?"
        val model = "gpt-4o-mini"
        val temperature = 0.1

        // When
        val result = openAiService.generate(prompt, model, temperature)

        // Then
        assertNotNull(result)
        assertTrue(result!!.isNotEmpty())
        println("Generated response with custom settings: $result")
    }

    @Test
    fun `generate stream response should return flux of strings`() {
        // Given
        val prompt = "간단한 이야기를 들려주세요."

        // When & Then
        StepVerifier.create(openAiService.generateStream(prompt))
            .expectNextCount(1)
            .verifyComplete()
    }

    @Test
    fun `generate embedding should return valid embeddings`() {
        // Given
        val texts = listOf("안녕하세요", "Hello world", "테스트 문장입니다")

        // When
        val embeddings = openAiService.generateEmbedding(texts)

        // Then
        assertNotNull(embeddings)
        assertEquals(texts.size, embeddings.size)
        embeddings.forEach { embedding ->
            assertTrue(embedding.isNotEmpty())
            println("Embedding dimension: ${embedding.size}")
        }
    }

    @Test
    fun `generate embedding with custom model`() {
        // Given
        val texts = listOf("Spring Boot", "Kotlin", "AI")
        val model = "text-embedding-3-small"

        // When
        val embeddings = openAiService.generateEmbedding(texts, model)

        // Then
        assertNotNull(embeddings)
        assertEquals(texts.size, embeddings.size)
        embeddings.forEach { embedding ->
            assertTrue(embedding.isNotEmpty())
        }
    }

    @Test
    fun `generate image should return valid image URLs`() {
        // Given
        val prompt = "A cute cat sitting on a chair"
        val count = 1

        // When
        val imageUrls = openAiService.generateImage(prompt, count)

        // Then
        assertNotNull(imageUrls)
        assertEquals(count, imageUrls.size)
        imageUrls.forEach { url ->
            assertTrue(url.isNotEmpty())
            assertTrue(url.startsWith("http"))
            println("Generated image URL: $url")
        }
    }

    @Test
    fun `generate image with custom settings`() {
        // Given
        val prompt = "A beautiful sunset over mountains"
        val count = 1
        val width = 1024
        val height = 1024
        val quality = "standard"
        val model = "dall-e-3"

        // When
        val imageUrls = openAiService.generateImage(prompt, count, width, height, quality, model)

        // Then
        assertNotNull(imageUrls)
        assertEquals(count, imageUrls.size)
        imageUrls.forEach { url ->
            assertTrue(url.isNotEmpty())
            assertTrue(url.startsWith("http"))
            println("Generated image URL with custom settings: $url")
        }
    }

    @Test
    fun `generate stream should handle multiple responses`() {
        // Given
        val prompt = "1부터 5까지 숫자를 하나씩 말해주세요."

        // When & Then
        StepVerifier.create(openAiService.generateStream(prompt))
            .expectNextCount(1)
            .verifyComplete()
    }
}
