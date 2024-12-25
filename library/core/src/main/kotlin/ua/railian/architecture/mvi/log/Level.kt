package ua.railian.architecture.mvi.log

import ua.railian.architecture.mvi.log.Priority.Debug
import ua.railian.architecture.mvi.log.Priority.Error
import ua.railian.architecture.mvi.log.Priority.Info
import ua.railian.architecture.mvi.log.Priority.Verbose
import ua.railian.architecture.mvi.log.Priority.Warn

public enum class Level { Verbose, Debug, Info, Warn, Error, Off }

public val Level.priorities: Set<Priority>
    get() = levelToPriorities[this].orEmpty()

private val levelToPriorities = Level.entries.associateWith { level ->
    when (level) {
        Level.Verbose -> setOf(Verbose, Debug, Info, Warn, Error)
        Level.Debug -> setOf(Debug, Info, Warn, Error)
        Level.Info -> setOf(Info, Warn, Error)
        Level.Warn -> setOf(Warn, Error)
        Level.Error -> setOf(Error)
        Level.Off -> emptySet()
    }
}
