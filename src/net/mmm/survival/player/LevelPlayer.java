package net.mmm.survival.player;

import net.mmm.survival.SurvivalData;

/* 
 * @author Suders
 * Date: 05.10.2018
 * Time: 01:41:39
 * Location: SurvivalProjekt 
*/

public class LevelPlayer {
  
  private Float exp;
  private Integer level;
  
  public LevelPlayer(final Float exp) {
    this.exp = exp != null ? exp : 100F;//Setzt die Exp
    float xp = 100F;
    Integer level = 1;
    for(float xp2 : SurvivalData.getInstance().getLevels().values()) {//Setzt das Level an Hand der Exp | NICHT Getestet
      if(xp <= exp && xp2 > exp) {
        this.level = level;
        break;
      }
      xp = xp2;
      level++;
    }
  }
  
  /*
   * @return Returnt den EXP Wert, nicht sein richtiges Level.
   * Bedeutet nicht eine Zahl zwischen 1-99 sondern eine über 100F
   */
  public Float getExp() {
    return this.exp;
  }
  
  public Integer getLevel() {
    return this.level;
  }
  
  /*
   * @return Returnt ob Level dem Spieler erfolgreich hinzugefügt wurden konnte
   * @param level Wie viel Level dem Spieler hinzugefügt werden soll
   */
  public boolean addExp(final Float exp) {
    if(exp == 0F) return false;
    this.exp += exp;
    return true;
  }
}
