package com.antwerkz.sofia

import java.io.File
import org.testng.Assert
import org.testng.annotations.Test

@Test
class SofiaConfigTest {
    fun parentBundles() {
        val properties = File("../tests/kotlin/src/main/resources/sofia.properties")
        val sofiaConfig =
            SofiaConfig(
                properties,
                packageName = "utils",
                loggingType = LoggingType.SLF4J,
                outputDirectory = File("target/testProperties/")
            )

        Assert.assertEquals(sofiaConfig.findParentExtension("en_GB"), "_en")
        Assert.assertEquals(sofiaConfig.findParentExtension("en"), "")
    }

    fun testKotlin() {
        val properties = File("../tests/kotlin/src/main/resources/sofia.properties")
        val sofiaConfig =
            SofiaConfig(
                properties,
                loggingType = LoggingType.SLF4J,
                generateJava = false,
                generateKotlin = true,
                outputDirectory = File("target/generated-test-sources/sofia")
            )

        LocalizerGenerator(sofiaConfig).write()
        val readFile =
            File("target/generated-test-sources/sofia/com/antwerkz/sofia/Sofia.kt").readText()
        Assert.assertTrue(
            readFile.contains(
                """
  public fun dateProperty(
    arg0: Date,
    arg1: Number,
    locale: Locale? = null,
  ): String"""
            )
        )
    }

    fun testJava() {
        val properties = File("../tests/kotlin/src/main/resources/sofia.properties")
        val sofiaConfig =
            SofiaConfig(
                properties,
                loggingType = LoggingType.SLF4J,
                className = "SofiaJava",
                outputDirectory = File("../tests/java/target/generated-test-sources/sofia")
            )

        Assert.assertEquals(sofiaConfig.className, "SofiaJava")
        LocalizerGenerator(sofiaConfig).write()
        val readFile =
            File(
                    "../tests/java/target/generated-test-sources/sofia/com/antwerkz/sofia/SofiaJava.java"
                )
                .readText()
        Assert.assertTrue(readFile.contains("public String dateProperty(Date arg0"))
    }
}
