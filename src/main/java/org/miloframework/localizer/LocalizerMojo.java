package org.miloframework.localizer;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Goal which touches a timestamp file.
 *
 * @goal localize
 * @phase generate-sources
 */
public class LocalizerMojo extends AbstractMojo {
    /**
     * @parameter expression="${localizer.target}" default-value="${project.build.directory}/generated-sources/localizer"
     */
    private File outputDirectory;
    /**
     * @parameter expression="${localizer.properties}" default-value="src/main/resources/localizer.properties"
     * @required
     */
    private File properties;
    /**
     * @parameter expression="${localizer.package}" default-value="org.miloframework.localizer"
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
        Properties props = new Properties();
        Map<String, String> map = new TreeMap<String, String>();
        try {
            props.load(new FileInputStream(properties));
            for (Entry<Object, Object> entry : props.entrySet()) {
                map.put((String) entry.getKey(), (String) entry.getValue());
            }
            emitClass(map);
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    private void emitClass(Map<String, String> map)
        throws FileNotFoundException, UnsupportedEncodingException {
        LocalizerGenerator localizer = new LocalizerGenerator(pkgName, map);
        File outputFile = new File(outputDirectory,
            String.format("%s/Localizer.java", pkgName.replace('.', '/')));
        outputFile.getParentFile().mkdirs();
        final PrintWriter stream = new PrintWriter(outputFile, "UTF-8");
        stream.println(localizer);
        stream.flush();
        stream.close();
    }

}
