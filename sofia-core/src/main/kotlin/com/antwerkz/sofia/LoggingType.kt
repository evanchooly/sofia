package com.antwerkz.sofia

import java.util.Locale
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.reflect.KClass
import org.slf4j.LoggerFactory

enum class LoggingType {
    NONE {
        override val factory: KClass<*> = Any::class
        override val logger: KClass<*> = Any::class
        override val imports: List<String> = listOf()
        override val loggingLevels: List<String> = listOf()
    },
    SLF4J {
        override val loggingLevels = listOf("error", "debug", "warn", "info")
        override val imports = listOf("org.slf4j.Logger", "org.slf4j.LoggerFactory")
        override val factory: KClass<*> = LoggerFactory::class
        override val logger: KClass<*> = org.slf4j.Logger::class
    },
    JUL {
        override val imports = listOf("java.util.logging.Logger")
        override val loggingLevels =
            listOf(
                    Level.ALL,
                    Level.SEVERE,
                    Level.WARNING,
                    Level.INFO,
                    Level.CONFIG,
                    Level.FINE,
                    Level.FINER,
                    Level.FINEST
                )
                .map { it.toString().lowercase(Locale.getDefault()) }
                .toList()
        override val factory: KClass<*> = Logger::class
        override val logger: KClass<*> = Logger::class
    };

    abstract val factory: KClass<*>
    abstract val logger: KClass<*>
    abstract val loggingLevels: List<String>
    abstract val imports: List<String>

    companion object {
        fun possibleLoggingLevels(): Set<String> =
            values().map { it.loggingLevels }.flatten().toSet()
    }
}
