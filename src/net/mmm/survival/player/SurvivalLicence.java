package net.mmm.survival.player;

import net.mmm.survival.util.SurvivalWorld;

public enum SurvivalLicence {
  
  NETHERLIZENZ(500D),
  ENDLIZENZ(750D);

  private final Double price;

  SurvivalLicence(final Double price) {
    this.price = price;
  }

  /**
   * @param sworld SurvivalWorld
   * @return Returnt die Lizenz zur zugehoerigen Welt
   */
  public static SurvivalLicence getLicence(final SurvivalWorld sworld) {
    if (sworld == SurvivalWorld.NETHER) {
      return NETHERLIZENZ;
    } else if (sworld == SurvivalWorld.END) {
      return ENDLIZENZ;
    }
    return null;
  }

  public Double getPrice() {
    return this.price;
  }
  
}
