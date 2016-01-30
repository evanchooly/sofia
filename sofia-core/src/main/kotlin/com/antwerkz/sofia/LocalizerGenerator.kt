package com.antwerkz.sofia

import freemarker.template.Configuration
import freemarker.template.Template
import freemarker.template.TemplateException
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.io.StringWriter

class LocalizerGenerator(val config: SofiaConfig) {

    fun write() {
        val packagePath = config.packageName.replace('.', '/')
        config.outputDirectory.mkdirs()
        if (config.generateJava) {
            generate(packagePath, "java", { generateJava() })
        }
        if(config.generateKotlin) {
            generate(packagePath, "kt", { generateKotlin() })
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

    private fun generate(packagePath: String, extension: String, generate: () -> String) {
        var file = File(config.outputDirectory, "${packagePath}/${config.className.capitalize()}.${extension}")
        file.parentFile.mkdirs()
        try {
            PrintWriter(file, "UTF-8").use { stream ->
                Template("sofia", InputStreamReader(javaClass.getResourceAsStream("/sofia-${extension}.ftl")), Configuration())
                        .process(config, stream)
                stream.flush()
            }
        } catch (e: Exception) {
            throw RuntimeException(e.message, e)
        }
    }

    fun generateJava(): String {
        val cfg = Configuration()
        val out = StringWriter()
        Template("sofia", InputStreamReader(javaClass.getResourceAsStream("/sofia-java.ftl")), cfg)
                .process(config, out)
        out.flush()
        return out.toString()
    }

    fun generateKotlin(): String {
        val cfg = Configuration()
        val out = StringWriter()
        Template("sofia", InputStreamReader(javaClass.getResourceAsStream("/sofia-kt.ftl")), cfg)
                .process(config, out)
        out.flush()
        return out.toString()
    }

    fun generateJavascript(): String {
        val cfg = Configuration()
        try {
            val template = Template("sofia",
                    InputStreamReader(javaClass.getResourceAsStream("/sofia-js.ftl")), cfg)
            val out = StringWriter()
            template.process(config, out)
            out.flush()
            return out.toString()
        } catch (e: IOException) {
            throw RuntimeException(e.message, e)
        } catch (e: TemplateException) {
            throw RuntimeException(e.message, e)
        }

    }

    companion object {

        fun capitalize(text: String): String {
            return Character.toTitleCase(text[0]) + text.substring(1)
        }
    }
}