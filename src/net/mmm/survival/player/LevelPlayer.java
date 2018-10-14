package net.mmm.survival.player;

import java.io.Serializable;

import net.mmm.survival.SurvivalData;
import net.mmm.survival.util.Konst;

/*
 * @author Suders
 * Date: 05.10.2018
 * Time: 01:41:39
 * Location: SurvivalProjekt
 */

//TODO (Abgie) 14.10.2018: Muss noch fertiggestellt werden
public class LevelPlayer implements Serializable {
  private static final long serialVersionUID = -324162873104318794L;
  /*
   * "transient" speichert nicht die Variabel ab2
   */
  private final transient Float exp;

  public LevelPlayer(final float exp) {
    this.exp = exp != 0 ? exp : 100F;//Setzt die Exp
  }

  public float getExp() {
    return this.exp;
  }

  public int getLevel() {
    return getLevel(exp);
  }

  /**
   * @param exp Aktuelle Exp
   * @return In der Schleife wird der aktuelle EXP Wert mit dem vorherigen Wert und den nächsten Wert verglichen,
   * sodass der letzte Wert kleiner sein muss und der nächste Wert größer sein muss.
   * Letzendlich erhält man dadurch das aktuelle Level.
   */
  public int getLevel(final float exp) {
    float xp = Konst.DEFAULT_EXPERIENCE;
    int level = 1;
    for (final float xp2 : SurvivalData.getInstance().getLevels().values()) { //Setzt das Level anhand der Exp | NICHT Getestet
      if (xp <= exp && xp2 > exp) {
        return level;
      }
      xp = xp2;
      level++;
    }
    return level;
  }
//
//  /**
//   * @return Returnt ob Level dem Spieler erfolgreich hinzugefügt wurden konnte
//   * @param level Wie viel Level dem Spieler hinzugefügt werden soll
//   */
//  public boolean addExp(final float exp, final SurvivalPlayer survivalPlayer) {
//    if (exp == 0F) return false;
//    final int level = getLevel();
//    if (level == 99) {
//      this.exp += exp;
//      return true;
//    }
//    final Float oldExp = this.exp;
//    this.exp += exp;
//    Bukkit.getPluginManager().callEvent(new ChangedExpEvent(survivalPlayer, oldExp, this.exp, level, getLevel()));
//    return true;
//  }

}