package net.mmm.survival.player;

import net.mmm.survival.util.SurvivalWorld;

public enum Licence {
	
	NETHERLIZENZ(0),
	ENDLIZENZ(1),
	BAULIZENZ(2);
	
  /*
   *  @param id LizenzID
   *
   */
  private final Integer id;
  
  /*
   *  @param id LizenzID
   */
	Licence(final Integer id) {
	  this.id = id;
	}
	
	/*
	 *   Wichtig
	 *   @return LizenzID
	 */
	public Integer getID() {
	  return id;
	}
	
	/*
	 *   @return Welten Name für die LizenzID
	 */
	public String getWorldName() {
	  return id == 0 ? SurvivalWorld.NETHER.name() : id == 1 ? SurvivalWorld.END.name() : SurvivalWorld.BAUWELT.name();
	}
}
