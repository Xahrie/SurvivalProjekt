package net.mmm.survival.util.logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import net.mmm.survival.util.Messages;
import org.bukkit.entity.Player;

/**
 * Import from abgie.tool.util.logging.Logger
 *
 * @author Abgie on 03.10.2018 16:58
 * project SurvivalProjekt
 * @version 1.0
 * @since JDK 8
 */
public class Logger implements Exitable {
  private final Filter filter;
  private LogManager logManager;
  private final String name;

  public Logger(final String name) {
    this.name = name;
    this.filter = new Filter();
    createLogManager(new File("plugins//Survival//log//log_" + new SimpleDateFormat("YYYY_MM_dd").format(new Date()) + ".log"));
  }

  @Override
  public void exit() {
    this.logManager.exit();
  }

  /**
   * Der Error wird optisch deutlich sichtbar gemacht
   *
   * @param errorMessage Mitteilung, die auszugeben ist
   */
  public void showError(final String errorMessage) {
    System.err.println("================================================================================");
    System.err.println("================================================================================");
    System.err.println(errorMessage);
    System.err.println("================================================================================");
    System.err.println("================================================================================");
  }

  /**
   * Die Infonachricht wird optisch deutlich sichtbar gemacht
   *
   * @param infoMessage Mitteilung, die auszugeben ist
   */
  public void showInfo(final String infoMessage) {
    System.out.println("================================================================================");
    System.out.println("================================================================================");
    System.out.println(infoMessage);
    System.out.println("================================================================================");
    System.out.println("================================================================================");
  }

  /**
   * Die Warnung wird optisch deutlich sichtbar gemacht
   *
   * @param warnMessage Mitteilung, die auszugeben ist
   */
  public void showWarning(final String warnMessage) {
    info(warnMessage);
  }

  private void toLog(final Level level, final String msg) {
    final String logMessage = new SimpleDateFormat("dd.MM.YYYY HH:mm:ss:SSS").format(new Date() + " " + name + "\n" + msg);
    if (filter.get().stream().anyMatch(item -> msg.toLowerCase().contains(item.toLowerCase())) || filter.get().isEmpty()) {
      logManager.log(logMessage);
      if (level.equals(Level.SEVERE)) {
        System.err.println(logMessage);
      } else {
        System.out.println(logMessage);
      }
    }
  }

  private void severe(final String msg) {
    toLog(Level.SEVERE, "SEVERE: " + msg);
  }

  private void warning(final String msg) {
    toLog(Level.WARNING, "WARN: " + msg);
  }

  private void info(final String msg) {
    toLog(Level.INFO, "INFO: " + msg);
  }

  private void fine(final String msg) {
    toLog(Level.FINE, "FINE: " + msg);
  }

