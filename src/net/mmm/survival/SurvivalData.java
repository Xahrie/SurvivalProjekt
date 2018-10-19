package net.mmm.survival;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.mmm.survival.mysql.AsyncMySQL;
import net.mmm.survival.player.SurvivalPlayer;
import net.mmm.survival.regions.DynmapWorldGuardPlugin;

public final class SurvivalData {
  private static SurvivalData survivalData;

  private final AsyncMySQL async;
  private final List<String> namesOfPlayersToVote;
  private final Map<UUID, String> playerCache;
  private final Map<UUID, SurvivalPlayer> players;
  private DynmapWorldGuardPlugin dynmap;

  /**
   * Konstruktor
   */
  private SurvivalData() {
    async = new AsyncMySQL();
    players = async.getPlayers(); // Lade Spieler(SurvivalPlayer) von MySQL
    playerCache = async.getPlayerCache(); // Lade Spielerdatenbank von MySQL
    namesOfPlayersToVote = new ArrayList<>();
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

  public List<String> getNamesOfPlayersToVote() {
    return namesOfPlayersToVote;
  }

  public Map<UUID, String> getPlayerCache() {
    return playerCache;
  }

  public Map<UUID, SurvivalPlayer> getPlayers() {
    return players;
  }
  //</editor-fold>
}
