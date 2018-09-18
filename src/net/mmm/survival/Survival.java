package net.mmm.survival;

import java.util.Arrays;
import java.util.List;

import net.mmm.survival.commands.Gamemode;
import net.mmm.survival.commands.Home;
import net.mmm.survival.commands.Navi;
import net.mmm.survival.commands.SetHome;
import net.mmm.survival.commands.SetSpawn;
import net.mmm.survival.commands.Spawn;
import net.mmm.survival.commands.Tame;
import net.mmm.survival.commands.Vote;
import net.mmm.survival.commands.Zone;
import net.mmm.survival.dynmap.DynmapWorldGuardPlugin;
import net.mmm.survival.events.ChatEvents;
import net.mmm.survival.events.CommandEvents;
import net.mmm.survival.events.DeathEvents;
import net.mmm.survival.events.EntityEvents;
import net.mmm.survival.events.InteractEvents;
import net.mmm.survival.events.LocationChangeEvents;
import net.mmm.survival.events.PlayerConnectionEvents;
import net.mmm.survival.events.ServerEvents;
import net.mmm.survival.player.Hotbar;
import net.mmm.survival.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main-Klasse
 *
 * @author PAS123
 * @author BlueIronGirl
 * @author Abgie
 * Ersteller des Projekts ist PAS123. Zu den Autor*innen gehoeren auch noch BlueIronGirl und Abgie
 */

public class Survival extends JavaPlugin {
  private static Survival server = null;

  public static Survival getInstance() {
    return server;
  }

  /**
   * Wird bei der Aktivierung des Servers durchgefuehrt
   */
  public void onEnable() {
    server = this;
    final SurvivalData survivalData = SurvivalData.getInstance();

    //Tabellen erzeugen
    survivalData.getAsyncMySQL().getMySQL().createTables();

    Hotbar.setup();
    registerEvents();
    registerCommands();
    registerDynmap(survivalData);

    //Farmwelt ggf. erzeugen
    if (Bukkit.getWorlds().contains(Bukkit.getWorld("farmwelt"))) {
      Bukkit.createWorld(new WorldCreator("farmwelt"));
    }
  }

  /**
   * Wird bei der Deaktivierung des Servers durchgefuehrt
   */
  public void onDisable() {
    //Spielerdaten speichern
    SurvivalData.getInstance().getAsyncMySQL().storePlayers();

    //Datenbankverbindung schliessen
    SurvivalData.getInstance().getAsyncMySQL().getMySQL().closeConnection();

    Bukkit.getOnlinePlayers().forEach(player -> player.kickPlayer(Messages.PREFIX + "Der Server wird neugestartet."));
  }

  /**
   * Events werden registriert
   */
  private void registerEvents() {
    final List<Listener> listeners = Arrays.asList(new ChatEvents(), new CommandEvents(), new PlayerConnectionEvents(), new DeathEvents(),
        new EntityEvents(), new InteractEvents(), new LocationChangeEvents(), new ServerEvents());

    listeners.forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, this));
  }

  /**
   * Commands werden registriert
   */
  private void registerCommands() {
    final List<CommandExecutor> commands = Arrays.asList(new Gamemode(), new Home(), new Navi(), new SetHome(), new SetSpawn(), new Spawn(),
        new Tame(), new Vote(), new Zone());

    commands.forEach(commandExecutor -> getCommand(commandExecutor.getClass().getName().substring(26)).setExecutor(commandExecutor));
  }

  /**
   * Dynmap wird registriert
   *
   * @param survivalData Speicher
   */
  private void registerDynmap(final SurvivalData survivalData) {
    final DynmapWorldGuardPlugin dynmap = new DynmapWorldGuardPlugin();
    dynmap.enable();
    survivalData.setDynmap(dynmap);
  }

}
