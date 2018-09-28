package net.mmm.survival.farming;

import net.mmm.survival.farming.statistics.OnlineTime;
import net.mmm.survival.farming.statistics.Statistic;
import net.mmm.survival.farming.statistics.WalkLength;

/**
 * Type beschreibt die Typen der Statistiken die hier getrackt werden
 *
 * @author Abgie on 28.09.18 08:58
 * project SurvivalProjekt
 * @version 1.0
 * @since JDK 8
 */
public enum Type {
  ONLINE_TIME(new OnlineTime()),
  WALK_LENGTH_CM(new WalkLength());

  private final Statistic statistic;

  Type(Statistic statistic) {
    this.statistic = statistic;
  }

  public Statistic get() {
    return statistic;
  }
}
