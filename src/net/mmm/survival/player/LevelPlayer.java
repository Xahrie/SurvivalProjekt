package net.mmm.survival.player;

import java.util.UUID;

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
  private final UUID uuid;
  
  public LevelPlayer(final UUID uuid, final Float exp) {
    this.exp = exp != null ? exp : 100F;//Setzt die Exp
    this.uuid = uuid;
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
  
  /*
   * @return Returnt das aktuelle Level
   */
  public Integer getLevel() {
    return this.level;
  }
  
  public UUID getUUID() {
    return this.uuid;
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
