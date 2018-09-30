package net.mmm.survival.player;

import net.mmm.survival.util.SurvivalWorld;

public enum Licence {
  NETHERLIZENZ(1),
  ENDLIZENZ(2);

  private final Integer id;

  Licence(final Integer id) {
    this.id = id;
  }

  /**
   * @param sworld SurvivalWorld
   * @return Returnt die Lizenz zur zugehoerigen Welt
   */
  public static Licence getLicence(final SurvivalWorld sworld) {
    return sworld == SurvivalWorld.NETHER ? NETHERLIZENZ : sworld == SurvivalWorld.END ? ENDLIZENZ : null;
  }

  public Integer getID() {
    return id;
  }

  /**
   * @return Welten Name fuer die LizenzID
   */
  public String getWorldName() {
    return id == 1 ? SurvivalWorld.NETHER.name() : id == 2 ? SurvivalWorld.END.name() : SurvivalWorld.BAUWELT.name();
  }

}
