package net.mmm.survival.regions;

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
  private static final String GLOBAL_REGION = "__global__";   // Rueckgabe, wenn keine Region in Selektion definiert

  private static void checkRegionId(final String id, final boolean allowGlobal) throws CommandException {
    checkValid(id); // Ueberpruefe, ob Name erlaubt ist
    checkGlobal(id, allowGlobal); // Ueberpruefe ob Region global ist
  }

  private static void checkValid(final String id) throws CommandException {
    if (!ProtectedRegion.isValidId(id)) {
      throw new CommandException("The region name of '" + id +
          "' contains characters that are not allowed.");
    }
  }

  private static void checkGlobal(final String id, final boolean allowGlobal) throws CommandException {
    if ((!allowGlobal) && (id.equalsIgnoreCase(GLOBAL_REGION))) {
      throw new CommandException("Sorry, you can't use __global__ here.");
    }
  }

  /**
   * Prueft ob eine Region existiert
   *
   * @param regionManager Regionenmanager
   * @param id ID
   * @param allowGlobal Ist Globaler Zugriff moeglich
   * @return Geschuetzte Region
   * @see com.sk89q.worldguard.protection.managers.RegionManager
   */
  public static ProtectedRegion checkExistingRegion(final RegionManager regionManager, final String id, final boolean allowGlobal) {
    try {
      checkRegionId(id, allowGlobal);
    } catch (final CommandException ex) {
      ex.printStackTrace();
    }
    return determineRegion(regionManager, id);
  }

  private static ProtectedRegion determineRegion(final RegionManager regionManager, final String id) {
    final ProtectedRegion region = regionManager.getRegion(id);
    if (region == null) {
      return noRegionFound(regionManager, id);
    }
    return region;
  }

  private static ProtectedRegion noRegionFound(final RegionManager regionManager, final String id) {
    final ProtectedRegion region;
    if (id.equalsIgnoreCase(GLOBAL_REGION)) { // Keine Region gefunden -> Global
      region = new GlobalProtectedRegion(id);
      regionManager.addRegion(region);
      return region;
    }
    return null;
  }

  /**
   * Prueft ob die Region an einer gewissen Position ist
   *
   * @param regionManager Regionenmanager
   * @param loc Position
   * @return Geschuetzte Region
   * @see com.sk89q.worldguard.protection.managers.RegionManager
   */
  public static ProtectedRegion checkRegionLocationIn(final RegionManager regionManager, final Location loc) {
    final ApplicableRegionSet set = regionManager
        .getApplicableRegions(new Vector(loc.getX(), loc.getY(), loc.getZ()));

    if (set.size() == 0) { // keine Region ausgewaehlt, dann direkt abbrechen
      return null;
    } else if (set.size() > 1) {  // mehr als eine Region ausgewaehlt
      warningMoreRegionsSelected(set);
    }
    return set.iterator().next();
  }

  private static void warningMoreRegionsSelected(final ApplicableRegionSet set) {
    final StringBuilder builder = new StringBuilder();
    set.forEach(region -> builder.append(region.getId()).append(", "));
    builder.deleteCharAt(builder.length() - 1);

    try {
      throw new CommandException("You're standing in several regions (please pick one).\nYou're in: " +
          builder.toString());
    } catch (final CommandException ex) {
      ex.printStackTrace();
    }
  }

}
