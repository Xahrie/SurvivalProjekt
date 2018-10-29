package net.mmm.survival.util.logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

import net.mmm.survival.util.Messages;

/**
 * Import from abgie.tool.util.logging.Logger
 *
 * @author Abgie on 03.10.2018 16:58
 * project SurvivalProjekt
 * @version 1.0
 * @since JDK 8
 */
public class Logger implements Exitable {
  private final String name;
  private LogManager logManager;

  public Logger(final String name) {
    this.name = name;
    createLogManager(new File("plugins" + File.separator + "Survival" + File.separator + "log" +
        File.separator + "log_" + new SimpleDateFormat("YYYY_MM_dd").format(new Date()) + ".log"));
  }

  private void createLogManager(final File logFile) {
    final File logDirectory = new File("plugins" + File.separator + "Survival" + File.separator + "log");

    if (!logDirectory.exists() && logDirectory.mkdir()) {
      System.out.println(Messages.LOG_CREATED);
    }
    doesLogFileExist(logFile);
    this.logManager = new LogManager(logFile);
  }

  private void doesLogFileExist(final File logFile) {
    if (!logFile.exists()) {
      try {
        if (logFile.createNewFile()) {
          createFile(logFile);
        } else System.out.println("File cannot build.");
      } catch (final IOException ex) {
        ex.printStackTrace();
      }
    }
  }

  private void createFile(final File logFile) throws IOException {
    System.out.println("Log successful set.");
    try (final Writer writer = new FileWriter(logFile)) {
      writer.write("The Log of " + new SimpleDateFormat("YYYY_MM_dd").format(new Date()) + ":");
    }
  }

  @Override
  public void exit() {
    this.logManager.exit();
  }

  public void warn(final Throwable t) {
    final String msg = t.toString();
    warning(msg);
  }

  private void warning(final String msg) {
    toLog(Level.WARNING, "WARN: " + msg);
  }

  private void toLog(final Level level, final String msg) {
    final String logMessage = new SimpleDateFormat("dd.MM.YYYY HH:mm:ss:SSS").format(new Date()) + " " + name + "\n" + msg;
    logManager.log(logMessage);
    if (level.equals(Level.SEVERE)) {
      System.err.println(logMessage);
    } else {
      System.out.println(logMessage);
    }
  }

  public void error(final String message) {
    severe(message);
  }

  private void severe(final String msg) {
    toLog(Level.SEVERE, "SEVERE: " + msg);
  }

  public void error(final String message, final Throwable t) {
    final String msg = message + "\n" + t;
    severe(msg);
  }

  public void error(final Throwable t) {
    final String msg = t.toString();
    severe(msg);
  }
}