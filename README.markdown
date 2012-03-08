Sofia
=====

Sofia is a typesafe (ish) layer on top of a properties file.  Given a properties file, it will generate a java class
providing compile time checks that you're using values that actually exist in your properties file.  It supports multiple
locales if you provide them.

A simple example looks like this.  For this properties file:

    test.property=I'm the first test property
    parameterized.property.long.name=I need parameters {0} and {1}
    new.property=New Property
    date.property=Today''s date {0,date,full} and now a number {1,number}

a java file will be generated with the following interface:

    public class Localizer {
        public static String dateProperty(java.util.Date arg0, Number arg1, Locale... locale) { }
        public static String newProperty(Locale... locale) { }
        public static String parameterizedPropertyLongName(Object arg0, Object arg1, Locale... locale) { }
        public static String testProperty(Locale... locale) { }
    }

There are some other details but they're mostly irrelevant.  Feel free to explore the generated file if you're
*really* interested.  What you see here is one method for each property listed in your properties file.  Those properties
that take parameters result in methods that take parameters as well.  Notice that each parameter is typed according to
the formatting specified in the properties file.  If no formatting is defined, "Object" is used for that parameter type.

Also note that each method takes a Locale... parameter.  This allows sofia to generate one method per property while
supporting an optional Locale to be supplied without having to generate two identical methods or having to pass in a null
to a "singleton" parameter.  Multiple Locales can be passed in but only one will be used.

So ... why?
-----------

When I worked on [glassfish](http://glassfish.java.net), we'd standardized on a library that did something similar. My
biggest complaint about that one is that all the generated methods WERE IN ALL CAPS WHICH MADE READING THE CODE AWKWARD.
Also, I'm not really sure where the source that library is so patching it would be tricky.  In any case, it turned out
to be a very handy facet to a project which made have use of properties files to manage its messages so I wanted to
improve on it where I could.

OK.  So what's next?
--------------------

There are a handful of things I'd like to add:

1.  Build time validation of properties files:  Making sure that all the properties files variants have the same key set.
1.  Optional autogeneration of error code values for those systems that need numeric codes assigned to each message.
Oracle had such requirements for its error messages and manually was troublesome.  Of course, generating them might
prove just as problematic, but we'll see how it goes.
1.  Play framework support.  I'd love for this to work in [play!](http://playframework.org) as well as I've been using that
more and more lately.

Great.  How do I use it?
-----

Add the following to your pom.xml:

    <plugin>
      <groupId>com.antwerkz.sofia</groupId>
      <artifactId>maven</artifactId>
      <version> *** sofia version *** </version>
      <executions>
        <execution>
          <goals>
            <goal>generate</goal>
          </goals>
        </execution>
      </executions>
    </plugin>

There are a few configuration options:

<table>
    <tr><td>Value</td><td>Description</td><td>Default</td></tr>
    <tr><td>sofia.package</td><td>Defines the package for the generated class</td><td>com.antwerkz.sofia</td></tr>
    <tr><td>sofia.properties</td><td>The base properties file to use</td><td>src/main/resources/sofia.properties</td></tr>
    <tr><td>sofia.target</td><td>Generated source location</td><td>${project.build.directory}/generated-sources/sofia</td></tr>
</table>