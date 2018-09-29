package net.mmm.survival.farming;

import java.util.Arrays;

import net.mmm.survival.SurvivalData;
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

  /**
   * Speichere Statistiken aller Spieler
   */
  public static void saveStats() {
    SurvivalData.getInstance().getPlayers().keySet().forEach(uuid ->
        saveStats(SurvivalData.getInstance().getPlayers().get(uuid)));
  }

  /**
   * speichere Statistiken des Spielers <code>playerToSave</code>, setze die
   * Statistiken lokal zurueck und wandle diese in einen verdienst um
   *
   * @param playerToSave zu verarbeitender Spieler
   */
  private static void saveStats(final SurvivalPlayer playerToSave) {
    replaceStatsToMoney(playerToSave);
    resetStats(playerToSave);
  }

  private static void replaceStatsToMoney(final SurvivalPlayer playerToSave) {
    for (final Type type : Type.values()) {
      playerToSave.setMoney(playerToSave.getStats().getStatistic(type).getMoney() + playerToSave.getMoney());
    }
  }

  private static void resetStats(final SurvivalPlayer playerToSave) {
    Arrays.asList(Type.values()).forEach(type -> playerToSave.getStats().getStatistic(type).resetValue());
  }
}
