package net.mmm.survival;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.mmm.survival.mysql.AsyncMySQL;
import net.mmm.survival.player.SurvivalPlayer;
import net.mmm.survival.regions.DynmapWorldGuardPlugin;
import net.mmm.survival.util.LevelState;

public final class SurvivalData {
  private static SurvivalData survivalData;

  private final AsyncMySQL async = new AsyncMySQL();
  private final Map<UUID, String> playerCache;
  private final Map<UUID, SurvivalPlayer> players;
  private final Map<Integer, Float> levels;
  private DynmapWorldGuardPlugin dynmap;

  /**
   * Konstruktor
   */
  private SurvivalData() {
    players = async.getPlayers(); // Lade Spieler(SurvivalPlayer) von MySQL
    playerCache = async.getPlayerCache(); // Lade Spielerdatenbank von MySQL
    levels = new HashMap<>();
    levelsBerechnen();
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

  private void levelsBerechnen() {
    float exp = 100F;
    for (int i = 1; i < 100; i++) {
      final LevelState state = LevelState.LEVEL_1_BETWEEN_20.getLevelState(i);
      exp += (state != null ? state.getFactor() : 0) * exp;
      levels.put(i, exp);
    }
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

  public Map<Integer, Float> getLevels() {
    return levels;
  }

  public Map<UUID, String> getPlayerCache() {
    return playerCache;
  }

  public Map<UUID, SurvivalPlayer> getPlayers() {
    return players;
  }
  //</editor-fold>
}
