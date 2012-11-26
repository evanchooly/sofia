package com.antwerkz.sofia;
/*
* Copyright 2001-2005 The Apache Software Foundation.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class SofiaMojo extends AbstractMojo {
  @Parameter(property = "project", defaultValue = "${project}", required = true, readonly = true)
  private MavenProject project;
  @Parameter(property = "sofia.target", defaultValue = "${project.build.directory}/generated-sources/sofia")
  private File outputDirectory;
  @Parameter(property = "sofia.inputFile", defaultValue = "src/main/resources/sofia.properties")
  private File inputFile;
  @Parameter(property = "sofia.package", defaultValue = "com.antwerkz.sofia")
  private String packageName;
  @Parameter(property = "sofia.play.logging", defaultValue = "false")
  private boolean playController;
  @Parameter(property="sofia.logging", defaultValue="slf4j")
  private String loggingType;
  @Parameter(property="sofia.js.dir", defaultValue="src/main/webapp/js")
  private File jsOutputDir;
  @Parameter(property="sofia.javascript", defaultValue="false")
  private boolean javascript;

  public void execute() throws MojoExecutionException {
    if (!inputFile.exists()) {
      throw new MojoExecutionException("Missing inputFile file: " + inputFile);
    }
    if (!outputDirectory.exists()) {
      outputDirectory.mkdirs();
    }
    try {
      new LocalizerGenerator(new SofiaConfig()
        .setBundleName(inputFile.getName())
        .setPackageName(packageName)
        .setProperties(inputFile)
        .setType(loadLoggingType())
        .setUseControl(playController)
        .setOutputDirectory(outputDirectory)
        .setGenerateJavascript(javascript)
        .setJavascriptOutputDirectory(jsOutputDir)
      ).write();
      project.addCompileSourceRoot(outputDirectory.getAbsolutePath());
    } catch (Exception e) {
      throw new MojoExecutionException(e.getMessage(), e);
    }
  }

  private LoggingType loadLoggingType() throws MojoExecutionException {
    try {
      return loggingType == null ? null : LoggingType.valueOf(loggingType.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new MojoExecutionException("Unknown logging type: " + loggingType);
    }
  }

}
