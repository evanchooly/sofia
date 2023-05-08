package com.antwerkz.sofia

import com.antwerkz.sofia.LoggingType.NONE
import com.antwerkz.sofia.LoggingType.SLF4J
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ClassName.Companion.bestGuess
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.KModifier.OPEN
import com.squareup.kotlinpoet.KModifier.OVERRIDE
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeSpec.Companion.classBuilder
import com.squareup.kotlinpoet.asClassName
import freemarker.template.Configuration
import freemarker.template.Template
import java.io.File
import java.io.InputStreamReader
import java.io.StringWriter
import java.text.MessageFormat
import java.util.Date
import java.util.Locale

class LocalizerGenerator(val config: SofiaConfig) {
    companion object {
        val NULLABLE_LOCALE = Locale::class.asClassName()
            .copy(nullable = true)

        fun capitalize(text: String): String {
            return Character.toTitleCase(text[0]) + text.substring(1)
        }
    }

    fun write() {
        config.outputDirectory.mkdirs()
        if (config.generateJava) {
            generate(config.packageName.replace('.', '/'), "java")
        } else if (config.generateKotlin) {
            generateKotlin(config.packageName)
        }
    }

    private fun generate(packagePath: String, extension: String) {
        val file = File(config.outputDirectory, "${packagePath}/${config.className}.${extension}")
        file.parentFile.mkdirs()
        val string = StringWriter()
        val template = Template("sofia", InputStreamReader(javaClass.getResourceAsStream("/sofia-${extension}.ftl")),
            Configuration(Configuration.VERSION_2_3_32))

        template.process(config, string)
        file.writeText(string.toString(), charset("UTF-8"))
    }

    private fun generateKotlin(packagePath: String) {
        val className = ClassName(packagePath, config.className)
        val sofia = TypeSpec.objectBuilder(className)

        if (config.loggingType != NONE) {
            sofia.addProperty(
                PropertySpec.builder("logger", config.loggingType.logger)
                    .mutable(false)
                    .initializer(CodeBlock.of("""%T.getLogger("${config.packageName}.${config.className}")""", config.loggingType.factory))
                    .build()
            )
        }
        sofia.addProperty(
            PropertySpec.builder("IMPLS", bestGuess("kotlin.collections.MutableMap")
                .parameterizedBy(String::class.asClassName(), bestGuess("Localized")))
                .mutable(false)
                .initializer("mutableMapOf()")
                .build())

        sofia.addInitializerBlock(CodeBlock.of(
            "IMPLS.put(\"\", LocalizedImpl())\n" +
            config.bundles.keys
                .joinToString("\n") {
                "IMPLS.put(\"$it\", LocalizedImpl_$it())"
            }))

        addGet(sofia)
        val localized = TypeSpec.interfaceBuilder("Localized")
        config.methods.forEach {
            addMethods(sofia, it)
            addLocalizer(localized, it)
        }
        sofia.addType(localized.build())
        sofia.addType(classBuilder("LocalizedImpl").apply {
            this.modifiers += listOf(OPEN, KModifier.PRIVATE)
            addSuperinterface(bestGuess("Localized"))
        }.build())
        addLocalizerImpls(sofia)

        FileSpec.builder(className)
            .addType(sofia.build())
            .build()
            .writeTo(config.outputDirectory)
    }

    private fun addLocalizer(localized: TypeSpec.Builder, method: Method, isOverride: Boolean = false) {
        val function = FunSpec.builder(method.methodName)
            .returns(String::class)
        if (isOverride) {
            function.addModifiers(OVERRIDE)
        }
        method.parameters.forEach { param ->
            function.addParameter(ParameterSpec(param.second, className(param)))
        }
        function.addCode(
            if (method.parameters.isNotEmpty()) {
                val arguments = method.arguments.joinToString(", ")
                CodeBlock.of("""return %T.format(
                    |"${'"'}"${method.value}"${'"'}", $arguments)""".trimMargin(), MessageFormat::class)
            } else {
                CodeBlock.of("""return 
                    |"${'"'}"${method.value}"${'"'}"""".trimMargin())
            }
        )

        localized.addFunction(function.build())
    }

    private fun addLocalizerImpls(sofia: TypeSpec.Builder) {
        config.bundles.forEach { bundle, methods ->
            val impl = classBuilder("LocalizedImpl_$bundle")
                .apply {
                    modifiers += listOf(OPEN, KModifier.PRIVATE)
                    superclass(bestGuess("LocalizedImpl${config.findParentExtension(bundle)}"))
                }
            methods.forEach {
                addLocalizer(impl, it, true)
            }
            sofia.addType(impl.build())
        }
    }

    private fun addMethods(sofia: TypeSpec.Builder, it: Method, isOverride: Boolean = false) {
        val function = FunSpec.builder(it.methodName)
            .returns(String::class)

        if(isOverride) {
            function.modifiers += OPEN
        }
        function.addAnnotation(JvmOverloads::class)
        it.parameters.forEach { param ->
            function.addParameter(ParameterSpec(param.second, className(param)))
        }
        val builder = ParameterSpec.builder("locale", NULLABLE_LOCALE)
            .addModifiers()
        if (!isOverride) {
            builder.defaultValue("null")
        }
        function.addParameter(builder.build())

        function.addCode("return get(locale).${it.methodName}(${it.arguments.joinToString(", ")})")

        if (!isOverride && it.logged) {
            val logged = FunSpec.builder(it.loggerName)
            logged.addAnnotation(JvmOverloads::class)
            it.parameters.forEach { param ->
                logged.addParameter(ParameterSpec(param.second, className(param)))
            }
            logged.addParameter(ParameterSpec.builder("locale", NULLABLE_LOCALE)
                .defaultValue("null")
                .build())
            logged.addCode(CodeBlock.of(when (config.loggingType) {
                SLF4J -> "if(logger.is${it.logLevel!!.capitalizeFirstLetter()}Enabled()) {\n"
                else -> "if(logger.isLoggable(Level.${it.logLevel?.toUpperCase()})) {\n"
            }))
            val arguments = (it.arguments + "locale").joinToString(", ")
            logged.addCode("return logger.${it.logLevel}(${it.methodName}($arguments))\n}")
            sofia.addFunction(logged.build())
        }

        sofia.addFunction(function.build())
    }

    private fun className(param: Pair<String, String>): ClassName {
        return when (param.first) {
            "Any" -> Any::class.asClassName()
            "Date" -> Date::class.asClassName()
            "Number" -> Number::class.asClassName()
            else -> TODO(param.first)
        }
    }

    private fun addGet(sofia: TypeSpec.Builder) {
        val get = FunSpec.builder("get")
            .returns(bestGuess("Localized"))
            .addCode(
                CodeBlock.of(
                    """
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
        """.trimIndent()
                )
            )
            .addParameter(ParameterSpec("locale", NULLABLE_LOCALE))
        sofia.addFunction(get.build())
    }
}

fun String?.capitalizeFirstLetter(): String {
    return this?.let {
        this[0].uppercaseChar() + this.drop(1)
    } ?: ""
}
