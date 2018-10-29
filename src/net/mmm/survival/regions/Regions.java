package net.mmm.survival.regions;

import java.util.StringJoiner;

import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.GlobalProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.mmm.survival.util.logger.Logger;
import org.bukkit.Location;

/**
 * Verwaltung der Regionen
 */
public final class Regions {
  private static final String GLOBAL_REGION = "__global__";   // Rueckgabe, wenn keine Region in Selektion definiert
  private static final Logger logger = new Logger(Regions.class.getName());

  /**
   * Prueft ob eine Region existiert
   *
   * @param regionManager Regionenmanager
   * @param id ID
   * @param allowGlobal Ist Globaler Zugriff moeglich
   * @return Geschuetzte Region
   * @see com.sk89q.worldguard.protection.managers.RegionManager
   */
  public static ProtectedRegion evaluateExistingRegion(final RegionManager regionManager, final String id, final boolean allowGlobal) {
    try {
      evaluateRegionId(id, allowGlobal);
    } catch (final CommandException ex) {
      ex.printStackTrace();
    }
    return determineRegion(regionManager, id);
  }

  private static void evaluateRegionId(final String id, final boolean allowGlobal) throws CommandException {
    evaluateValid(id); // Ueberpruefe, ob Name erlaubt ist
    evaluateGlobal(id, allowGlobal); // Ueberpruefe ob Region global ist
  }

  private static ProtectedRegion determineRegion(final RegionManager regionManager, final String id) {
    final ProtectedRegion region = regionManager.getRegion(id);
    if (region == null) {
      return noRegionFound(regionManager, id);
    }
    return region;
  }

  private static void evaluateValid(final String id) throws CommandException {
    if (!ProtectedRegion.isValidId(id)) {
      throw new CommandException("The region name of '" + id +
          "' contains characters that are not allowed.");
    }
  }

  private static void evaluateGlobal(final String id, final boolean allowGlobal) throws CommandException {
    if ((!allowGlobal) && (id.equalsIgnoreCase(GLOBAL_REGION))) {
      throw new CommandException("Sorry, you can't use __global__ here.");
    }
  }

  private static ProtectedRegion noRegionFound(final RegionManager regionManager, final String id) {
    final ProtectedRegion region;
    if (id.equalsIgnoreCase(GLOBAL_REGION)) { // Keine Region gefunden => Global
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
  public static ProtectedRegion evaluateRegionOnCurrentLocation(final RegionManager regionManager, final Location loc) {
    final ApplicableRegionSet set = regionManager.
        getApplicableRegions(new Vector(loc.getX(), loc.getY(), loc.getZ()));

    if (set.size() == 0) { // keine Region ausgewaehlt, dann direkt abbrechen
      return null;
    } else if (set.size() > 1) {  // mehr als eine Region ausgewaehlt
      warningMoreRegionsSelected(set);
    }
    return set.iterator().next();
  }

  private static void warningMoreRegionsSelected(final ApplicableRegionSet set) {
    final StringJoiner builder = new StringJoiner(",");
    for (final ProtectedRegion region : set) {
      builder.add(region.getId());
    }

    logger.warn(new CommandException("You're standing in several regions (please pick one).\nYou're in: " + builder));
  }

}
