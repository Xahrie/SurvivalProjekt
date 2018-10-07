package net.mmm.survival.player;

import net.mmm.survival.SurvivalData;
import net.mmm.survival.events.ChangedExpEvent;

import java.io.Serializable;

import org.bukkit.Bukkit;

/*
 * @author Suders
 * Date: 05.10.2018
 * Time: 01:41:39
 * Location: SurvivalProjekt
 */
public class LevelPlayer implements Serializable {

  /*
   * "transient" speichert nicht die Variabel ab2
   */
  private static final long serialVersionUID = -324162873104318794L;
  private Float exp;

  public LevelPlayer(final Float exp) {
    this.exp = exp != null ? exp : 100F;//Setzt die Exp
  }

  /*
   * @return Returnt den EXP Wert, nicht sein richtiges Level.
   * Bedeutet nicht eine Zahl zwischen 1-99 sondern eine über 100F
   */
  public Float getExp() {
    return this.exp;
  }

  /*
   * @return Returnt das aktuelle Level
   */
  public Integer getLevel() {
    return getLevel(exp);
  }

  /*
   * @param exp Aktuelle Exp
   * @return In der Schleife wird der aktuelle EXP Wert mit dem vorherigen Wert und den nächsten Wert verglichen,
   * sodass der letzte Wert kleiner sein muss und der nächste Wert größer sein muss.
   * Letzendlich erhält man dadurch das aktuelle Level.
   */
  private Integer getLevel(final Float exp) {
    float xp = 100F;
    Integer level = 1;
    for (float xp2 : SurvivalData.getInstance().getLevels().values()) {//Setzt das Level anhand der Exp | NICHT Getestet
      if (xp <= exp && xp2 > exp) {
        return level;
      }
      xp = xp2;
      level++;
    }
    return level;
  }

  /*
   * @return Returnt ob Level dem Spieler erfolgreich hinzugefügt wurden konnte
   * @param level Wie viel Level dem Spieler hinzugefügt werden soll
   */
  public boolean addExp(final float exp, final SurvivalPlayer survivalPlayer) {
    if (exp == 0F) return false;
    int level = getLevel();
    if (level == 99) {
      this.exp += exp;
      return true;
    }
    final Float oldExp = this.exp;
    this.exp += exp;
    Bukkit.getPluginManager().callEvent(new ChangedExpEvent(survivalPlayer, oldExp, this.exp, level, getLevel()));
    return true;
  }
}
