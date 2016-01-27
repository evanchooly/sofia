package com.antwerkz.sofia

import org.apache.maven.plugin.MojoExecutionException
import org.apache.maven.plugins.annotations.Mojo
import java.io.IOException
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds
import java.nio.file.WatchEvent

@Mojo(name = "watch")
class WatchMojo : SofiaMojo() {
    @Suppress("UNCHECKED_CAST")
    @Throws(MojoExecutionException::class)
    override fun execute() {
        try {
            val watcher = FileSystems.getDefault().newWatchService()
            val inputPath = inputFile.toPath()
            val dir = inputPath.getParent()
            dir.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY)
            log.info("Watching $inputPath for changes")
            generate()
            var valid = true
            while (valid) {
                val key = watcher.take()
                for (event in key.pollEvents()) {
                    if (event.kind() === StandardWatchEventKinds.OVERFLOW) {
                        continue
                    }
                    val ev = event as WatchEvent<Path>
                    if (dir.resolve(ev.context()).equals(inputPath)) {
                        generate()
                    }
                }
                valid = key.reset()
            }
        } catch (e: IOException) {
            throw MojoExecutionException(e.message, e)
        } catch (e: InterruptedException) {
            throw MojoExecutionException(e.message, e)
        }

    }
}
