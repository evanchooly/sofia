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
import org.apache.maven.project.MavenProject;

/**
 * Goal which touches a timestamp file.
 *
 * @goal sofia
 * @phase generate-sources
 */
public class SofiaMojo extends AbstractMojo {
  /**
   * The default maven project object.
   *
   * @parameter expression="${project}"
   * @required
   * @readonly
   */
  private MavenProject project;
  /**
   * @parameter expression="${sofia.target}" default-value="${project.build.directory}/generated-sources/sofia"
   */
  private File outputDirectory;
  /**
   * @parameter expression="${sofia.inputFile}" default-value="src/main/resources/sofia.properties"
   * @required
   */
  private File inputFile;
  /**
   * @parameter expression="${sofia.package}" default-value="com.antwerkz.sofia"
   * @required
   */
  private String packageName;
  /**
   * @parameter expression="${sofia.play.logging}" default-value="false"
   * @required
   */
  private boolean playController;
  /**
   * @parameter expression="${sofia.logging}" default-value="slf4j"
   * @required
   */
  private String loggingType;

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
        .setOutputDirectory(outputDirectory)).write();
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
