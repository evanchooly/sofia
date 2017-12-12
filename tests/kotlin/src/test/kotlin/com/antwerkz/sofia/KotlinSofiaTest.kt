package com.antwerkz.sofia

import org.apache.commons.io.FileUtils
import org.testng.Assert
import org.testng.annotations.Test
import java.io.File
import java.io.FileInputStream
import java.util.Date
import java.util.Locale
import java.util.logging.LogManager

@Test
class KotlinSofiaTest {
    fun simple() {
        Assert.assertEquals(Sofia.testProperty(), "I'm the first test property")
        Assert.assertEquals(Sofia.lonely(), "I'm only in the default bundle.")
        Assert.assertEquals(Sofia.lonely(Locale.GERMAN), "I'm only in the default bundle.")

        Assert.assertEquals(Sofia.testProperty(Locale.CHINA), "I'm the first test property")
        Assert.assertEquals(Sofia.testProperty(Locale("en", "GB")), "I'm the first test property, bloke")
        Assert.assertEquals(Sofia.testProperty(Locale.GERMAN), "I'm zee first test property")

        Assert.assertEquals(Sofia.parameterizedPropertyLongName("bob", "alice"), "I need parameters bob and alice")
        Assert.assertEquals(Sofia.parameterizedPropertyLongName("bob", "alice", Locale.CHINA),
                "I need parameters bob and alice")
        Assert.assertEquals(Sofia.parameterizedPropertyLongName("bob", "alice", Locale("en", "GB")),
                "I need two parameters bob and alice")
        Assert.assertEquals(Sofia.parameterizedPropertyLongName("bob", "alice", Locale.GERMAN),
                "I need zwei parameters bob and alice")
        val message = Sofia.dateProperty(Date(), 2)
        Assert.assertTrue(message.contains("Today's date"), message)
        LogManager.getLogManager().readConfiguration(FileInputStream("src/test/resources/logging.properties"))
        Sofia.logMe()
        Sofia.logMe(Locale.GERMAN)
        val file = File("/tmp/sofia.log")
        val s = FileUtils.readFileToString(file)
        Assert.assertTrue(s.contains("I'm just a warning, though."))
        Assert.assertTrue(s.contains("Ich bin nur eine Warnung, wenn."))
        file.delete()
    }

    fun inheritance() {
        Assert.assertNotEquals(Sofia.dateProperty(Date(), 42, Locale.UK), Sofia.dateProperty(Date(), 42))
        Assert.assertEquals(Sofia.dateProperty(Date(), 42, Locale.UK), Sofia.dateProperty(Date(), 42, Locale.ENGLISH))
    }
}
