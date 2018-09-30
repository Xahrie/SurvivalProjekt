package net.mmm.survival.player;

import net.mmm.survival.util.SurvivalWorld;

public enum SurvivalLicense {
  NETHERLIZENZ(1),
  ENDLIZENZ(2);

  private final Integer id;

  SurvivalLicense(final Integer id) {
    this.id = id;
  }

  /**
   * @param sworld SurvivalWorld
   * @return Returnt die Lizenz zur zugehoerigen Welt
   */
  public static SurvivalLicense getLicence(final SurvivalWorld sworld) {
    if (sworld == SurvivalWorld.NETHER) {
      return NETHERLIZENZ;
    } else if (sworld == SurvivalWorld.END) {
      return ENDLIZENZ;
    }
    return null;
  }

  public Integer getID() {
    return id;
  }
}
