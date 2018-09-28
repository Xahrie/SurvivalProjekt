package net.mmm.survival.farming;

import java.util.Arrays;

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
public class StatsManager {

  public static void saveStats() {
    SurvivalData.getInstance().getPlayers().keySet().forEach(uuid ->
        saveStats(SurvivalData.getInstance().getPlayers().get(uuid)));
  }

  public static void saveStats(SurvivalPlayer playerToSave) {
    replaceStatsToMoney(playerToSave);
    resetStats(playerToSave);
  }

  private static void updateStats(SurvivalPlayer playerToSave) {
    Arrays.asList(Type.values()).forEach(type ->
        playerToSave.getStats().getStatistic(type).update(playerToSave));
  }

  private static void replaceStatsToMoney(SurvivalPlayer playerToSave) {
    Arrays.asList(Type.values()).forEach(type ->
        playerToSave.setMoney((int) playerToSave.getStats().getStatistic(type).getMoney() +
            playerToSave.getMoney()));
  }

  private static void resetStats(SurvivalPlayer playerToSave) {
    if (!playerToSave.getStats().isEmpty()) {
      Arrays.asList(Type.values()).forEach(type ->
          playerToSave.getStats().getStatistic(type).resetValue());
    }

  }

  public static Statistic create(Type type) {
    return create(type, 0);
  }

  public static Statistic create(Type type, int value) {
    Statistic statistic = type.get();
    statistic.setValue(value);
    return statistic;
  }
}
