package net.mmm.survival.util.logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * Import from abgie.tool.util.logging.LogManager
 *
 * @author Abgie on 03.10.2018 17:00
 * project SurvivalProjekt
 * @version 1.0
 * @since JDK 8
 */
final class LogManager {
  private final File file;
  private StringBuilder log;

  LogManager(final File file) {
    this.file = file;
    this.log = new StringBuilder();

  }

  void log(final String str) {
    this.log.append("\n").append(str);
  }

  void exit() {
    try (final Writer writer = new FileWriter(file, true)) {
      writer.write(log.toString());
      log = new StringBuilder();
    } catch (final IOException ex) {
      ex.printStackTrace();
    }
  }
}
