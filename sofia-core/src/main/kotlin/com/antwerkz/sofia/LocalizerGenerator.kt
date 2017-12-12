package com.antwerkz.sofia

import freemarker.template.Configuration
import freemarker.template.Template
import java.io.File
import java.io.InputStreamReader
import java.io.StringWriter

class LocalizerGenerator(val config: SofiaConfig)  {

    fun write() {
        val packagePath = config.packageName.replace('.', '/')
        config.outputDirectory.mkdirs()
        if (config.generateJava) {
            generate(packagePath, "java")
        } else if (config.generateKotlin) {
            generate(packagePath, "kt")
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

    companion object {
        fun capitalize(text: String): String {
            return Character.toTitleCase(text[0]) + text.substring(1)
        }
    }
}