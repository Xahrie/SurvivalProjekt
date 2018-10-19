package net.mmm.survival.farming.statistics;

import java.util.HashSet;
import java.util.Set;

import net.mmm.survival.farming.FarmingKonst;
import net.mmm.survival.farming.Type;
import net.mmm.survival.player.SurvivalPlayer;

/**
 * OnlineTime trackt die Onlinezeit, die ein Spieler nicht afk ist.
 *
 * @author Abgie on 28.09.18 11:36
 * project SurvivalProjekt
 * @version 1.0
 * @since JDK 8
 */
public class OnlineTime extends Statistic {
  private final Set<Long> addedMinutes = new HashSet<>(); //Pro Minute nur einmal Wert erhoehen

  /**
   * Konstruktor
   */
  public OnlineTime() {
    super(Type.ONLINE_TIME);
  }

  /**
   * Setzt die Statistik zurueck und zahlt das Geld auf ein Konto ein
   *
   * @param survivalPlayer Spieler der Statistik
   */
  @Override
  public void update(final SurvivalPlayer survivalPlayer) {
    final long currentTimeInMillis = System.currentTimeMillis() / 60_000;
    if (!addedMinutes.contains(currentTimeInMillis)) {
      incrementValue(1);
      addedMinutes.add(currentTimeInMillis);
    }
  }

  /**
   * Setzt die Statistik zurueck und zahlt das Geld auf ein Konto ein
   */
  @Override
  public float getMoney() {
    return getValue() * FarmingKonst.MONEY_PER_ACTIVE_MINUTE;
  }

}
