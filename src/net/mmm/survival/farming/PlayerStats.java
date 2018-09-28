package net.mmm.survival.farming;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.mmm.survival.farming.statistics.Statistic;

/**
 * PlayerStats speichert die Statistiken eines Spielers
 *
 * @author Abgie on 28.09.18 09:35
 * project SurvivalProjekt
 * @version 1.0
 * @since JDK 8
 */
public class PlayerStats {
  private Set<Statistic> statistics;

  /**
   * Konstruktor zur Erstellung von PlayerStats
   */
  PlayerStats() {
    this.statistics = new HashSet<>();
    fillStats();
  }

  private void fillStats() {
    Arrays.asList(Type.values()).forEach(type -> statistics.add(StatsManager.create(type)));
  }

  public Set<Statistic> getStatistics() {
    return statistics;
  }

  protected Statistic get(Type statisticType) {
    return statistics.stream().filter(statistic ->
        statistic.getType().equals(statisticType)).findFirst().orElse(null);
  }

  public boolean isEmpty() {
    return statistics.stream().allMatch(statistic -> statistic.getValue() == 0);
  }
}
