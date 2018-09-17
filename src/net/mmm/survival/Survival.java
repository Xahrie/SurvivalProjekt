package net.mmm.survival;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
import net.mmm.survival.mysql.AsyncMySQL;
import net.mmm.survival.player.Hotbar;
import net.mmm.survival.player.SurvivalPlayer;
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
 * Hauptautor des Projekts ist PAS123. Nebenautor*innen sind BlueIronGirl und Abgie
 */

public class Survival extends JavaPlugin {

  private static Survival system;

  public AsyncMySQL async;
  public DynmapWorldGuardPlugin dynmap;
  public Map<UUID, SurvivalPlayer> players = new HashMap<>();

  /**
   * Konstruktor
   *
   * @return Instanz von Survival
   */
  public static Survival getInstance() {
    return system;
  }

  /**
   * Wird bei der Aktivierung des Servers durchgefuehrt
   */
  public void onEnable() {
    system = this;
    Hotbar.setup();
    async = new AsyncMySQL();
    async.getMySQL().createTables();

    registerEvents();
    registerCommands();

    if (Bukkit.getWorlds().contains(Bukkit.getWorld("farmwelt"))) {
      Bukkit.createWorld(new WorldCreator("farmwelt"));
    }

    this.players = loadPlayers();

    dynmap = new DynmapWorldGuardPlugin(this);
  }

  /**
   * Wird bei der Deaktivierung des Servers durchgefuehrt
   */
  public void onDisable() {
    storePlayers();
    Bukkit.getOnlinePlayers().forEach(player -> player.kickPlayer(Messages.PREFIX + "Der Server wird neugestartet."));
  }

  /**
   * Events werden registriert
   */
  private void registerEvents() {
    final List<Listener> listeners = Arrays.asList(new ChatEvents(), new CommandEvents(), new PlayerConnectionEvents(), new DeathEvents(),
        new EntityEvents(), new InteractEvents(), new LocationChangeEvents());

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
   * Lade Spieler aus der Datenbank
   *
   * @return Liste mit den angemeldeten Spielern
   */
  private Map<UUID, SurvivalPlayer> loadPlayers() {
    final AsyncMySQL sql = new AsyncMySQL();

    return sql.getPlayers();
  }

  /**
   * Speichere Spieler in der Datenbank
   */
  private void storePlayers() {
    final AsyncMySQL sql = new AsyncMySQL();

    sql.storePlayers();
  }

}
