package net.mmm.survival;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import net.mmm.survival.commands.Complain;
import net.mmm.survival.commands.Economy;
import net.mmm.survival.commands.Gamemode;
import net.mmm.survival.commands.Home;
import net.mmm.survival.commands.Licence;
import net.mmm.survival.commands.Money;
import net.mmm.survival.commands.Navi;
import net.mmm.survival.commands.Pay;
import net.mmm.survival.commands.Save;
import net.mmm.survival.commands.SetHome;
import net.mmm.survival.commands.SetSpawn;
import net.mmm.survival.commands.Spawn;
import net.mmm.survival.commands.Tame;
import net.mmm.survival.commands.TeleportWorld;
import net.mmm.survival.commands.Vote;
import net.mmm.survival.commands.Zone;
import net.mmm.survival.events.ChatEvents;
import net.mmm.survival.events.CommandEvents;
import net.mmm.survival.events.DeathEvents;
import net.mmm.survival.events.EntityEvents;
import net.mmm.survival.events.FarmingEvents;
import net.mmm.survival.events.InteractEvents;
import net.mmm.survival.events.LocationChangeEvents;
import net.mmm.survival.events.PlayerConnectionEvents;
import net.mmm.survival.farming.StatsManager;
import net.mmm.survival.mysql.AsyncMySQL;
import net.mmm.survival.mysql.MySQL;
import net.mmm.survival.player.Scoreboards;
import net.mmm.survival.regions.DynmapWorldGuardPlugin;
import net.mmm.survival.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main-Klasse
 *
 * @author PAS123
 * @author BlueIronGirl
 * @author Abgie
 * Ersteller des Projekts ist PAS123. Zu den Autor*innen gehoeren auch noch_
 * BlueIronGirl und Abgie
 */
public class Survival extends JavaPlugin {
  private static Survival server;

  /**
   * Wird bei der Aktivierung des Servers durchgefuehrt
   */
  public void onEnable() {
    final SurvivalData survivalData = createInstanceAndData();
    setupPlugin(survivalData);
  }

  private SurvivalData createInstanceAndData() {
    server = this;
    return SurvivalData.getInstance();
  }

  private void setupPlugin(final SurvivalData survivalData) {
    final AsyncMySQL asyncMySQL = survivalData.getAsyncMySQL();
    final MySQL mySQL = asyncMySQL.getMySQL();
    mySQL.createTables(); // Tabellen erzeugen

    registerEvents();
    registerCommands();
    registerDynmap(survivalData);

    //Scheduler starten fuer regelmaessige Aufgaben
    execScheduler();
  }

  private void registerEvents() {
    final List<Listener> listeners = Arrays.asList(new ChatEvents(), new CommandEvents(),
        new DeathEvents(), new EntityEvents(), new FarmingEvents(), new InteractEvents(),
        new LocationChangeEvents(), new PlayerConnectionEvents());

    for (final Listener listener : listeners) {
      Bukkit.getPluginManager().registerEvents(listener, this);
    }
  }

  private void registerCommands() {
    final List<CommandExecutor> commands = Arrays.asList(new Complain(), new Economy(),
        new Gamemode(), new Home(), new Licence(), new Money(), new Navi(), new Pay(), new Save(),
        new SetHome(), new SetSpawn(), new Spawn(), new Tame(), new TeleportWorld(), new Vote(),
        new Zone());

    for (final CommandExecutor commandExecutor : commands) {
      final Class<? extends CommandExecutor> commandExecutorClass = commandExecutor.getClass();
      final String commandName = commandExecutorClass.getName();
      getCommand(commandName.substring(26).toLowerCase()).setExecutor(commandExecutor);
    }
  }

  private void registerDynmap(final SurvivalData survivalData) {
    final DynmapWorldGuardPlugin dynmap = new DynmapWorldGuardPlugin();
    dynmap.onEnable();
    survivalData.setDynmap(dynmap);
  }

  private void execScheduler() {
    final AtomicInteger counter = new AtomicInteger();
    Bukkit.getScheduler().scheduleSyncRepeatingTask(getInstance(), () -> {
      if (counter.get() % 2 == 0) {
        for (final Player player : Bukkit.getOnlinePlayers()) {
          Scoreboards.setScoreboards(player);
        }
      }
      if (counter.get() % 60 == 0) {
        StatsManager.saveStats(); // Statistiken werden 1 Mal pro Minute in Geld umgewandelt
      }
      //alle 5 Minuten alle Daten in der Datenbank speichern
      if (counter.get() % 300 == 0) {
        save();
      }
      if (counter.get() == Integer.MAX_VALUE) {
        counter.set(0);
      }
      counter.getAndIncrement();
    }, 20L, 20L);
  }

  /**
   * @return Instanz des Plugins
   */
  public static Survival getInstance() {
    return server;
  }

  public void save() {
    // Statistiken der Spieler speichern bzw. in Geld umwandeln
    StatsManager.saveStats();

    //Spielerdaten speichern
    System.out.println("Saving to Database");
    final AsyncMySQL asyncMySQL = SurvivalData.getInstance().getAsyncMySQL();
    asyncMySQL.storePlayers();
  }

  /**
   * Wird bei der Deaktivierung des Servers durchgefuehrt
   */
  public void onDisable() {
    for (final Player player : Bukkit.getOnlinePlayers()) {
      player.kickPlayer(Messages.PREFIX + "Der Server wird neugestartet.");
    }

    //Alle Werte in Datenbank speichern
    save();

    final AsyncMySQL asyncMySQL = SurvivalData.getInstance().getAsyncMySQL();
    final MySQL mySQL = asyncMySQL.getMySQL();
    mySQL.closeConnection(); /* Datenbankverbindung schliessen */
    final DynmapWorldGuardPlugin dynmap = SurvivalData.getInstance().getDynmap();
    if (dynmap != null) {
      dynmap.onDisable(); /* Disable von Dynmap */
    }
  }
}