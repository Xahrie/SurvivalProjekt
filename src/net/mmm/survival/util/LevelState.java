package net.mmm.survival.util;

/*
 * @author Suders
 * Date: 05.10.2018
 * Time: 02:58:43
 * Location: SurvivalProjekt
 */

public enum LevelState {

  LEVEL_1_BETWEEN_20(0.07F),
  LEVEL_21_BETWEEN_40(0.09F),
  LEVEL_41_BETWEEN_70(0.07F),
  LEVEL_71_BETWEEN_90(0.05F),
  LEVEL_91_BETWEEN_99(0.01F);

  private final float factor;

  LevelState(final float factor) {
    this.factor = factor;
  }

  public float getFactor() {
    return this.factor;
  }

  public LevelState getLevelState(final Integer level) {
    if (checkbetweenNumbers(1, 20, level)) {
      return LEVEL_1_BETWEEN_20;
    } else if (checkbetweenNumbers(21, 40, level)) {
      return LEVEL_21_BETWEEN_40;
    } else if (checkbetweenNumbers(41, 70, level)) {
      return LEVEL_41_BETWEEN_70;
    } else if (checkbetweenNumbers(71, 90, level)) {
      return LEVEL_71_BETWEEN_90;
    } else if (checkbetweenNumbers(91, 99, level)) {
      return LEVEL_91_BETWEEN_99;
    }
    return null;
  }

  private boolean checkbetweenNumbers(final Integer tinyNr, final Integer bigNr, final Integer nr) {
    return nr >= tinyNr && bigNr >= nr;
  }
}
