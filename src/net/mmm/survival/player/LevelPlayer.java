package net.mmm.survival.player;

import java.util.UUID;

import org.bukkit.Bukkit;

import net.mmm.survival.SurvivalData;
import net.mmm.survival.events.ChangedExpEvent;

/* 
 * @author Suders
 * Date: 05.10.2018
 * Time: 01:41:39
 * Location: SurvivalProjekt 
*/

public class LevelPlayer {
  
  private Float exp;
  private Float nextExpLevel;
  private Integer level;
  private final UUID uuid;
  
  public LevelPlayer(final UUID uuid, final Float exp) {
    this.exp = exp != null ? exp : 100F;//Setzt die Exp
    this.uuid = uuid;//Setzt die Spieler UUID
    this.level = getLevel(exp);//Setzt das aktuelle Level
    this.nextExpLevel = getNextLevelExp(exp);//Setzt die Exp für das nächste Level
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
   * @param exp Aktuelle Exp
   * @return In der Schleife wird der aktuelle EXP Wert mit dem vorherigen Wert und den nächsten Wert verglichen,
   * sodass der letzte Wert kleiner sein muss und der nächste Wert größer sein muss.
   * Letzendlich erhält man dadurch das aktuelle Level.
   */
  private Integer getLevel(final Float exp) {
    float xp = 100F;
    Integer level = 1;
    for(float xp2 : SurvivalData.getInstance().getLevels().values()) {//Setzt das Level an Hand der Exp | NICHT Getestet
      if(xp <= exp && xp2 > exp) {
        return level;
      }
      xp = xp2;
      level++;
    }
    return level;
  }

  private Float getNextLevelExp(final Float exp) {
    float xp = 100F;
    for(float xp2 : SurvivalData.getInstance().getLevels().values()) {
      if(xp <= exp && xp2 > exp) {
        return xp2;
      }
      xp = xp2;
    }
    return null;
  }
  
  /*
   * @return Returnt ob Level dem Spieler erfolgreich hinzugefügt wurden konnte
   * @param level Wie viel Level dem Spieler hinzugefügt werden soll
   */
  public boolean addExp(final Float exp) {
    if(exp == 0F) return false;
    if(this.level == 99) {
      this.exp += exp;
      return true;
    }
    final Float oldExp = this.exp;
    final Integer oldLevel = this.level;
    Float newExp = this.exp + exp;
    if(newExp >= nextExpLevel) {
      this.nextExpLevel = getNextLevelExp(newExp);
      this.level++;
    }
    this.exp = newExp;
    Bukkit.getPluginManager().callEvent(new ChangedExpEvent(SurvivalPlayer.findSurvivalPlayer(Bukkit.getPlayer(uuid)), oldExp, this.exp, oldLevel, this.level));
    return true;
  }
}
