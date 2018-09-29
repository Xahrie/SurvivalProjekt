package net.mmm.survival.farming.statistics;

import java.util.HashSet;
import java.util.Set;

import net.mmm.survival.farming.FarmingKonst;
import net.mmm.survival.farming.Type;
import net.mmm.survival.player.SurvivalPlayer;

/**
 * OnlineTime: Beschreibung der Klasse moeglichst praezise, aber nicht zu lang.
 * Zeilenlaenge: 80
 *
 * @author Abgie on 28.09.18 11:36
 * project SurvivalProjekt
 * @version 1.0
 * @since JDK 8
 */
public class OnlineTime extends Statistic {
  private final Set<Long> activeMinutes = new HashSet<>();

  /**
   * Konstruktor
   */
  public OnlineTime() {
    this(0);
  }

  /**
   * Konstruktor
   *
   * @param value Wert der Statistik
   */
  private OnlineTime(final int value) {
    super(Type.ONLINE_TIME, value);
  }

  /**
   * Berechnet wie viel die Statistik wert ist
   *
   * @param objects Parameter
   */
  @Override
  public void modify(final Object... objects) {
    this.activeMinutes.add((Long) objects[0]);
  }

  /**
   * Setzt die Statistik zurueck und zahlt das Geld auf ein Konto ein
   *
   * @param survivalPlayer Spieler der Statistik
   */
  @Override
  public void update(final SurvivalPlayer survivalPlayer) {
    incrementValue(this.activeMinutes.size() - 1);
    this.activeMinutes.clear();
  }

  /**
   * Setzt die Statistik zurueck und zahlt das Geld auf ein Konto ein
   */
  @Override
  public float getMoney() {
    return getValue() * FarmingKonst.MONEY_PER_ACTIVE_MINUTE;
  }

}