  private void createLogManager(final File logFile) {
    final File logDirectory = new File("plugins//Survival//log");
    if (!new File("log").exists() && logDirectory.mkdir()) {
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

  //<editor-fold desc="Types">
  public void trace(final Object message) {
    info(message.toString());
  }

  public void trace(final String message) {
    info(message);
  }

  public void trace(final Player player) {
    final String msg = "Player: " + player.getDisplayName();
    info(msg);
  }

  public void trace(final Throwable t) {
    info(t.toString());
  }

  public void trace(final Object message, final Throwable t) {
    final String msg = message.toString() + "\n" + t.toString();
    info(msg);
  }

  public void trace(final Player player, final Throwable t) {
    final String msg = "Player: " + player.getDisplayName() + "\n" + t.toString();
    info(msg);
  }

  public void trace(final String message, final Object o) {
    final String msg = message + "\n" + o.toString();
    info(msg);
  }

  public void trace(final String message, final Player player) {
    final String msg = message + "\nPlayer: " + player.getDisplayName();
    info(msg);
  }

  public void trace(final String message, final Throwable t) {
    final String msg = message + "\n" + t.toString();
    info(msg);
  }

  public void trace(final String message, final Object... params) {
    final String msg = message + "\n" + Arrays.toString(params);
    info(msg);
  }

  public void trace(final String message, final Player... players) {
    final List<String> pl = new ArrayList<>();
    Arrays.asList(players).forEach(player -> pl.add(player.getDisplayName()));

    final String msg = message + "\nPlayers: " + pl;
    info(msg);
  }

  public void trace(final String message, final Throwable t, final Player... players) {
    final List<String> pl = new ArrayList<>();
    Arrays.asList(players).forEach(player -> pl.add(player.getDisplayName()));

    final String msg = message + "\n" + t.toString() + "\nPlayers: " + pl;
    info(msg);
  }

  public void warn(final Object message) {
    warning(message.toString());
  }

  public void warn(final String message) {
    warning(message);
  }

  public void warn(final Player player) {
    final String msg = "Player: " + player.getDisplayName();
    warning(msg);
  }

  public void warn(final Throwable t) {
    warning(t.toString());
  }

  public void warn(final Object message, final Throwable t) {
    final String msg = message.toString() + "\n" + t.toString();
    warning(msg);
  }

  public void warn(final Player player, final Throwable t) {
    final String msg = "Player: " + player.getDisplayName() + "\n" + t.toString();
    warning(msg);
  }

  public void warn(final String message, final Object o) {
    final String msg = message + "\n" + o.toString();
    warning(msg);
  }

  public void warn(final String message, final Player player) {
    final String msg = message + "\nPlayer: " + player.getDisplayName();
    warning(msg);
  }

  public void warn(final String message, final Throwable t) {
    final String msg = message + "\n" + t.toString();
    warning(msg);
  }

  public void warn(final String message, final Object... params) {
    final String msg = message + "\n" + Arrays.toString(params);
    warning(msg);
  }

  public void warn(final String message, final Player... players) {
    final List<String> pl = new ArrayList<>();
    Arrays.asList(players).forEach(player -> pl.add(player.getDisplayName()));

    final String msg = message + "\nPlayers: " + pl;
    warning(msg);
  }

  public void warn(final String message, final Throwable t, final Player... players) {
    final List<String> pl = new ArrayList<>();
    Arrays.asList(players).forEach(player -> pl.add(player.getDisplayName()));

    final String msg = message + "\n" + t.toString() + "\nPlayers: " + pl;
    warning(msg);
  }

  public void error(final Object message) {
    severe(message.toString());
  }

  public void error(final String message) {
    severe(message);
  }

  public void error(final Player player) {
    final String msg = "Player: " + player.getDisplayName();
    severe(msg);
  }

  public void error(final Throwable t) {
    severe(t.toString());
  }

  public void error(final Object message, final Throwable t) {
    final String msg = message.toString() + "\n" + t.toString();
    severe(msg);
  }

  public void error(final Player player, final Throwable t) {
    final String msg = "Player: " + player.getDisplayName() + "\n" + t.toString();
    severe(msg);
  }

  public void error(final String message, final Object o) {
    final String msg = message + "\n" + o.toString();
    severe(msg);
  }

  public void error(final String message, final Player player) {
    final String msg = message + "\nPlayer: " + player.getDisplayName();
    severe(msg);
  }

  public void error(final String message, final Throwable t) {
    final String msg = message + "\n" + t.toString();
    severe(msg);
  }

  public void error(final String message, final Object... params) {
    final String msg = message + "\n" + Arrays.toString(params);
    severe(msg);
  }

  public void error(final String message, final Player... players) {
    final List<String> pl = new ArrayList<>();
    Arrays.asList(players).forEach(player -> pl.add(player.getDisplayName()));

    final String msg = message + "\nPlayers: " + pl;
    severe(msg);
  }

  public void error(final String message, final Throwable t, final Player... players) {
    final List<String> pl = new ArrayList<>();
    Arrays.asList(players).forEach(player -> pl.add(player.getDisplayName()));

    final String msg = message + "\n" + t.toString() + "\nPlayers: " + pl;
    severe(msg);
  }

  public void entry(final Object... params) {
    fine(Arrays.toString(params));
  }
  //</editor-fold>

  public Filter getFilter() {
    return filter;
  }

  public LogManager getLogManager() {
    return logManager;
  }
}