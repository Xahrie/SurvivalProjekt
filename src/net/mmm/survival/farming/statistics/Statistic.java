package net.mmm.survival.farming.statistics;

import net.mmm.survival.farming.Type;
import net.mmm.survival.player.SurvivalPlayer;

/**
 * Statistic beschreibt verschiedene Statistiken fuer einen gewissen Spieler.
 *
 * @author Abgie on 28.09.18 09:19
 * project SurvivalProjekt
 * @version 1.0
 * @since JDK 8
 */
public abstract class Statistic {
  private final Type type;
  private int value;

  Statistic(final Type type) {
    this.type = type;
  }

  //<editor-fold desc="getter and setter">
  public Type getType() {
    return this.type;
  }

  int getValue() {
    return this.value;
  }
  //</editor-fold>

  /**
   * Setzt die Statistik zurueck und zahlt das Geld auf ein Konto ein
   *
   * @param survivalPlayer Spieler der Statistik
   */
  public abstract void update(SurvivalPlayer survivalPlayer);

  /**
   * Setzt die Statistik zurueck und zahlt das Geld auf ein Konto ein
   */
  public abstract float getMoney();

  void incrementValue(final int amount) {
    this.value += amount;
  }

  /**
   * Setzt den Wert der Statistik zurueck
   */
  public void resetValue() {
    this.value = 0;
  }

}
