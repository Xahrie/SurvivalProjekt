package net.mmm.survival.farming;

import java.util.Map;
import java.util.UUID;

import net.mmm.survival.SurvivalData;
import net.mmm.survival.farming.statistics.Statistic;
import net.mmm.survival.player.SurvivalPlayer;

/**
 * StatsManager ist eine Klasse, die die Statistiken verwalten soll.
 *
 * @author Abgie on 28.09.18 09:41
 * project SurvivalProjekt
 * @version 1.0
 * @since JDK 8
 */
public final class StatsManager {

  /**
   * Speichere Statistiken aller Spieler
   */
  public static void saveStats() {
    final Map<UUID, SurvivalPlayer> players = SurvivalData.getInstance().getPlayers();
    players.keySet().forEach(uuid ->
        replaceStatsToMoneyAndResetStats(players.get(uuid)));
  }

  private static void replaceStatsToMoneyAndResetStats(final SurvivalPlayer playerToSave) {
    for (final Type type : Type.values()) {
      final PlayerStats playerToSaveStats = playerToSave.getStats();
      final Statistic playerToSaveStatistic = playerToSaveStats.getStatistic(type);
      playerToSave.addOrTakeMoney(playerToSaveStatistic.getMoney());
      playerToSaveStatistic.resetValue();
    }
  }
}
