package net.mmm.survival.player;

import net.mmm.survival.util.SurvivalWorld;

public enum SurvivalLicence {
  
<<<<<<< HEAD
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

=======
  NETHERLIZENZ(1, 500D),
  ENDLIZENZ(2, 750D);

  private final Integer id;
  private final Double price;

  SurvivalLicence(final Integer id, final Double price) {
    this.id = id;
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

  public Integer getID() {
    return id;
  }
  
>>>>>>> refs/remotes/origin/master
  public Double getPrice() {
    return this.price;
  }
  
}
