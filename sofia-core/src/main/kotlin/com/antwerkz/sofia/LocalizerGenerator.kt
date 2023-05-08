package com.antwerkz.sofia

import com.antwerkz.sofia.LoggingType.NONE
import com.antwerkz.sofia.LoggingType.SLF4J
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ClassName.Companion.bestGuess
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier.OPEN
import com.squareup.kotlinpoet.KModifier.OVERRIDE
import com.squareup.kotlinpoet.KModifier.PRIVATE
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeSpec.Builder
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
                    .addModifiers(PRIVATE)
                    .mutable(false)
                    .initializer(CodeBlock.of("""%T.getLogger("${config.packageName}.${config.className}")""", config.loggingType.factory))
                    .build()
            )
            sofia.addProperty(
                PropertySpec.builder("loggedMessages", bestGuess("kotlin.collections.MutableSet")
                    .parameterizedBy(String::class.asClassName()))
                    .addModifiers(PRIVATE)
                    .mutable(false)
                    .initializer("mutableSetOf()")
                    .build()
            )
        }
        sofia.addProperty(
            PropertySpec.builder("IMPLS", bestGuess("kotlin.collections.MutableMap")
                .parameterizedBy(String::class.asClassName(), bestGuess("Localized")))
                .addModifiers(PRIVATE)
                .mutable(false)
                .initializer("mutableMapOf()")
                .build())

        sofia.addInitializerBlock(CodeBlock.of(
            "IMPLS.put(\"\", LocalizedImpl())\n" +
            config.bundles.keys
                .joinToString("\n", postfix = "\n") {
                "IMPLS.put(\"$it\", LocalizedImpl_$it())"
            }))

        get(sofia)
        val localized = TypeSpec.interfaceBuilder("Localized")
            .addModifiers(PRIVATE)
        config.methods.forEach {
            addMethods(sofia, it)
            addLocalizer(localized, it)
        }
        sofia.addType(localized.build())

        addLocaleSpecificImpls(sofia)

        FileSpec.builder(className)
            .addType(sofia.build())
            .build()
            .writeTo(config.outputDirectory)
    }

    private fun addLocaleSpecificImpls(sofia: Builder) {
        sofia.addType(classBuilder("LocalizedImpl").apply {
            this.modifiers += listOf(OPEN, PRIVATE)
            addSuperinterface(bestGuess("Localized"))
        }.build())
        addLocalizerImpls(sofia)
    }

    private fun addLocalizer(localized: Builder, method: Method, isOverride: Boolean = false) {
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
                CodeBlock.of("""return %T.format(%S, ${arguments})""", MessageFormat::class, method.value )
            } else {
                CodeBlock.of("return %S", method.value)
            }
        )

        localized.addFunction(function.build())
    }

    private fun addLocalizerImpls(sofia: Builder) {
        config.bundles.forEach { bundle, methods ->
            val impl = classBuilder("LocalizedImpl_$bundle")
                .apply {
                    modifiers += listOf(OPEN, PRIVATE)
                    superclass(bestGuess("LocalizedImpl${config.findParentExtension(bundle)}"))
                }
            methods.forEach {
                addLocalizer(impl, it, true)
            }
            sofia.addType(impl.build())
        }
    }

    private fun addMethods(sofia: Builder, method: Method, isOverride: Boolean = false) {
        val function = FunSpec.builder(method.methodName)
            .returns(String::class)

        function.addKdoc("Generated from ${method.key}")
        if(isOverride) {
            function.modifiers += OPEN
        }
        method.parameters.forEach { param ->
            function.addParameter(ParameterSpec(param.second, className(param)))
        }
        val builder = ParameterSpec.builder("locale", NULLABLE_LOCALE)
            .addModifiers()
        if (!isOverride) {
            builder.defaultValue("null")
        }
        function.addParameter(builder.build())

        function.addCode("return get(locale).${method.methodName}(${method.arguments.joinToString(", ")})")

        if (!isOverride && method.logged) {
            val loggerName = method.loggerName
            val logged = FunSpec.builder(loggerName)
            method.parameters.forEach { param ->
                logged.addParameter(ParameterSpec(param.second, className(param)))
            }
            logged.addParameter(ParameterSpec.builder("locale", NULLABLE_LOCALE)
                .defaultValue("null")
                .build())

            logged.beginControlFlow(when (config.loggingType) {
                SLF4J -> "if(logger.is${method.logLevel!!.capitalizeFirstLetter()}Enabled())"
                else -> "if(logger.isLoggable(Level.${method.logLevel?.uppercase(Locale.getDefault())}))"
            })
            if (method.logOnce) {
                logged.beginControlFlow("if(loggedMessages.add(\"${loggerName}\"))")
            }
            logged.addStatement("return logger.${method.logLevel}(${method.methodName}(${(method.arguments + "locale").joinToString(", ")}))")
            if (method.logOnce) {
                logged.endControlFlow()
            }
            logged.endControlFlow()


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

    private fun get(sofia: Builder) {
        val get = FunSpec.builder("get")
            .addModifiers(PRIVATE)
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
