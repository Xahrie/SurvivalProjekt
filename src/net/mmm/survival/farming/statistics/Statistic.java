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
  private Type type;
  private int value;

  /**
   * Konstruktor
   *
   * @param type Typ der Statistik
   * @param value Wert der Statistik
   */
  Statistic(Type type, int value) {
    this.type = type;
    this.value = value;
  }

  /**
   * Erhoeht den Wert der Statistik um 1
   */
  public void incrementValue() {
    incrementValue(1);
  }

  /**
   * Erhoeht den Wert der Statistik um dem Faktor <code>amount</code>
   *
   * @param amount Faktor der Erhöhung
   */
  void incrementValue(int amount) {
    this.value += amount;
  }

  /**
   * Vermindert den Wert der Statistik um 1
   */
  public void decrementValue() {
    decrementValue(1);
  }


  /**
   * Vermindert den Wert der Statistik um den Faktor <code>amount</code>
   *
   * @param amount Faktor der Verminderung
   */
  void decrementValue(int amount) {
    this.value -= amount;
  }

  /**
   * Setzt den Wert der Statistik zurueck
   */
  public void resetValue() {
    setValue(0);
  }

  /**
   * Berechnet wie viel die Statistik wert ist
   */
  public abstract void calculate(Object... objects);

  /**
   * Setzt die Statistik zurueck und zahlt das Geld auf ein Konto ein
   */
  public abstract void update(SurvivalPlayer survivalPlayer);

  /**
   * Setzt die Statistik zurueck und zahlt das Geld auf ein Konto ein
   */
  public abstract float getMoney();
  //<editor-fold desc="getter and setter">

  public void setValue(int value) {
    this.value = value;
  }

  public Type getType() {
    return this.type;
  }

  public Integer getValue() {
    return this.value;
  }

  //</editor-fold>
}
