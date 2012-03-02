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
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

/**
 * Goal which touches a timestamp file.
 *
 * @goal generate
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
     * @parameter expression="${localizer.target}" default-value="${project.build.directory}/generated-sources/sofia"
     */
    private File outputDirectory;
    /**
     * @parameter expression="${localizer.properties}" default-value="src/main/resources/sofia.properties"
     * @required
     */
    private File properties;
    /**
     * @parameter expression="${localizer.package}" default-value="com.antwerkz.sofia"
     * @required
     */
    private String pkgName;

    public void execute() throws MojoExecutionException {
        if (!properties.exists()) {
            throw new MojoExecutionException("Missing input file: " + properties);
        }
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }
        try {
            emitClass();
            project .addCompileSourceRoot(outputDirectory.getAbsolutePath());
        } catch (Exception e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    private void emitClass() throws IOException, UnsupportedEncodingException {
        LocalizerGenerator localizer = new LocalizerGenerator(pkgName, properties);
        File outputFile = new File(outputDirectory,
            String.format("%s/Localizer.java", pkgName.replace('.', '/')));
        outputFile.getParentFile().mkdirs();
        final PrintWriter stream = new PrintWriter(outputFile, "UTF-8");
        stream.println(localizer);
        stream.flush();
        stream.close();
    }

}
