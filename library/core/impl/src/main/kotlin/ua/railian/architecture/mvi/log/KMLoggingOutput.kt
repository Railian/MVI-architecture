package ua.railian.architecture.mvi.log

import org.lighthousegames.logging.KmLogging
import org.lighthousegames.logging.logging

public val Output.Companion.KMLogging: Output
    get() = KMLoggingOutput()

private class KMLoggingOutput : Output {

    override fun invoke(
        tag: String?,
        priority: Priority,
        message: () -> String,
    ) {
        KmLogging.createTag()
        logging(tag).apply {
            when (priority) {
                Priority.Verbose -> verbose(message)
                Priority.Debug -> debug(message)
                Priority.Info -> info(message)
                Priority.Warn -> warn(message)
                Priority.Error -> error(message)
            }
        }
    }
}
