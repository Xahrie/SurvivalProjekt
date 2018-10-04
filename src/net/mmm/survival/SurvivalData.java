package net.mmm.survival;

import java.util.Map;
import java.util.UUID;

import net.mmm.survival.mysql.AsyncMySQL;
import net.mmm.survival.player.SurvivalPlayer;
import net.mmm.survival.regions.DynmapWorldGuardPlugin;

public class SurvivalData {
  private static SurvivalData survivalData;

  private final AsyncMySQL async = new AsyncMySQL();
  private final Map<UUID, String> playerCache;
  private final Map<UUID, SurvivalPlayer> players;
  private DynmapWorldGuardPlugin dynmap;

  /**
   * Konstruktor
   */
  public SurvivalData() {
    players = async.getPlayers(); // Lade Spieler(SurvivalPlayer) von MySQL
    playerCache = async.getPlayerCache(); // Lade Spielerdatenbank von MySQL
  }

  /**
   * @return Instanz
   */
  public static SurvivalData getInstance() {
    if (survivalData == null) {
      survivalData = new SurvivalData();
    }
    return survivalData;
  }

  //<editor-fold desc="getter and setter">
  public AsyncMySQL getAsyncMySQL() {
    return async;
  }

  public DynmapWorldGuardPlugin getDynmap() {
    return dynmap;
  }

  void setDynmap(final DynmapWorldGuardPlugin dynmap) {
    this.dynmap = dynmap;
  }

  public Map<UUID, String> getPlayerCache() {
    return playerCache;
  }

  public Map<UUID, SurvivalPlayer> getPlayers() {
    return players;
  }
  //</editor-fold>
}
