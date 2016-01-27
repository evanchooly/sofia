package com.antwerkz.sofia

import freemarker.template.Configuration
import freemarker.template.Template
import freemarker.template.TemplateException
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.io.StringWriter
import java.util.HashMap

class LocalizerGenerator(val config: SofiaConfig) {

    fun generateJava(): String {
        val cfg = Configuration()
        val template = Template("sofia", InputStreamReader(javaClass.getResourceAsStream("/sofia.ftl")), cfg)
        val out = StringWriter()
        template.process(config, out)
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

    fun write() {
        var file = File(config.outputDirectory, "%s/%s.java".format(config.packageName.replace('.', '/'), capitalize(config.className)))
        file.parentFile.mkdirs()
        try {
            PrintWriter(file, "UTF-8").use { stream ->
                stream.println(this.generateJava())
                stream.flush()
            }
        } catch (e: Exception) {
            throw RuntimeException(e.message, e)
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

    companion object {

        fun capitalize(text: String): String {
            return Character.toTitleCase(text[0]) + text.substring(1)
        }
    }
}