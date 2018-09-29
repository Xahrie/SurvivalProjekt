package net.mmm.survival.farming;

import java.util.Set;

import net.mmm.survival.farming.statistics.Statistic;

/**
 * Eigenschaft von SurvivalPlayer. Erbt von PlayerStats, wo Spielerstatistiken
 * gespeichert werden.
 *
 * @see org.bukkit.Statistic
 */
public class Statistics extends PlayerStats {

  /**
   * Konstruktor
   */
  public Statistics() {
    super();
  }

  /**
   * Statistik eines Typs
   *
   * @param statisticType Typ der Statistik
   * @return Statistik
   */
  public Statistic getStatistic(final Type statisticType) {
    return super.get(statisticType);
  }

  /**
   * Fuegt manuell eine neue Statistik hinzu, bzw. updatet dessen Werte manuell
   *
   * @param statisticType Typ der Statistik
   * @param value Wert
   */
  public void setStatistic(final Type statisticType, final int value) {
    final Statistic statistic = StatsManager.create(statisticType, value);
    setStatistic(statistic);
  }

  private void setStatistic(final Statistic statistic) {
    final Set<Statistic> statistics = super.getStatistics();
    statistics.remove(statistics.stream().filter(stat ->
        stat.getType().equals(statistic.getType())).findFirst().orElse(null));
    statistics.add(statistic);
  }

}
