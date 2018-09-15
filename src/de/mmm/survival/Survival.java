package de.mmm.survival;

import de.mmm.survival.commands.Gamemode;
import de.mmm.survival.commands.Home;
import de.mmm.survival.commands.Navi;
import de.mmm.survival.commands.SetHome;
import de.mmm.survival.commands.SetSpawn;
import de.mmm.survival.commands.Spawn;
import de.mmm.survival.commands.Tame;
import de.mmm.survival.commands.Vote;
import de.mmm.survival.commands.Zone;
import de.mmm.survival.config.Config;
import de.mmm.survival.dynmap.DynmapWorldGuardPlugin_1_13;
import de.mmm.survival.mysql.AsyncMySQL;
import de.mmm.survival.player.SurvivalPlayer;
import de.mmm.survival.util.Events;
import de.mmm.survival.util.Hotbar;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Main-Klasse
 */
public class Survival extends JavaPlugin implements Listener {

  private static Survival system;

  public AsyncMySQL async;
  public DynmapWorldGuardPlugin_1_13 dynmap;
  public List<SurvivalPlayer> playerList = new ArrayList<>();
  public HashMap<String, Location> spawns;

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
//		server = CloudServer.getInstance();
    Hotbar.setup();
    final Config config = Config.getInstance();
    final File ordner = config.getOrdner();
    final YamlConfiguration data = config.getData();
    final File file = config.getFile();

    if (!ordner.exists()) {
      //noinspection ResultOfMethodCallIgnored
      ordner.mkdir();
      try {
        //noinspection ResultOfMethodCallIgnored
        file.createNewFile();
        setSQLValues(data);
        data.save(file);
      } catch (final IOException ignored) {
      }
    } else {
      setLocations(data);
      async = new AsyncMySQL(data.getString("mysql.host"), data.getInt("mysql.port"), data.getString("mysql.user"), data.getString("mysql.password"), data.getString("mysql.database"));
      async.getMySQL().createTables();
    }

    Bukkit.getPluginManager().registerEvents(new Events(), this);
    loadCommands();

    dynmap = new DynmapWorldGuardPlugin_1_13(this);

    this.playerList = loadPlayers();
  }

  /**
   * Commands werden geladen
   */
  private void loadCommands() {
    getCommand("zone").setExecutor(new Zone());
    getCommand("home").setExecutor(new Home());
    getCommand("sethome").setExecutor(new SetHome());
    getCommand("setspawn").setExecutor(new SetSpawn());
    getCommand("setnetherspawn").setExecutor(new SetSpawn());
    getCommand("setfarmweltspawn").setExecutor(new SetSpawn());
    getCommand("spawn").setExecutor(new Spawn());
    getCommand("navi").setExecutor(new Navi());
    getCommand("tame").setExecutor(new Tame());

    getCommand("vote").setExecutor(new Vote());

    getCommand("gm").setExecutor(new Gamemode());
    getCommand("gamemode").setExecutor(new Gamemode());
  }

  /**
   * SQL-Werte werden gesetzt
   *
   * @param data Konfigurationsdatei
   */
  private void setSQLValues(final YamlConfiguration data) {
    data.set("mysql.host", AsyncMySQL.VOTIFIER_HOST);
    data.set("mysql.port", AsyncMySQL.VOTIFIER_PORT);
    data.set("mysql.user", AsyncMySQL.VOTIFIER_USER);
    data.set("mysql.password", AsyncMySQL.VOTIFIER_PASSWORT);
    data.set("mysql.database", AsyncMySQL.VOTIFIER_DATABASE);
  }

  /**
   * Setzt Spawnlocations
   *
   * @param data Konfigurationsdatei
   */
  private void setLocations(final YamlConfiguration data) {

    final Set<String> keysSpawn = data.getConfigurationSection("Spawn.").getKeys(false);
    final List<World> worlds = Bukkit.getWorlds();

    for (final String keySpawn : keysSpawn) {
      for (final World world : worlds) {
        //Spawn.WORLDNAME.x
        final String sWorldName = keySpawn.split("\\.")[1];
        if (sWorldName.equals(world.getName())) {
          Location location = this.spawns.get(sWorldName);
          if (location == null) {
            final double x = data.getDouble("Spawn." + sWorldName + ".x");
            final double y = data.getDouble("Spawn." + sWorldName + ".y");
            final double z = data.getDouble("Spawn." + sWorldName + ".z");
            final float yaw = (float) data.getDouble("Spawn." + sWorldName + ".yaw");
            final float pitch = (float) data.getDouble("Spawn." + sWorldName + ".pitch");
            location = new Location(world, x, y, z, yaw, pitch);
            this.spawns.put(sWorldName, location);
          }
        }
      }
    }
  }

  /**
   * Lade Spieler aus der Datenbank
   *
   * @return Liste mit den angemeldeten Spielern
   */
  private List<SurvivalPlayer> loadPlayers() {
    final AsyncMySQL sql = new AsyncMySQL(AsyncMySQL.PLAYER_HOST, AsyncMySQL.PLAYER_PORT, AsyncMySQL.PLAYER_USER,
            AsyncMySQL.PLAYER_PASSWORT, AsyncMySQL.PLAYER_DATABASE);

    return sql.getPlayers();
  }

}
