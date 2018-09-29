package net.mmm.survival.farming;

import java.util.HashSet;
import java.util.Set;

import net.mmm.survival.farming.statistics.OnlineTime;
import net.mmm.survival.farming.statistics.Statistic;
import net.mmm.survival.farming.statistics.WalkLength;

/**
 * PlayerStats speichert die Statistiken eines Spielers
 *
 * @author Abgie on 28.09.18 09:35
 * project SurvivalProjekt
 * @version 1.0
 * @since JDK 8
 */
public class PlayerStats {
  private final Set<Statistic> statistics = new HashSet<>();

  /**
   * Konstruktor zur Erstellung von PlayerStats
   */
  public PlayerStats() {
    fillStats();
  }

  private void fillStats() {
    statistics.add(new OnlineTime());
    statistics.add(new WalkLength());
  }

  /**
   * Statistik eines Typs
   *
   * @param statisticType Typ der Statistik
   * @return Statistik
   */
  public Statistic getStatistic(final Type statisticType) {
    return statistics.stream().filter(statistic ->
        statistic.getType().equals(statisticType)).findFirst().orElse(null);
  }

}
