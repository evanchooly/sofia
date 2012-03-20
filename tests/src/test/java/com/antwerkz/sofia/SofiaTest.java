package com.antwerkz.sofia;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;
import java.util.logging.LogManager;

import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class SofiaTest {
    public void simple() throws IOException {
        LogManager.getLogManager().readConfiguration(getClass().getResourceAsStream("/logging.properties"));

        Assert.assertEquals(Sofia.testProperty(), "I'm the first test property");

        Assert.assertEquals(Sofia.lonely(), "I'm only in the default bundle.");
        Assert.assertEquals(Sofia.lonely(Locale.GERMAN), "I'm only in the default bundle.");

        Assert.assertEquals(Sofia.testProperty(Locale.CHINA), "I'm the first test property");
        Assert.assertEquals(Sofia.testProperty(new Locale("en", "GB")), "I'm the first test property, bloke");
        Assert.assertEquals(Sofia.testProperty(Locale.GERMAN), "I'm zee first test property");

        Assert.assertEquals(Sofia.parameterizedPropertyLongName("bob", "alice"), "I need parameters bob and alice");
        Assert.assertEquals(Sofia.parameterizedPropertyLongName("bob", "alice", Locale.CHINA), "I need parameters bob and alice");
        Assert.assertEquals(Sofia.parameterizedPropertyLongName("bob", "alice", new Locale("en", "GB")), "I need two parameters bob and alice");
        Assert.assertEquals(Sofia.parameterizedPropertyLongName("bob", "alice", Locale.GERMAN), "I need zwei parameters bob and alice");

        String message = Sofia.dateProperty(new Date(),  2);
        Assert.assertFalse(message.contains("{"), message);

        Sofia.logMe();
        String s = FileUtils.readFileToString(new File("/tmp/sofia.log"));
        Assert.assertTrue(s.contains("I'm just a warning, though."));
    }
}
