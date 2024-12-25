
fun String.trimNewLines(): String {
    return buildString {
        this@trimNewLines.lines()
            .windowed(size = 2, partialWindows = true) { lines ->
                val currentLine = lines.first()
                val nextLine = lines.lastOrNull()
                when (nextLine.isNullOrBlank()) {
                    true -> appendLine(currentLine)
                    else -> append(currentLine.trimEnd()).append(" ")
                }
            }
    }
}
