package net.mmm.survival.farming.statistics;

import java.util.Arrays;
import java.util.List;

import net.mmm.survival.farming.FarmingKonst;
import net.mmm.survival.farming.Type;
import net.mmm.survival.player.SurvivalPlayer;

/**
 * WalkLength: Beschreibung der Klasse moeglichst praezise, aber nicht zu lang.
 * Zeilenlaenge: 80
 *
 * @author Abgie on 28.09.18 12:00
 * project SurvivalProjekt
 * @version 1.0
 * @since JDK 8
 */
public class WalkLength extends Statistic {
  private final List<org.bukkit.Statistic> statistics = Arrays.asList(org.bukkit.Statistic.WALK_ON_WATER_ONE_CM,
      org.bukkit.Statistic.WALK_ONE_CM, org.bukkit.Statistic.WALK_UNDER_WATER_ONE_CM,
      org.bukkit.Statistic.SPRINT_ONE_CM, org.bukkit.Statistic.CROUCH_ONE_CM,
      org.bukkit.Statistic.HORSE_ONE_CM, org.bukkit.Statistic.AVIATE_ONE_CM,
      org.bukkit.Statistic.BOAT_ONE_CM, org.bukkit.Statistic.CLIMB_ONE_CM,
      org.bukkit.Statistic.FLY_ONE_CM, org.bukkit.Statistic.MINECART_ONE_CM,
      org.bukkit.Statistic.PIG_ONE_CM, org.bukkit.Statistic.SWIM_ONE_CM);

  /**
   * Konstruktor
   */
  public WalkLength() {
    this(0);
  }

  /**
   * Konstruktor
   *
   * @param value Wert der Statistik
   */
  private WalkLength(final int value) {
    super(Type.WALK_LENGTH_CM, value);
  }

  /**
   * Setzt die Statistik zurueck und zahlt das Geld auf ein Konto ein
   *
   * @param survivalPlayer Spieler der Statistik
   */
  @Override
  public void update(final SurvivalPlayer survivalPlayer) {
    int lengthInCm = 0;
    for (final org.bukkit.Statistic statistic : statistics) {
      lengthInCm += survivalPlayer.getPlayer().getStatistic(statistic);
    }
    final int actualLength = statistics.stream().mapToInt(statistic -> survivalPlayer.getPlayer().getStatistic(statistic)).sum();
    final int distance = actualLength - lengthInCm;
    incrementValue(distance);
  }

  /**
   * Setzt die Statistik zurueck und zahlt das Geld auf ein Konto ein
   */
  @Override
  public float getMoney() {
    return getValue() / 100F * FarmingKonst.MONEY_PER_METER;
  }

}
