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

  /**
   * Konstruktor
   *
   * @param type Typ der Statistik
   * @param value Wert der Statistik
   */
  Statistic(final Type type, final int value) {
    this.type = type;
    this.value = value;
  }

  /**
   * Erhoeht den Wert der Statistik um dem Faktor {@code amount}
   *
   * @param amount Faktor der Erhöhung
   */
  void incrementValue(final int amount) {
    this.value += amount;
  }

  /**
   * Vermindert den Wert der Statistik um den Faktor {@code amount}
   *
   * @param amount Faktor der Verminderung
   */
  void decrementValue(final int amount) {
    this.value -= amount;
  }

  /**
   * Setzt den Wert der Statistik zurueck
   */
  public void resetValue() {
    setValue(0);
  }

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

  //<editor-fold desc="getter and setter">

  public Type getType() {
    return this.type;
  }

  Integer getValue() {
    return this.value;
  }

  private void setValue(final int value) {
    this.value = value;
  }

  //</editor-fold>
}
