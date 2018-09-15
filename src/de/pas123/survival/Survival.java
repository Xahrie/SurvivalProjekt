package de.pas123.survival;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.pas123.survival.commands.Befehle;
import de.pas123.survival.dynmap.DynmapWorldGuardPlugin_1_13;
import de.pas123.survival.util.AsyncMySQL;
import de.pas123.survival.util.Events;
import de.pas123.survival.util.Hotbar;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Survival extends JavaPlugin implements Listener {

  public Map<UUID, Integer> coins = new HashMap<UUID, Integer>();
  public Map<UUID, Integer> votes = new HashMap<UUID, Integer>();

  public File ordner = new File("plugins//Survival//");
  public File file = new File("plugins//Survival//data.yml");

  public YamlConfiguration data = YamlConfiguration.loadConfiguration(file);

  public DynmapWorldGuardPlugin_1_13 dynmap;

  public String prefix = "§8┃ §eSurvival §7»";

  public Location spawn;
  public Location netherspawn;
  public Location farmweltspawn;

//	public CloudServer server;

  public AsyncMySQL async;

  private static Survival system;

  public void onEnable() {
    system = this;
//		server = CloudServer.getInstance();
    Hotbar.setup();

    if (!ordner.exists()) {
      ordner.mkdir();
      try {
        file.createNewFile();
        data.set("mysql.host", "localhost");
        data.set("mysql.port", 3306);
        data.set("mysql.user", "root");
        data.set("mysql.password", "test123");
        data.set("mysql.database", "Votes");
        data.save(file);
      } catch (IOException e) {
      }
    } else {
      if (data.contains("Spawn")) {
        double x = data.getDouble("Spawn.x");
        double y = data.getDouble("Spawn.y");
        double z = data.getDouble("Spawn.z");
        float yaw = (float) data.getDouble("Spawn.yaw");
        float pitch = (float) data.getDouble("Spawn.pitch");
        spawn = new Location(Bukkit.getWorld("world"), x, y, z, yaw, pitch);
      }
      if (data.contains("Spawn.nether")) {
        double x = data.getDouble("Spawn.nether.x");
        double y = data.getDouble("Spawn.nether.y");
        double z = data.getDouble("Spawn.nether.z");
        float yaw = (float) data.getDouble("Spawn.nether.yaw");
        float pitch = (float) data.getDouble("Spawn.nether.pitch");
        netherspawn = new Location(Bukkit.getWorld("world_nether"), x, y, z, yaw, pitch);
      }
      if (data.contains("Spawn.farmwelt")) {
        double x = data.getDouble("Spawn.farmwelt.x");
        double y = data.getDouble("Spawn.farmwelt.y");
        double z = data.getDouble("Spawn.farmwelt.z");
        float yaw = (float) data.getDouble("Spawn.farmwelt.yaw");
        float pitch = (float) data.getDouble("Spawn.farmwelt.pitch");
        farmweltspawn = new Location(Bukkit.getWorld("world"), x, y, z, yaw, pitch);
      }
      try {
        async = new AsyncMySQL(this, data.getString("mysql.host"), data.getInt("mysql.port"), data.getString("mysql.user"), data.getString("mysql.password"), data.getString("mysql.database"));
        async.getMySQL().createTables();
      } catch (Exception ex) {
      }
    }

    Bukkit.getPluginManager().registerEvents(new Events(), this);

    getCommand("zone").setExecutor(new Befehle());
    getCommand("home").setExecutor(new Befehle());
    getCommand("sethome").setExecutor(new Befehle());
    getCommand("setspawn").setExecutor(new Befehle());
    getCommand("setnetherspawn").setExecutor(new Befehle());
    getCommand("setfarmweltspawn").setExecutor(new Befehle());
    getCommand("spawn").setExecutor(new Befehle());
    getCommand("navi").setExecutor(new Befehle());
    getCommand("tame").setExecutor(new Befehle());

    getCommand("gm").setExecutor(new Befehle());
    getCommand("gamemode").setExecutor(new Befehle());

    dynmap = new DynmapWorldGuardPlugin_1_13(this);

//		System.out.println(server.getServerConfig().getProperties());
//		server.getServerConfig().setExtra("§c⚠ §01.12.2 §c⚠");
//		server.update();
  }

  public static Survival getInstance() {
    return system;
  }

  public void save() {
    try {
      data.save(file);
    } catch (IOException e) {
    }
  }

}
