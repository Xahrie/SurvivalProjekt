package net.mmm.survival.farming.statistics;

import java.util.Arrays;
import java.util.List;

import net.mmm.survival.farming.FarmingKonst;
import net.mmm.survival.farming.Type;
import net.mmm.survival.player.SurvivalPlayer;
import org.bukkit.entity.Player;

/**
 * WalkLength trackt die Laenge, die ein Spieler (nicht in der Bauwelt) laeuft.
 *
 * @author Abgie on 28.09.18 12:00
 * project SurvivalProjekt
 * @version 1.0
 * @since JDK 8
 */
public final class WalkLength extends Statistic {
  private int oldLength;
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
    super(Type.WALK_LENGTH_CM);
  }

  /**
   * Setzt die Statistik zurueck und zahlt das Geld auf ein Konto ein
   *
   * @param target Spieler der Statistik
   */
  @Override
  public void update(final SurvivalPlayer target) {
    int lengthInCm = 0;
    for (final org.bukkit.Statistic statistic : statistics) {
      final Player targetPlayer = target.getPlayer();
      lengthInCm += targetPlayer.getStatistic(statistic);
    }
    final int difference = lengthInCm - oldLength;
    incrementValue(difference);
    oldLength = lengthInCm;
  }

  /**
   * Setzt die Statistik zurueck und zahlt das Geld auf ein Konto ein
   */
  @Override
  public float getMoney() {
    return getValue() / 100F * FarmingKonst.MONEY_PER_METER;
  }

}
