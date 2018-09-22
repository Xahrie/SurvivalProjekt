package net.mmm.survival;

import java.util.Map;
import java.util.UUID;

import net.mmm.survival.dynmap.DynmapWorldGuardPlugin;
import net.mmm.survival.mysql.AsyncMySQL;
import net.mmm.survival.player.SurvivalPlayer;

public class SurvivalData {
  private static SurvivalData survivalData;

  private final AsyncMySQL async = new AsyncMySQL();
  private DynmapWorldGuardPlugin dynmap;
  private final Map<UUID, SurvivalPlayer> players;

  /**
   * Konstruktor
   */
  public SurvivalData() {
    players = async.getPlayers();
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

  public Map<UUID, SurvivalPlayer> getPlayers() {
    return players;
  }

  public DynmapWorldGuardPlugin getDynmap() {
    return dynmap;
  }

  public void setDynmap(final DynmapWorldGuardPlugin dynmap) {
    this.dynmap = dynmap;
  }
  //</editor-fold>
}
