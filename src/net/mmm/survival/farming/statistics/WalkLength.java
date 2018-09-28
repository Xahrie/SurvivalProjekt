package net.mmm.survival.farming.statistics;

import net.mmm.survival.farming.Farming;
import net.mmm.survival.farming.Type;
import net.mmm.survival.player.SurvivalPlayer;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerMoveEvent;

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
  private Location lastLocation;

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
  public WalkLength(int value) {
    super(Type.WALK_LENGTH_CM, value);
  }

  /**
   * Berechnet wie viel die Statistik wert ist
   *
   * @param objects
   */
  @Override
  public void calculate(Object... objects) {
    PlayerMoveEvent event = (PlayerMoveEvent) objects[0];
    if (event.getPlayer().getWorld().getName().equals("farmwelt")) {
      if (event.getPlayer().isSprinting() && lastLocation == null) {
        this.lastLocation = event.getPlayer().getLocation();
      } else if (!event.getPlayer().isSprinting()) {

      }
    }
  }

  @Override
  public void update(SurvivalPlayer survivalPlayer) {
    Location currentLocation = survivalPlayer.getPlayer().getLocation();
    int distance = (int) this.lastLocation.distance(currentLocation);
    this.lastLocation = null;
    incrementValue(distance);
  }

  /**
   * Setzt die Statistik zurueck und zahlt das Geld auf ein Konto ein
   */
  @Override
  public float getMoney() {
    return getValue() / 100F * Farming.MONEY_PER_METER;
  }

}
