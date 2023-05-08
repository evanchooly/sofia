package com.antwerkz.sofia

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.util.ArrayList
import java.util.Locale
import java.util.Properties
import java.util.TreeMap

class SofiaConfig(
        val propertiesFile: File,
        var outputDirectory: File,
        var loggingType: LoggingType = LoggingType.NONE,
        var packageName: String = "com.antwerkz.sofia",
        var generateJava: Boolean = true,
        var generateKotlin: Boolean = false,
        var bundleName: String = propertiesFile.nameWithoutExtension,
        var className: String = bundleName.capitalize(),
        var charset: Charset = Charset.forName("ISO-8859-1")) {

    var bundles: MutableMap<String, List<Method>> = sortedMapOf()
    val methods: List<Method>
    private val language: String = when {
        generateJava -> "java"
        generateKotlin -> "kotlin"
        else -> throw RuntimeException("Either 'java' or 'kotlin' must be selected to generate")
    }

    init {
        methods = mapMethods(loadProperties(propertiesFile))
        discoverBundles(propertiesFile)
    }

    fun findParentExtension(bundle: String): String {
        var parent = bundle
        do {
            parent = parent.split("_").dropLast(1).joinToString("_")
        } while (bundles[parent] == null && parent != "")
        return if (parent == "") parent else "_$parent"
    }

    private fun mapMethods(map: MutableMap<String, String>): ArrayList<Method> {
        val list = ArrayList<Method>()
        for (entry in map.entries) {
            val method = Method(language, loggingType, entry.key, entry.value)
            list.add(method)
            if (method.logged && !loggingType.loggingLevels.contains(method.logLevel!!)) {
                throw IllegalArgumentException("Invalid logging level '${method.logLevel}' for logging type '$loggingType'")
            }
        }
        return list
    }

    private fun discoverBundles(propertiesFile: File) {
        val name = propertiesFile.name
        val rootDir = propertiesFile.parent
        val last = name.lastIndexOf('.')
        if (!name.matches(".*_[a-zA-Z]+[_a-zA-Z]*.properties".toRegex())) {
            for (locale in Locale.getAvailableLocales()) {
                val file = File(rootDir, "%s_%s%s".format(name.substring(0, last), locale.toString(), name.substring(last)))
                if (file.exists()) {
                    bundles.put(locale.toString(), mapMethods(loadProperties(file)))
                }
            }
        }
    }

    private fun loadProperties(file: File): MutableMap<String, String> {
        val map = TreeMap<String, String>()
        try {
            InputStreamReader(FileInputStream(file), charset).use { reader ->
                val props = Properties()
                props.load(reader)
                for (entry in props.entries) {
                    map.put(entry.key as String, entry.value as String)
                }
            }
        } catch (e: IOException) {
            throw RuntimeException(e.message, e)
        }

        if (map.isEmpty()) {
            throw RuntimeException("No properties found in properties file.")
        }
        return map
    }
}
