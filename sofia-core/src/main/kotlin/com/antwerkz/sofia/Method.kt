package com.antwerkz.sofia

import java.text.DateFormat
import java.text.Format
import java.text.MessageFormat
import java.text.NumberFormat
import java.util.ArrayList

class Method(val language: String, val loggingType: LoggingType, val key: String, val value: String) {
    var logged = java.lang.Boolean.FALSE
        private set
    var argCount: Int = 0
        private set
    val arguments = ArrayList<String>()
    val parameters = ArrayList<Pair<String, String>>()
    var logLevel: String? = null
        private set
    var name: String

    init {
        name = key
        if (key.startsWith("@")) {
            logged = java.lang.Boolean.TRUE
            logLevel = key.substring(1, key.indexOf(".")).toLowerCase()
            name = key.substring(key.indexOf(".") + 1)
        }
        countArguments()
    }

    private fun countArguments() {
        val messageFormat = MessageFormat(value)
        val formats = messageFormat.formats
        argCount = messageFormat.formats.size
        for (i in 0..argCount - 1) {
            parameters.add(Pair(getType(language, formats[i]), "arg${i}"))
            arguments.add("arg${i}")
        }
    }

    private fun getType(language: String, format: Format?): String {
        return when (format) {
            null -> if (language == "java") "Object" else "Any"
            is DateFormat -> "Date"
            is NumberFormat -> "Number"
            else -> "Object"
        }

    }

    val methodName: String
        get() {
            val parts = name.split("\\.".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
            val name = StringBuilder()
            for (part in parts) {
                name.append(if (name.length != 0) LocalizerGenerator.capitalize(part) else part)
            }
            return name.toString()
        }

    val loggerName: String
        get() {
            val parts = name.split("\\.".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
            val name = StringBuilder()
            name.append("log")
            for (part in parts) {
                name.append(Character.toTitleCase(part.get(0))).append(part.substring(1))
            }
            return name.toString()
        }

        override fun toString(): String {
            return "Method {key='${key}', parameters=${parameters}', logged=${logged}', logLevel='${logLevel}, value='${value}'," +
                    " argCount=${argCount}, arguments=${arguments}}"
        }
}