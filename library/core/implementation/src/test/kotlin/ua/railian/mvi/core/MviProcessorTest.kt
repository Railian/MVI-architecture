package ua.railian.mvi.core

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.mockk
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Test
import ua.railian.mvi.log.Category
import ua.railian.mvi.log.MviLogger
import ua.railian.mvi.log.Priority
import ua.railian.mvi.pipeline.PipelineId
import ua.railian.mvi.pipeline.PipelineIdGenerator
import kotlin.test.assertFailsWith

class MviProcessorTest {

    @Test
    fun `process logs and processes intent correctly`() = runTest {
        // Arrange
        val mockLogger = mockk<MviLogger>(relaxed = true)
        val mockPipelineId = mockk<PipelineId>()
        val mockPipelineIdGenerator = mockk<PipelineIdGenerator>()
        val mockProcessFunction: suspend (PipelineId, String) -> Unit = mockk(relaxed = true)

        coEvery { mockPipelineIdGenerator.next() } returns mockPipelineId

        val processor = MviProcessor(
            viewModelScope = this,
            pipelineIdGenerator = mockPipelineIdGenerator,
            process = mockProcessFunction,
            logger = mockLogger,
        )

        // Act
        processor.process(intent = "TestIntent")

        // Assert
        coVerifyOrder {
            mockLogger.log(
                priority = Priority.Info,
                category = Category.Intent,
                pipelineId = mockPipelineId,
                message = match { it().contains("process intent TestIntent") }
            )
            mockProcessFunction.invoke(any(), "TestIntent")
            mockLogger.log(
                priority = Priority.Info,
                category = Category.Intent,
                pipelineId = mockPipelineId,
                message = match { it().startsWith("intent TestIntent was processed in ") },
            )
        }
    }

    @Test
    fun `processAsync calls process function in a coroutine`() = runTest {
        // Arrange
        val mockLogger = mockk<MviLogger>(relaxed = true)
        val mockPipelineId = mockk<PipelineId>()
        val mockPipelineIdGenerator = mockk<PipelineIdGenerator>()
        val mockProcessFunction: suspend (PipelineId, String) -> Unit = mockk(relaxed = true)

        coEvery { mockPipelineIdGenerator.next() } returns mockPipelineId

        val processor = MviProcessor(
            viewModelScope = this,
            pipelineIdGenerator = mockPipelineIdGenerator,
            process = mockProcessFunction,
            logger = mockLogger,
        )

        // Act
        processor.processAsync(intent = "TestIntent").join()

        // Assert
        // Assert
        coVerifyOrder {
            mockLogger.log(
                priority = Priority.Info,
                category = Category.Intent,
                pipelineId = mockPipelineId,
                message = match { it().contains("process intent TestIntent") }
            )
            mockProcessFunction.invoke(any(), "TestIntent")
            mockLogger.log(
                priority = Priority.Info,
                category = Category.Intent,
                pipelineId = mockPipelineId,
                message = match { it().startsWith("intent TestIntent was processed in ") },
            )
        }
    }

    @Test
    fun `process handles cancellations correctly`() = runTest {
        // Arrange
        val mockLogger = mockk<MviLogger>(relaxed = true)
        val mockPipelineId = mockk<PipelineId>()
        val mockPipelineIdGenerator = mockk<PipelineIdGenerator>()
        val mockProcessFunction: suspend (PipelineId, String) -> Unit = mockk(relaxed = true)

        coEvery { mockPipelineIdGenerator.next() } returns mockPipelineId
        coEvery { mockProcessFunction.invoke(any(), any()) } throws CancellationException()

        val processor = MviProcessor(
            viewModelScope = this,
            pipelineIdGenerator = mockPipelineIdGenerator,
            process = mockProcessFunction,
            logger = mockLogger,
        )

        // Act & Assert
        assertFailsWith<CancellationException> {
            processor.process(intent = "CancelledIntent")
        }
        coVerify {
            mockLogger.log(
                priority = Priority.Info,
                category = Category.Intent,
                pipelineId = mockPipelineId,
                message = match { it().contains("process intent CancelledIntent") },
            )
            mockProcessFunction.invoke(any(), "CancelledIntent")
            mockLogger.log(
                priority = Priority.Warn,
                category = Category.Intent,
                pipelineId = mockPipelineId,
                message = match { it().startsWith("intent CancelledIntent was cancelled") },
            )
        }
    }

    @Test
    fun `process handles failures correctly`() = runTest {
        // Arrange
        val mockLogger = mockk<MviLogger>(relaxed = true)
        val mockPipelineId = mockk<PipelineId>()
        val mockPipelineIdGenerator = mockk<PipelineIdGenerator>()
        val mockProcessFunction: suspend (PipelineId, String) -> Unit = mockk(relaxed = true)

        coEvery { mockPipelineIdGenerator.next() } returns mockPipelineId
        coEvery { mockProcessFunction.invoke(any(), any()) } throws TestException()

        val processor = MviProcessor(
            viewModelScope = this,
            pipelineIdGenerator = mockPipelineIdGenerator,
            process = mockProcessFunction,
            logger = mockLogger,
        )

        // Act & Assert
        assertFailsWith<TestException> {
            processor.process(intent = "FaultyIntent")
        }
        coVerify {
            mockLogger.log(
                priority = Priority.Info,
                category = Category.Intent,
                pipelineId = mockPipelineId,
                message = match { it().contains("process intent FaultyIntent") },
            )
            mockProcessFunction.invoke(any(), "FaultyIntent")
            mockLogger.log(
                priority = Priority.Error,
                category = Category.Intent,
                pipelineId = mockPipelineId,
                message = match { it().startsWith("intent FaultyIntent failed") },
            )
        }
    }

    private class TestException : Exception("Test exception")
}