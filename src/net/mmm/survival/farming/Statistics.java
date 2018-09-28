package net.mmm.survival.farming;

import java.util.Set;

import net.mmm.survival.farming.statistics.Statistic;

/**
 * @see org.bukkit.Statistic
 */
public class Statistics extends PlayerStats {

  public Statistics() {
    super();
  }

  public Statistic getStatistic(Type statisticType) {
    return super.get(statisticType);
  }

  public void setStatistic(Type statisticType, int value) {
    Statistic statistic = StatsManager.create(statisticType, value);
    setStatistic(statistic);
  }

  public void setStatistic(Statistic statistic) {
    Set<Statistic> statistics = super.getStatistics();
    statistics.remove(statistics.stream().filter(stat ->
        stat.getType().equals(statistic.getType())).findFirst().orElse(null));
    statistics.add(statistic);
  }

}
