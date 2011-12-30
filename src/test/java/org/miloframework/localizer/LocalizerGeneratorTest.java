package org.miloframework.localizer;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class LocalizerGeneratorTest {
    public void simpleClass() {
        Map<String, String> map = new TreeMap<String, String>();
        map.put("test.property", "I'm the first test property");
        map.put("test.property.long.name", "I need parameters {} and {}");

        LocalizerGenerator generator = new LocalizerGenerator("org.testing", map);
        final List<Method> methods = generator.getMethods();
        Assert.assertEquals(methods.size(), 2, "Should have 2 methods");
        final Method method = methods.get(0);

        Assert.assertEquals("public String testProperty()", method.getSignature());
        final String classDef = generator.toString();
        System.out.println(classDef);
    }
}
