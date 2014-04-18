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
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class SofiaMojo extends AbstractMojo {
  @Parameter(property = "project")
  private MavenProject project;
  @Parameter(property = "outputDirectory", defaultValue = "${project.build.directory}/generated-sources/sofia")
  private File outputDirectory;
  @Parameter(defaultValue = "src/main/resources/sofia.properties")
  private File inputFile;
  @Parameter(defaultValue = "com.antwerkz.sofia")
  private String packageName;
  @Parameter(defaultValue = "false")
  private boolean playController;
  @Parameter(defaultValue = "jul")
  private String loggingType;
  @Parameter(defaultValue = "src/main/webapp/js/sofia.js")
  private File jsOutputFile;
  @Parameter(defaultValue = "false")
  private boolean javascript;
  @Parameter(defaultValue = "ISO-8859-1")
  private String charset;

  public void execute() throws MojoExecutionException {
    if (!outputDirectory.exists()) {
      outputDirectory.mkdirs();
    }
    try {
      generate();
      project.addCompileSourceRoot(outputDirectory.getAbsolutePath());
    } catch (Exception e) {
      throw new MojoExecutionException(e.getMessage(), e);
    }
  }

  public void generate() throws IOException, MojoExecutionException {
    if (!inputFile.exists()) {
      throw new MojoExecutionException("Missing inputFile file: " + inputFile);
    }
    new LocalizerGenerator(new SofiaConfig()
      .setBundleName(inputFile.getName())
      .setPackageName(packageName)
      .setProperties(inputFile)
      .setType(loadLoggingType())
      .setUseControl(playController)
      .setOutputDirectory(outputDirectory)
      .setGenerateJavascript(javascript)
      .setJavascriptOutputFile(jsOutputFile)
      .setCharset(Charset.forName(charset))
    ).write();
  }

  private LoggingType loadLoggingType() throws MojoExecutionException {
    try {
      return loggingType == null ? null : LoggingType.valueOf(loggingType.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new MojoExecutionException("Unknown logging type: " + loggingType);
    }
  }

  public File getInputFile() {
    return inputFile;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("SofiaMojo{");
    sb.append("outputDirectory=").append(outputDirectory);
    sb.append(", inputFile=").append(inputFile);
    sb.append(", packageName='").append(packageName).append('\'');
    sb.append(", playController=").append(playController);
    sb.append(", loggingType='").append(loggingType).append('\'');
    sb.append(", jsOutputFile=").append(jsOutputFile);
    sb.append(", javascript=").append(javascript);
    sb.append(", charset='").append(charset).append('\'');
    sb.append('}');
    return sb.toString();
  }
}
