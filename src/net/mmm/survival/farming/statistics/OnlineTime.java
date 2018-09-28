package net.mmm.survival.farming.statistics;

import java.util.HashSet;
import java.util.Set;

import net.mmm.survival.farming.Farming;
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
  private Set<Long> activeMinutes;

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
  public OnlineTime(int value) {
    super(Type.ONLINE_TIME, value);
    this.activeMinutes = new HashSet<>();
  }


  @Override
  public void calculate(Object... objects) {
    this.activeMinutes.add((Long) objects[0]);
  }

  @Override
  public void update(SurvivalPlayer survivalPlayer) {
    incrementValue(this.activeMinutes.size());
    this.activeMinutes.clear();
  }

  /**
   * Setzt die Statistik zurueck und zahlt das Geld auf ein Konto ein
   */
  @Override
  public float getMoney() {
    return getValue() * Farming.MONEY_PER_ACTIVE_MINUTE;
  }
}
