package com.antwerkz.sofia

import java.io.File
import java.util.Date
import java.util.Locale
import org.testng.Assert
import org.testng.annotations.Test

@Test
class KotlinSofiaTest {
    fun simple() {
        Assert.assertEquals(SofiaKotlin.testProperty(), "I'm the first test property")
        Assert.assertEquals(SofiaKotlin.lonely(), "I'm only in the default bundle.")
        Assert.assertEquals(SofiaKotlin.lonely(Locale.GERMAN), "I'm only in the default bundle.")

        Assert.assertEquals(SofiaKotlin.testProperty(Locale.CHINA), "I'm the first test property")
        Assert.assertEquals(
            SofiaKotlin.testProperty(Locale("en", "GB")),
            "I'm the first test property, bloke"
        )
        Assert.assertEquals(SofiaKotlin.testProperty(Locale.GERMAN), "I'm zee first test property")

        Assert.assertEquals(
            SofiaKotlin.parameterizedPropertyLongName("bob", "alice"),
            "I need parameters bob and alice"
        )
        Assert.assertEquals(
            SofiaKotlin.parameterizedPropertyLongName("bob", "alice", Locale.CHINA),
            "I need parameters bob and alice"
        )
        Assert.assertEquals(
            SofiaKotlin.parameterizedPropertyLongName("bob", "alice", Locale("en", "GB")),
            "I need two parameters bob and alice"
        )
        Assert.assertEquals(
            SofiaKotlin.parameterizedPropertyLongName("bob", "alice", Locale.GERMAN),
            "I need zwei parameters bob and alice"
        )
        val message = SofiaKotlin.dateProperty(Date(), 2)
        Assert.assertTrue(message.contains("Today's date"), "Could not find text in $message")
        SofiaKotlin.logAnother()
        SofiaKotlin.logAnother()
        SofiaKotlin.logAnother()
        SofiaKotlin.logAnother()
        SofiaKotlin.logAnother()
        SofiaKotlin.logMe()
        SofiaKotlin.logMe(Locale.GERMAN)
        val file = File("target/sofia.log")
        val s = file.readText()
        Assert.assertFalse(s.isEmpty())
        Assert.assertTrue(s.contains("I'm just a warning, though."))
        Assert.assertTrue(s.contains("Ich bin nur eine Warnung, wenn."))
        file.delete()

        Assert.assertEquals(
            SofiaKotlin.quoted(),
            """"To be or not to be.  That is the question," said an overly wrought Hamlet."""
        )
        Assert.assertEquals(
            SofiaKotlin.quoted2("stuff"),
            """But sometimes one needs to "quote" stuff in the middle, too."""
        )
        val split = s.split("\n")
        val count = split.count { line -> line.endsWith(" - I'm an error") }

        Assert.assertEquals(count, 1, "Should find the log message only once.")
    }

    fun inheritance() {
        Assert.assertNotEquals(
            SofiaKotlin.dateProperty(Date(), 42, Locale.UK),
            SofiaKotlin.dateProperty(Date(), 42)
        )
        Assert.assertEquals(
            SofiaKotlin.dateProperty(Date(), 42, Locale.UK),
            SofiaKotlin.dateProperty(Date(), 42, Locale.ENGLISH)
        )
    }
}
