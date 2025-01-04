package ua.railian.mvi.log

import ua.railian.mvi.log.Priority.Debug
import ua.railian.mvi.log.Priority.Error
import ua.railian.mvi.log.Priority.Info
import ua.railian.mvi.log.Priority.Verbose
import ua.railian.mvi.log.Priority.Warn

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
