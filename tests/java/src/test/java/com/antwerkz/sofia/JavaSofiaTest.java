package com.antwerkz.sofia;

import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.logging.LogManager;

@Test
public class JavaSofiaTest {
    public void simple() throws IOException {
        Assert.assertEquals(SofiaJava.testProperty(), "I'm the first test property");
        Assert.assertEquals(SofiaJava.lonely(), "I'm only in the default bundle.");
        Assert.assertEquals(SofiaJava.lonely(Locale.GERMAN), "I'm only in the default bundle.");

        Assert.assertEquals(SofiaJava.testProperty(Locale.CHINA), "I'm the first test property");
        Assert.assertEquals(SofiaJava.testProperty(new Locale("en", "GB")), "I'm the first test property, bloke");
        Assert.assertEquals(SofiaJava.testProperty(Locale.GERMAN), "I'm zee first test property");

        Assert.assertEquals(SofiaJava.parameterizedPropertyLongName("bob", "alice"), "I need parameters bob and alice");
        Assert.assertEquals(SofiaJava.parameterizedPropertyLongName("bob", "alice", Locale.CHINA),
                "I need parameters bob and alice");
        Assert.assertEquals(SofiaJava.parameterizedPropertyLongName("bob", "alice", new Locale("en", "GB")),
                "I need two parameters bob and alice");
        Assert.assertEquals(SofiaJava.parameterizedPropertyLongName("bob", "alice", Locale.GERMAN),
                "I need zwei parameters bob and alice");
        String message = SofiaJava.dateProperty(new Date(), 2);
        Assert.assertTrue(message.contains("Today's date"), message);
        SofiaJava.logAnother();
        SofiaJava.logAnother();
        SofiaJava.logAnother();
        SofiaJava.logAnother();
        SofiaJava.logMe();
        SofiaJava.logMe(Locale.GERMAN);
        final File file = new File("target/sofia.log");
        String s = FileUtils.readFileToString(file);
        file.delete();
        Assert.assertTrue(s.contains("I'm just a warning, though."));
        Assert.assertTrue(s.contains("Ich bin nur eine Warnung, wenn."));
        long count = Arrays.stream(s.split("\\n"))
                           .filter(line -> line.endsWith(" - I'm an error"))
                           .count();
        Assert.assertEquals(count, 1, "Should find the log message only once.");
    }

    public void inheritance()
    {
        Assert.assertNotEquals(SofiaJava.dateProperty(new  Date(), 42, Locale.UK), SofiaJava.dateProperty(new Date(), 42));
        Assert.assertEquals(SofiaJava.dateProperty(new Date(), 42, Locale.UK), SofiaJava.dateProperty(new Date(), 42, Locale.ENGLISH));
    }
}
