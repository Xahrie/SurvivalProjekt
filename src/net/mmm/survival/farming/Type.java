package net.mmm.survival.farming;

/**
 * Type beschreibt die Typen der Statistiken die hier getrackt werden
 *
 * @author Abgie on 28.09.18 08:58
 * project SurvivalProjekt
 * @version 1.0
 * @since JDK 8
 */
public enum Type {
  ONLINE_TIME("Online-Time"),
  WALK_LENGTH_CM("Walk-Length");

  private final String statistic;

  Type(final String statistic) {
    this.statistic = statistic;
  }

  public String get() {
    return statistic;
  }
}
