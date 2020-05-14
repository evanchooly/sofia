package com.antwerkz.sofia

import com.antwerkz.kibble.model.ClassOrObjectHolder
import com.antwerkz.kibble.model.FunctionHolder
import com.antwerkz.kibble.model.KibbleFile
import com.antwerkz.kibble.model.KibbleInterface
import com.antwerkz.kibble.model.KibbleObject
import com.antwerkz.kibble.model.Modality.OPEN
import com.antwerkz.kibble.model.Mutability.VAL
import com.antwerkz.kibble.model.Visibility.PRIVATE
import com.antwerkz.sofia.LoggingType.SLF4J
import freemarker.template.Configuration
import freemarker.template.Template
import java.io.File
import java.io.InputStreamReader
import java.io.StringWriter

class LocalizerGenerator(val config: SofiaConfig) {

    fun write() {
        val packagePath = config.packageName.replace('.', '/')
        config.outputDirectory.mkdirs()
        if (config.generateJava) {
            generate(packagePath, "java")
        } else if (config.generateKotlin) {
            generateKotlin(packagePath)
        }

        /*
                if (config.generateJavascript) {
                    file = config.javascriptOutputFile!!
                    System.out.printf("Generating javascript code in to %s\n", file)
                    file.parentFile.mkdirs()
                    try {
                        PrintWriter(file, "UTF-8").use { stream ->
                            stream.println(this.generateJavascript())
                            stream.flush()
                        }
                    } catch (e: Exception) {
                        throw RuntimeException(e.message, e)
                    }

                }
        */
    }

    private fun generate(packagePath: String, extension: String) {
        val file = File(config.outputDirectory, "${packagePath}/${config.className}.${extension}")
        file.parentFile.mkdirs()
        val string = StringWriter()
        val template = Template("sofia", InputStreamReader(javaClass.getResourceAsStream("/sofia-${extension}.ftl")), Configuration())

        template.process(config, string)
        file.writeText(string.toString(), charset("UTF-8"))
    }

    private fun generateKotlin(packagePath: String) {
        val file = File(config.outputDirectory, "${packagePath}/${config.className}.kt")
        file.parentFile.mkdirs()
        val kibbleFile = KibbleFile("${config.className}.kt")
        kibbleFile.pkgName = config.packageName
        config.loggingType.imports
                .forEach {
                    kibbleFile.addImport(it)
                }
        kibbleFile.addImport("java.util.*")
        kibbleFile.addImport("java.text.MessageFormat")
        val sofia = kibbleFile.addObject(config.className)

        sofia.addProperty("logger", mutability = VAL,
                initializer = """LoggerFactory.getLogger("${config.packageName}.${config.className}")""")
        sofia.addProperty("IMPLS", mutability = VAL,
                initializer = """mutableMapOf<String, Localized>()""")

        sofia.initBlock = "IMPLS.put(\"\", LocalizedImpl())\n" +
                config.bundles.keys.joinToString("\n") {
                    "IMPLS.put(\"$it\", LocalizedImpl_$it())"
                }

        addGet(sofia)
        val localized = sofia.addInterface("Localized")
        config.methods.forEach {
            addMethods(sofia, it)
            addLocalizer(localized, it)
        }
        sofia.addClass("LocalizedImpl").apply {
            modality = OPEN
            visibility = PRIVATE
            implement("Localized")
        }
        addLocalizerImpls(sofia)
        kibbleFile.toSource()
                .toFile(kibbleFile.outputFile(config.outputDirectory))

    }

    private fun addLocalizer(localized: ClassOrObjectHolder, method: Method, isOverride: Boolean = false) {
        val function = localized.addFunction(method.methodName, "String")
        function.overriding = isOverride
        method.parameters.forEach { param ->
            function.addParameter(param.second, param.first)
        }
        function.body =
                if (method.parameters.isNotEmpty()) {
                    val arguments = method.arguments.joinToString(", ")
                    """return MessageFormat.format("${'"'}"${method.value}"${'"'}", $arguments)"""
                } else {
                    """return "${'"'}"${method.value}"${'"'}""""
                }
    }

    private fun addLocalizerImpls(sofia: KibbleObject) {
        config.bundles.forEach { bundle, methods ->
            //     private open class LocalizedImpl_${bundle}: LocalizedImpl${findParentExtension(bundle)}() {
            val impl = sofia.addClass("LocalizedImpl_$bundle").apply {
                modality = OPEN
                visibility = PRIVATE
                extend("LocalizedImpl${config.findParentExtension(bundle)}")
            }
            methods.forEach {
                addLocalizer(impl, it, true)
            }
        }
    }

    private fun addMethods(sofia: FunctionHolder, it: Method, isOverride: Boolean = false) {
        val function = sofia.addFunction(it.methodName, "String")
        function.overriding = isOverride
        function.addAnnotation("kotlin.jvm.JvmOverloads")
        it.parameters.forEach { param ->
            function.addParameter(param.second, param.first)
        }
        function.addParameter("locale", "java.util.Locale?", if (!isOverride) "null" else null)
        function.body = "return get(locale).${it.methodName}(${it.arguments.joinToString(", ")})"

        if (!isOverride && it.logged) {
            val logged = sofia.addFunction(it.loggerName)
            logged.addAnnotation("kotlin.jvm.JvmOverloads")
            it.parameters.forEach { param ->
                logged.addParameter(param.second, param.first)
            }
            logged.addParameter("locale", "java.util.Locale?", "null")
            logged.body = when (config.loggingType) {
                SLF4J -> "if(logger.is${it.logLevel!!.capitalizeFirstLetter()}Enabled()) {\n"
                else -> "if(logger.isLoggable(Level.${it.logLevel?.toUpperCase()})) {\n"
            }
            val arguments = (it.arguments + "locale").joinToString(", ")
            logged.body += """    return logger.${it.logLevel}(${it.methodName}($arguments))
                    |}
                """.trimMargin()
        }
    }

    private fun addGet(sofia: KibbleObject) {
        val get = sofia.addFunction(name = "get", type = "Localized", body = """
        if (locale == null) {
            return IMPLS.get("")!!
        }
        var name = locale.toString()
        while (!name.isEmpty()) {
            val localized = IMPLS.get(name)
            if(localized != null) {
                return localized
            } else {
                name = if (name.contains("_")) name.substring(0, name.lastIndexOf('_')) else ""
            }
        }
        return IMPLS.get("")!!
                """.trimIndent())
        get.addParameter("locale", "java.util.Locale?")
    }

    companion object {
        fun capitalize(text: String): String {
            return Character.toTitleCase(text[0]) + text.substring(1)
        }
    }
}

fun String?.capitalizeFirstLetter(): String {
    return this?.let {
        this[0].toUpperCase() + this.drop(1)
    } ?: ""
}