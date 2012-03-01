package org.miloframework.sofia;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class LocalizerGeneratorTest {
    public void simpleClass() throws IOException {
        LocalizerGenerator generator = new LocalizerGenerator("org.testing",
            new File("src/test/resources/localizer.properties"));
        final String classDef = generator.toString();
        System.out.println(classDef);

        final List<Method> methods = generator.getMethods();
        Assert.assertEquals(methods.size(), 2, "Should have 2 methods");

        Assert.assertTrue(classDef.contains("public static String testProperty(Locale... locale)"));
    }
}
