package com.antwerkz.sofia.play;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;

import com.antwerkz.sofia.LocalizerGenerator;
import com.antwerkz.sofia.SofiaConfig;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public class SofiaPlugin {
  private File targetDir = new File("app");
  private boolean running = true;
  private WatchService watchService;
  private Path messages = new File("messages").toPath();

  public void start() throws IOException {
    watchService = FileSystems.getDefault().newWatchService();
    new File("conf").toPath().register(watchService, ENTRY_MODIFY);
    generate();
    new Thread(new Runnable() {
      @Override
      public void run() {
        while (running) {
          WatchKey key;
          if ((key = watchService.poll()) != null) {
            List<WatchEvent<?>> watchEvents = key.pollEvents();
            for (WatchEvent<?> event : watchEvents) {
              WatchEvent<Path> ev = cast(event);
              if(ev.context().equals(messages)) {
                generate();
                key.reset();
              }
            }
          } else {
            try {
              Thread.sleep(100);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          }
        }
      }
    }).start();
  }

  @SuppressWarnings("unchecked")
  static <T> WatchEvent<T> cast(WatchEvent<?> event) {
    return (WatchEvent<T>) event;
  }

  public void stop() {
    running = false;
  }

  private void generate() {
    try {
      new LocalizerGenerator(new SofiaConfig()
        .setPackageName("utils")
        .setBundleName("messages")
        .setClassName("Sofia")
        .setProperties(findProperties())
        .setOutputDirectory(targetDir)
        .setUseControl(true))
        .write();
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  private InputStream  findProperties() throws FileNotFoundException {
    InputStream stream = getClass().getClassLoader().getResourceAsStream("/messages");
    if (stream == null) {
      stream = new FileInputStream("conf/messages");
    }
    return stream;
  }

  public static void main(String[] args) {
    new SofiaPlugin().generate();
  }
}