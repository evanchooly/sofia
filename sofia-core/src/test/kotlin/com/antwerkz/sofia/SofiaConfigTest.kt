package com.antwerkz.sofia

import org.testng.Assert
import org.testng.annotations.Test
import java.io.File

@Test
class SofiaConfigTest {
    fun parentBundles() {
        val properties = File("../tests/src/main/resources/sofia.properties")
        val sofiaConfig = SofiaConfig(properties, packageName = "utils", loggingType = LoggingType.SLF4J,
                outputDirectory = File("target/testProperties/"))

        Assert.assertEquals(sofiaConfig.findParentExtension("en_GB"), "_en")
        Assert.assertEquals(sofiaConfig.findParentExtension("en"), "")

    }

    fun testProperties() {
        val properties = File("../tests/src/main/resources/sofia.properties")
        val sofiaConfig = SofiaConfig(properties, loggingType = LoggingType.SLF4J, generateJava = false,
                generateKotlin = true, outputDirectory = File("../tests/target/generated-sources/sofia"))

        LocalizerGenerator(sofiaConfig).write()
        val readFile = File("../tests/target/generated-sources/sofia/com/antwerkz/sofia/Sofia.kt").readText()
        Assert.assertTrue(readFile.contains("fun dateProperty(arg0: java.util.Date, arg1: Number, vararg locale: Locale): String"))
    }
}
