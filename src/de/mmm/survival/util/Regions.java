package de.mmm.survival.util;

import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.GlobalProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;

/**
 * Verwaltung der Regionen
 */
public class Regions {
  //Rueckgabe, wenn keine Region in Selektion definiert
  private static final String GLOBAL_REGION = "__global__";

  private static void checkRegionId(final String id, final boolean allowGlobal)
          throws CommandException {
    if (!ProtectedRegion.isValidId(id)) {
      throw new CommandException("The region name of '" + id + "' contains characters that are not allowed.");
    }
    if ((!allowGlobal) && (id.equalsIgnoreCase(GLOBAL_REGION))) {
      throw new CommandException("Sorry, you can't use __global__ here.");
    }
  }

  /**
   * Prueft ob eine Region existiert
   *
   * @param regionManager Regionenmanager
   * @param id            ID
   * @param allowGlobal   Ist Globaler Zugriff moeglich
   * @return Geschuetzte Region
   * @see com.sk89q.worldguard.protection.managers.RegionManager
   */
  public static ProtectedRegion checkExistingRegion(final RegionManager regionManager, final String id, final boolean allowGlobal) {
    try {
      checkRegionId(id, allowGlobal);
    } catch (final CommandException ex) {
      ex.printStackTrace();
    }
    ProtectedRegion region = regionManager.getRegion(id);
    if (region == null) {
      if (id.equalsIgnoreCase(GLOBAL_REGION)) {
        region = new GlobalProtectedRegion(id);
        regionManager.addRegion(region);
        return region;
      }
      return null;
    }

    return region;
  }

  /**
   * Prueft ob die Region an einer gewissen Position ist
   *
   * @param regionManager Regionenmanager
   * @param loc           Position
   * @return Geschuetzte Region
   * @see com.sk89q.worldguard.protection.managers.RegionManager
   */
  public static ProtectedRegion checkRegionLocationIn(final RegionManager regionManager, final Location loc) {
    final ApplicableRegionSet set = regionManager.getApplicableRegions(new Vector(loc.getX(), loc.getY(), loc.getZ()));
    //keine Region ausgewaehlt, dann direkt abbrechen
    if (set.size() == 0) {
      return null;
    }
    //mehr als eine Region ausgewaehlt
    if (set.size() > 1) {
      warningMoreRegionsSelected(set);
    }
    return set.iterator().next();
  }

  /**
   * Es wurden mehrere Regionen ausgewaehlt
   *
   * @param set ApplicableRegionSet
   * @see com.sk89q.worldguard.protection.ApplicableRegionSet
   */
  private static void warningMoreRegionsSelected(final ApplicableRegionSet set) {
    final StringBuilder builder = new StringBuilder();
    boolean first = true;

    for (final ProtectedRegion region : set) {
      if (!first) {
        builder.append(", ");
      }
      first = false;
      builder.append(region.getId());
    }

    try {
      throw new CommandException("You're standing in several regions (please pick one).\nYou're in: " + builder.toString());
    } catch (final CommandException ex) {
      ex.printStackTrace();
    }
  }

}
