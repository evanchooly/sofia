package com.antwerkz.sofia

import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugin.MojoExecutionException
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.project.MavenProject
import java.io.File
import java.nio.charset.Charset

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
    @Parameter(defaultValue = "jul")
    lateinit var loggingType: String
    @Parameter(defaultValue = "true")
    var generateJava: Boolean = true
    @Parameter(defaultValue = "true")
    var generateKotlin: Boolean = false
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
                generateJava = this.generateJava, generateKotlin = this.generateKotlin, className = this.className,
                charset = Charset.forName(charset))
        LocalizerGenerator(config).write()
    }

    private fun loadLoggingType(): LoggingType {
        try {
            return LoggingType.valueOf(loggingType.toUpperCase())
        } catch (e: IllegalArgumentException) {
            throw MojoExecutionException("Unknown logging type: " + loggingType)
        }

    }

    override fun toString(): String {
        val sb = StringBuilder("SofiaMojo{")
        sb.append("outputDirectory=").append(outputDirectory)
        sb.append(", inputFile=").append(inputFile)
        sb.append(", packageName='").append(packageName).append('\'')
        sb.append(", loggingType='").append(loggingType).append('\'')
//        sb.append(", jsOutputFile=").append(jsOutputFile)
//        sb.append(", javascript=").append(javascript)
        sb.append(", charset='").append(charset).append('\'')
        sb.append('}')
        return sb.toString()
    }
}
