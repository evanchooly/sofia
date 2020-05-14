package com.antwerkz.sofia

import java.util.logging.Level

enum class LoggingType {
    NONE,

    SLF4J {
        override val loggingLevels = listOf("error", "debug", "warn", "info")

        override val imports =  listOf("org.slf4j.Logger", "org.slf4j.LoggerFactory")
    },

    JUL {
        override val imports = listOf("java.util.logging.Logger")

        override val loggingLevels = listOf(Level.ALL, Level.SEVERE, Level.WARNING, Level.INFO,
                Level.CONFIG, Level.FINE, Level.FINER, Level.FINEST)
                .map { it.toString().toLowerCase() }
                .toList()
    };

    open val loggingLevels: List<String> = emptyList()

    open val imports: List<String> = emptyList()
}
