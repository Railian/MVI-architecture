package ua.railian.mvi.simple

import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.test.runTest
import org.junit.Test

class MviInitializerTest {

    @Test
    fun `initAsync processes initial intents and sets initialized to true`() = runTest {
        // Arrange
        val initialIntents = flowOf("Intent1", "Intent2")
        val intentProcessor: MviProcessor<String> = mockk(relaxed = true)
        val initializer = MviInitializer(
            viewModelScope = this,
            initialIntents = initialIntents,
            intentProcessor = intentProcessor,
        )

        // Act
        initializer.initAsync().join()

        // Assert
        coVerifyOrder {
            intentProcessor.process(intent = "Intent1", initial = true)
            intentProcessor.process(intent = "Intent2", initial = true)
        }
        coVerify(exactly = 2) {
            intentProcessor.process(intent = any(), initial = any())
        }
    }

    @Test
    fun `initAsync does not reinitialize if already initialized`() = runTest {
        // Arrange
        val initialIntents = flowOf("Intent1", "Intent2")
        val intentProcessor: MviProcessor<String> = mockk(relaxed = true)
        val initializer = MviInitializer(
            viewModelScope = this,
            initialIntents = initialIntents,
            intentProcessor = intentProcessor,
        )

        // Act
        initializer.initAsync().join()
        initializer.initAsync().join()

        // Assert
        coVerifyOrder {
            intentProcessor.process(intent = "Intent1", initial = true)
            intentProcessor.process(intent = "Intent2", initial = true)
        }
        coVerify(exactly = 2) {
            intentProcessor.process(intent = any(), initial = any())
        }
    }

    @Test
    fun `initAsync handles concurrent initialization attempts`() = runTest {
        // Arrange
        val initialIntents = flowOf("Intent1", "Intent2")
        val intentProcessor: MviProcessor<String> = mockk(relaxed = true)
        val initializer = MviInitializer(
            viewModelScope = this,
            initialIntents = initialIntents,
            intentProcessor = intentProcessor,
        )

        // Act
        listOf(
            initializer.initAsync(),
            initializer.initAsync(),
        ).joinAll()

        // Assert
        coVerifyOrder {
            intentProcessor.process(intent = "Intent1", initial = true)
            intentProcessor.process(intent = "Intent2", initial = true)
        }
        coVerify(exactly = 2) {
            intentProcessor.process(intent = any(), initial = any())
        }
    }
}
