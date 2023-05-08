package com.antwerkz.sofia

import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugin.MojoExecutionException
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.project.MavenProject
import java.io.File
import java.nio.charset.Charset
import java.util.Locale

@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
open class SofiaMojo : AbstractMojo() {

    @Parameter(property = "project")
    lateinit var project: MavenProject
    @Parameter(property = "outputDirectory", defaultValue = "\${project.build.directory}/generated-sources/sofia")
    lateinit var outputDirectory: File
    @Parameter(defaultValue = "src/main/resources/sofia.properties")
    lateinit var inputFile: File
    @Parameter(defaultValue = "com.antwerkz.sofia")
    lateinit var packageName: String
    @Parameter
    var className: String? = null
    @Parameter(defaultValue = "slf4j")
    lateinit var loggingType: String
    @Parameter(defaultValue = "java")
    lateinit var outputType: String
//    @Parameter(defaultValue = "src/main/webapp/js/sofia.js")
//    lateinit var jsOutputFile: File
//    @Parameter(defaultValue = "false")
//    var javascript: Boolean = false
    @Parameter(defaultValue = "ISO-8859-1")
    lateinit var charset: String

    override fun execute() {
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs()
        }
        try {
            generate()
            project.addCompileSourceRoot(outputDirectory.absolutePath)
        } catch (e: Exception) {
            throw MojoExecutionException(e.message, e)
        }
    }

    fun generate() {
        if (!inputFile.exists()) {
            throw MojoExecutionException("Missing inputFile file: " + inputFile)
        }
        val config = SofiaConfig(inputFile, outputDirectory, loadLoggingType(), packageName,
                generateJava = this.outputType == "java", generateKotlin = this.outputType == "kotlin",
                className = this.className ?: inputFile.nameWithoutExtension.capitalizeFirstLetter(), charset = Charset.forName(charset))
        LocalizerGenerator(config).write()
    }

    private fun loadLoggingType(): LoggingType {
        try {
            return LoggingType.valueOf(loggingType.uppercase(Locale.getDefault()))
        } catch (e: IllegalArgumentException) {
            throw MojoExecutionException("Unknown logging type: " + loggingType)
        }

    }

    override fun toString(): String {
        return "SofiaMojo(outputDirectory=$outputDirectory, inputFile=$inputFile, packageName='$packageName'," +
                " className=$className, loggingType='$loggingType', outputType='$outputType', charset='$charset')"
    }
}
