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
        val sofiaConfig = SofiaConfig(properties, packageName = "utils", loggingType = LoggingType.SLF4J,
                outputDirectory = File("target/testProperties/"))

        LocalizerGenerator(sofiaConfig).write()
        val readFile = File("target/testProperties/utils/Sofia.java").readText()
        Assert.assertTrue(readFile.contains("default String testProperty"))
    }
}
