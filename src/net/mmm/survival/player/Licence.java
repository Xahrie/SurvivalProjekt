package net.mmm.survival.player;

import net.mmm.survival.util.SurvivalWorld;

public enum Licence {
	
	NETHERLIZENZ(1),
	ENDLIZENZ(2);
	
  /*
   * @param id LizenzID
   *
   */
  private final Integer id;
  
  /*
   * 
   * @param id LizenzID
   */
	Licence(final Integer id) {
	  this.id = id;
	}
	
	/*
	 * Die ID ist Wichtig für die Abspeicherung der Lizenzen eines Spielers
	 * @return LizenzID
	 */
	public Integer getID() {
	  return id;
	}
	
	/*
	 * 
	 * @return Welten Name für die LizenzID
	 */
	public String getWorldName() {
	  return id == 1 ? SurvivalWorld.NETHER.name() : id == 2 ? SurvivalWorld.END.name() : SurvivalWorld.BAUWELT.name();
	}
	
	/*
	 * @param sworld SurvivalWorld
	 * @return Returnt die Lizenz zur zugehörigen Welt
	 */
	public static Licence getLicence(final SurvivalWorld sworld) {
	  return sworld == SurvivalWorld.NETHER ? NETHERLIZENZ : sworld == SurvivalWorld.END ? ENDLIZENZ : null;
	}
	
}
