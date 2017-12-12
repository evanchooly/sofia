package com.antwerkz.sofia

import java.util.logging.Level

enum class LoggingType {
    NONE {
        override val imports = ""

        override val loggingLevels = listOf<String>()
    },

    SLF4J {
        override val loggingLevels = listOf("error", "debug", "warn", "info")

        override val imports = "import org.slf4j.*;"
    },

    JUL {
        override val imports = "import java.util.logging.*;"

        override val loggingLevels = listOf(Level.ALL, Level.SEVERE, Level.WARNING, Level.INFO,
                Level.CONFIG, Level.FINE, Level.FINER, Level.FINEST)
                .map { it.toString().toLowerCase() }
                .toList()
    };

    abstract val loggingLevels: List<String>

    abstract val imports: String
}
