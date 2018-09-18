package net.mmm.survival.dynmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.mmm.survival.Survival;
import net.mmm.survival.SurvivalData;
import org.bukkit.World;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.GenericMarker;

/**
 * UpdateJob verwaltet eine spezifische Region
 *
 * @see java.lang.Runnable
 */
class UpdateJob implements Runnable {
  private List<ProtectedRegion> regionsToDo = null;
  private List<World> worldsToDo = null;
  private final Map<String, AreaMarker> newmap = new HashMap<>(); /* Build new map */
  private World curworld = null;

  @Override
  @SuppressWarnings("deprecation")
  public void run() {
    if (SurvivalData.getInstance().getDynmap().isStop()) return;
    if (setupWorld(SurvivalData.getInstance().getDynmap())) return;
    limitRegions(SurvivalData.getInstance().getDynmap());
    Survival.getInstance().getServer().getScheduler().scheduleAsyncDelayedTask(Survival.getInstance(), this, 1L);
  }

  private void limitRegions(final DynmapWorldGuardPlugin dynmap) {
    for (int i = 0; i < 20; i++) {
      if (regionsToDo.isEmpty()) {
        regionsToDo = null;
        break;
      }

      final ProtectedRegion region = regionsToDo.remove(regionsToDo.size() - 1);
      int depth = 1;
      ProtectedRegion p = region;

      while (p.getParent() != null) {
        depth++;
        p = p.getParent();
      }

      if (depth > 16) continue;

      dynmap.handleRegion(curworld, region, newmap);
    }
  }

  private boolean setupWorld(final DynmapWorldGuardPlugin dynmap) {
    if (worldsToDo == null) worldsToDo = new ArrayList<>(Survival.getInstance().getServer().getWorlds());
    while (regionsToDo == null) if (noMoreWorlds(dynmap)) return true;

    return false;
  }

  @SuppressWarnings("deprecation")
  private boolean noMoreWorlds(final DynmapWorldGuardPlugin dynmap) {
    if (worldsToDo.isEmpty()) {
      dynmap.getResareas().values().forEach(GenericMarker::deleteMarker);  /* review old map*/
      dynmap.setResareas(newmap); /* And replace with new map */
      Survival.getInstance().getServer().getScheduler().scheduleAsyncDelayedTask(Survival.getInstance(), this, dynmap.getUpdperiod());

      return true;
    } else {
      curworld = worldsToDo.remove(0);
      final RegionManager regionManager = dynmap.getRegion();
      final Map<String, ProtectedRegion> regions = (regionManager != null) ? regionManager.getRegions() : null;  /* Get all the regions */

      if (regions != null && !regions.isEmpty()) regionsToDo = new ArrayList<>(regions.values());
    }

    return false;
  }

}
