package net.mmm.survival.player;

import net.mmm.survival.util.SurvivalWorld;

public enum Licence {
	
	NETHERLIZENZ(0),
	ENDLIZENZ(1),
	BAULIZENZ(2);
	
  /*
   *  
   *
   */
  private final Integer id;
  
	Licence(final Integer id) {
	  this.id = id;
	}
	
	public Integer getID() {
	  return id;
	}
	
	public String getWorldName() {
	  return id == 0 ? SurvivalWorld.NETHER.name() : id == 1 ? SurvivalWorld.END.name() : SurvivalWorld.BAUWELT.name();
	}
}
