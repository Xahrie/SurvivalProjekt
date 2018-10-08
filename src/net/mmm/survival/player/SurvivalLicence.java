package net.mmm.survival.player;

import net.mmm.survival.regions.SurvivalWorld;

public enum SurvivalLicence {

  NETHERLIZENZ(500D),
  ENDLIZENZ(750D);

  private final double price;

  SurvivalLicence(final double price) {
    this.price = price;
  }

  /**
   * @param survivalWorld SurvivalWorld
   * @return Returnt die Lizenz zur zugehoerigen Welt
   */
  public static SurvivalLicence getLicence(final SurvivalWorld survivalWorld) {
    if (survivalWorld.equals(SurvivalWorld.NETHER)) {
      return NETHERLIZENZ;
    } else if (survivalWorld.equals(SurvivalWorld.END)) {
      return ENDLIZENZ;
    }
    return null;
  }

  public double getPrice() {
    return this.price;
  }

}
