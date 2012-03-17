package com.antwerkz.sofia.play;

import java.io.File;
import java.io.IOException;

import com.antwerkz.sofia.LocalizerGenerator;
import play.PlayPlugin;

public class SofiaPlugin extends PlayPlugin {
    private File properties = new File("conf/sofia.properties");
    private File targetDir = new File("app");
    private File targetFile = new File(targetDir, "utils/Localizer.java");

    @Override
    public void onApplicationStart() {
        generate();
    }

    @Override
    public void detectChange() {
        generate();
    }

    private void generate() {
        Long targetMod = targetFile.lastModified();
        Long propMod = properties.lastModified();
        boolean less = targetMod < propMod;
        System.out.println("targetMod = " + targetMod);
        System.out.println("propMod = " + propMod);
        System.out.println("less = " + less);

        if (less) {
            try {
                new LocalizerGenerator("utils", properties, targetDir).write();
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }
}