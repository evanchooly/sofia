package com.antwerkz.sofia;

import java.io.IOException;
import java.util.Locale;

import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class SofiaTest {
    public void simple() throws IOException {
        Assert.assertEquals(Localizer.testProperty(), "I'm the first test property");
        Assert.assertEquals(Localizer.testProperty(Locale.CHINA), "I'm the first test property");
        Assert.assertEquals(Localizer.testProperty(new Locale("en", "GB")), "I'm the first test property, bloke");
        Assert.assertEquals(Localizer.testProperty(Locale.GERMAN), "I'm zee first test property");

        Assert.assertEquals(Localizer.parameterizedPropertyLongName("bob", "alice"), "I need parameters bob and alice");
        Assert.assertEquals(Localizer.parameterizedPropertyLongName("bob", "alice", Locale.CHINA), "I need parameters bob and alice");
        Assert.assertEquals(Localizer.parameterizedPropertyLongName("bob", "alice", new Locale("en", "GB")), "I need two parameters bob and alice");
        Assert.assertEquals(Localizer.parameterizedPropertyLongName("bob", "alice", Locale.GERMAN), "I need zwei parameters bob and alice");
    }
}
