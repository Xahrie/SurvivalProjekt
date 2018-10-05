package net.mmm.survival;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import net.mmm.survival.commands.Complain;
import net.mmm.survival.commands.Economy;
import net.mmm.survival.commands.Gamemode;
import net.mmm.survival.commands.Home;
import net.mmm.survival.commands.Money;
import net.mmm.survival.commands.Navi;
import net.mmm.survival.commands.Pay;
import net.mmm.survival.commands.Save;
import net.mmm.survival.commands.SetHome;
import net.mmm.survival.commands.SetSpawn;
import net.mmm.survival.commands.Spawn;
import net.mmm.survival.commands.Tame;
import net.mmm.survival.commands.Vote;
import net.mmm.survival.commands.Zone;
import net.mmm.survival.events.ChangedExpEvents;
import net.mmm.survival.events.ChatEvents;
import net.mmm.survival.events.CommandEvents;
import net.mmm.survival.events.DeathEvents;
import net.mmm.survival.events.EntityEvents;
import net.mmm.survival.events.FarmingEvents;
import net.mmm.survival.events.InteractEvents;
import net.mmm.survival.events.LocationChangeEvents;
import net.mmm.survival.events.PlayerConnectionEvents;
import net.mmm.survival.farming.StatsManager;
import net.mmm.survival.regions.DynmapWorldGuardPlugin;
import net.mmm.survival.regions.SurvivalWorld;
import net.mmm.survival.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main-Klasse
 *
 * @author PAS123
 * @author BlueIronGirl
 * @author Abgie
 * Ersteller des Projekts ist PAS123. Zu den Autor*innen gehoeren auch noch
 * BlueIronGirl und Abgie
 */
public class Survival extends JavaPlugin {
  private static Survival server = null;

  /**
   * @return Instanz des Plugins
   */
  public static Survival getInstance() {
    return server;
  }

  /**
   * Wird bei der Aktivierung des Servers durchgefuehrt
   */
  public void onEnable() {
    final SurvivalData survivalData = createInstanceAndData();
    setupPlugin(survivalData);
    checkAndCreateWorlds();
  }

  /**
   * Wird bei der Deaktivierung des Servers durchgefuehrt
   */
  public void onDisable() {
    Bukkit.getOnlinePlayers().forEach(player -> player.kickPlayer(Messages.PREFIX +
        "Der Server wird neugestartet."));
    save();
    SurvivalData.getInstance().getAsyncMySQL().getMySQL().closeConnection(); /* Datenbankverbindung schliessen */
    if (SurvivalData.getInstance().getDynmap() != null) {
      SurvivalData.getInstance().getDynmap().onDisable(); /* Disable von Dynmap */
    }
  }

  public void save() {
    StatsManager.saveStats(); /* Statistiken der Spieler speichern bzw. in Geld umwandeln */
    SurvivalData.getInstance().getAsyncMySQL().storePlayers(); /* Spielerdaten speichern */
  }

  private SurvivalData createInstanceAndData() {
    server = this;
    return SurvivalData.getInstance();
  }

  private void setupPlugin(final SurvivalData survivalData) {
    survivalData.getAsyncMySQL().getMySQL().createTables(); // Tabellen erzeugen
    registerEvents(); // Events registrieren
    registerCommands(); // Commands registrieren
    registerDynmap(survivalData); // Dynmap registrieren
    execScheduler(); // Starte den Counter
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  private void checkAndCreateWorlds() {
    Arrays.asList(SurvivalWorld.values()).forEach(SurvivalWorld::get);
  }

  private void registerEvents() {
    final List<Listener> listeners = Arrays.asList(new ChatEvents(), new CommandEvents(),
        new DeathEvents(), new EntityEvents(), new FarmingEvents(), new InteractEvents(),
        new LocationChangeEvents(), new PlayerConnectionEvents(), new ChangedExpEvents());
    listeners.forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, this));
  }

  private void registerCommands() {
    final List<CommandExecutor> commands = Arrays.asList(new Complain(), new Economy(), new Gamemode(), new Home(),
        new Money(), new Navi(), new Pay(), new Save(), new SetHome(), new SetSpawn(), new Spawn(), new Tame(),
        new Vote(), new Zone());
    commands.forEach(commandExecutor -> getCommand(commandExecutor.getClass().getName()
        .substring(26).toLowerCase()).setExecutor(commandExecutor));
  }

  private void registerDynmap(final SurvivalData survivalData) {
    final DynmapWorldGuardPlugin dynmap = new DynmapWorldGuardPlugin();
    dynmap.onEnable();
    survivalData.setDynmap(dynmap);
  }

  private void execScheduler() {
    final AtomicInteger counter = new AtomicInteger();
    Bukkit.getScheduler().scheduleSyncRepeatingTask(Survival.getInstance(), () -> {
          if (counter.get() % 60 == 0) {
            StatsManager.saveStats(); // Statistiken werden 1 Mal pro Minute in Geld umgewandelt
          } //TODO (Abgie) 30.09.2018: HotbarMessager ueberarbeiten
          if (counter.get() % 5 == 0) { //dauerhaft Geldwert anzeigen
            /*Bukkit.getOnlinePlayers().forEach(player ->
                SurvivalPlayer.findSurvivalPlayer(player, player.getName()).sendHotbarMessage(
                    "Money: " + SurvivalPlayer.findSurvivalPlayer(player, player.getName())
                        .getMoney() + Constants.CURRENCY));*/
          }
          counter.getAndIncrement();
        }

        , 20L, 20L);
  }
}
