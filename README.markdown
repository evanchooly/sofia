Sofia
=====

Sofia is a typesafe (ish) layer on top of a properties file.  Given a properties file, it will generate a java class
providing compile time checks that you're using values that actually exist in your properties file.  It supports multiple
locales if you provide them.

A simple example looks like this.  For this properties file:

    test.property=I'm the first test property
    parameterized.property.long.name=I need parameters {0} and {1}
    new.property=New Property

    @error.date.property=Today''s date {0,date,full} and now a number {1,number}

    @error.another=I'm an error
    @warn.me=I'm just a warning, though.

    lonely=I'm only in the default bundle.

a java file will be generated with the following interface:

    public class com.antwerkz.sofia.Sofia {
      public static java.lang.String another(java.util.Locale...);
      public static void logAnother(java.util.Locale...);
      public static java.lang.String dateProperty(java.util.Date, java.lang.Number, java.util.Locale...);
      public static void logDateProperty(java.util.Date, java.lang.Number, java.util.Locale...);
      public static java.lang.String me(java.util.Locale...);
      public static void logMe(java.util.Locale...);
      public static java.lang.String lonely(java.util.Locale...);
      public static java.lang.String newProperty(java.util.Locale...);
      public static java.lang.String parameterizedPropertyLongName(java.lang.Object, java.lang.Object, java.util.Locale...);
      public static java.lang.String testProperty(java.util.Locale...);
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
Also, I'm not really sure where the source for that library is so patching it would be tricky.  In any case, it turned out
to be a very handy facet to a project which makes use of properties files to manage its messages so I wanted to
improve on it where I could.

Great.  How do I use it?
-----

Add the following to your pom.xml:

    <plugin>
      <groupId>com.antwerkz.sofia</groupId>
      <artifactId>maven</artifactId>
      <version>0.12</version>
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
    <col width="33%" />
    <col width="33%" />
    <col width="33%" />
    <tbody>
        <tr><td>Value</td><td>Description</td><td>Default</td></tr>
        <tr><td>packageName</td><td>Defines the package for the generated class</td><td>com.antwerkz.sofia</td></tr>
        <tr><td>inputFile</td><td>The base properties file to use</td><td>src/main/resources/sofia.properties</td></tr>
        <tr><td>outputDirectory</td><td>Generated source location</td><td>${project.build.directory}/generated-sources/sofia</td></tr>
        <tr><td>loggingType</td><td>Use a specific logger</td><td></td></tr>
        <tr><td>playController</td><td>Generate code for use in play applications (supported: none, jul, slf4j)</td><td>slf4j</td>
        </tr>
    </tbody>
</table>

If you'd like continuous monitoring and regeneration of your files, there's also "sofia:watch."  Using this target,
sofia will watch for changes to your input file and regenerate as needed.

### For use in Play apps

Add the this line to your Build.scala file in your appDependencies variable

    "com.antwerkz.sofia" % "sofia-play" % "0.12"

If you've just checked out a new project, you might need to pregenerate your file before the play app
will start.  The easiest way, for now, is if the project has provided a pom.xml and configured Sofia
with something like this:

    <plugin>
        <groupId>org.codehaus.mojo</groupId>
        <artifactId>exec-maven-plugin</artifactId>

        <configuration>
            <mainClass>com.antwerkz.sofia.play.SofiaPlugin</mainClass>
        </configuration>
    </plugin>

Ideally, Sofia would provide an sbt plugin to make this happen.  I don't know how to write those yet so
that will just have to wait until I do or someone provides a patch.</hint>  It would be easiest for all
to simply commit your generated file to whichever version control system you use.

OK.  So what's next?
--------------------

There are a handful of things I'd like to add:

1.  Build time validation of properties files:  Making sure that all the properties files variants have the same key set.
1.  Optional autogeneration of error code values for those systems that need numeric codes assigned to each message.
Oracle had such requirements for its error messages and manually managing that was troublesome.  Of course, generating
them might prove just as problematic, but we'll see how it goes.