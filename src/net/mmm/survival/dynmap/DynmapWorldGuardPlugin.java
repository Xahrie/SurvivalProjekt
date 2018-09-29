package net.mmm.survival.dynmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.domains.PlayerDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionType;
import net.mmm.survival.Survival;
import net.mmm.survival.SurvivalData;
import net.mmm.survival.util.SurvivalWorld;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.GenericMarker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;


public class DynmapWorldGuardPlugin {
  private static final String DEF_INFOWINDOW = "<div class=\"infowindow\"><span style=\"font-size:120%;\"><center>%a</center></span><br /> " +
      "%b<span style=\"font-weight:bold;\">%c</span>%d</div>";

  private boolean reload = false, stop, use3d;
  private int maxdepth, updatesPerTick = 20;
  private long updperiod;
  private AreaStyle defstyle;
  private Map<String, AreaMarker> resareas = new HashMap<>();
  private Map<String, AreaStyle> cusstyle, cuswildstyle, ownerstyle;
  private MarkerSet set;
  private Plugin dynmap;
  private RegionManager regionManager;
  private Set<String> hidden, visible;
  private String infowindow;
  private WorldGuardPlugin wg;

  /**
   * Konstruktor
   */
  public DynmapWorldGuardPlugin() {
    try {
      this.regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(SurvivalWorld.BAUWELT.get()));
    } catch (final NullPointerException ex) {
      System.err.println("Platform is Null");
    }
  }

  private static void severe(final String msg) {
    System.out.println(msg);
  }

  private String formatInfoWindow(final ProtectedRegion region, final AreaMarker marker) {
    final StringBuilder owner = determineOwner(region);
    String format = "<div class=\"regioninfo\">" + this.infowindow + "</div>";

    format = checkAreaMarkerAndstyleWindow(region, marker, format, owner).replace("%a", "")
        .replace("%b", "")
        .replace("%c", "")
        .replace("%d", "");

    return format;
  }

  private StringBuilder determineOwner(final ProtectedRegion region) {
    final StringBuilder owner = new StringBuilder();
    region.getOwners().getUniqueIds().forEach(uuid -> owner.append(", ").append(SurvivalData.getInstance().getAsyncMySQL().getName(uuid)));
    owner.replace(0, 2, "");
    return owner;
  }

  private String checkAreaMarkerAndstyleWindow(final ProtectedRegion region, final AreaMarker marker, final String format,
                                               final StringBuilder owner) {
    return doesLabelStartsWithP(marker, format, owner, region);
  }

  private String doesLabelStartsWithP(final AreaMarker marker, String format, final StringBuilder owner, final ProtectedRegion region) {
    if (marker.getLabel().startsWith("P-")) {
      format = format.replace("%a", "<center>" + marker.getLabel() + "</center>")
          .replace("%c", "<center>Projekt-Zone</center>")
          .replace("%d", "<br />Projektleiter:<br /><span style=\"font-weight:bold;\">" + owner + "</span>");
    } else {
      format = checkUnowned(region, marker, format, owner);
    }

    return format;
  }

  private String checkUnowned(final ProtectedRegion region, final AreaMarker marker, final String format, final StringBuilder owner) {
    return isUnowned(region) ? format.replace("%a", marker.getLabel()).replace("%c", "<center>Server-Zone</center>")
        : modifyFormat(region, marker, format, owner);
  }

  private boolean isUnowned(final ProtectedRegion region) {
    return (region.getOwners().getPlayers().size() == 0) && (region.getOwners().getUniqueIds().size() == 0) && (region.getOwners().getGroups()
        .size() == 0);
  }

  private String modifyFormat(final ProtectedRegion region, final AreaMarker marker, String format, final StringBuilder owner) {
    format = checkNoOwnersFound(format, owner);
    format = format.replace("%a", SurvivalData.getInstance().getAsyncMySQL().getName(UUID.fromString(marker.getLabel().toLowerCase())))
        .replace("%b", "Owner: ")
        .replace("%groupowners%", region.getOwners().toGroupsString())
        .replace("%groupmembers%", region.getMembers().toGroupsString())
        .replace("*", "");

    return format;
  }

  private String checkNoOwnersFound(final String format, final StringBuilder owner) {
    return owner.length() == 0 ? format.replace("%c", "") : format;
  }

  private boolean isVisibleOrHidden(final String id, final String worldname) {
    return (!isVisible(id, worldname)) && (!isHidden() || !this.hidden.contains(id) && !this.hidden.contains("world:" + worldname) &&
        !this.hidden.contains(worldname + "/" + id));
  }

  private boolean isHidden() {
    return (this.hidden != null) && (this.hidden.size() > 0);
  }

  private boolean isVisible(final String id, final String worldname) {
    return ((this.visible != null) && (this.visible.size() > 0)) && ((!this.visible.contains(id)) && (!this.visible.contains("world:" +
        worldname)) && (!this.visible.contains(worldname + "/" + id)));
  }

  private void addStyle(final String resid, final String worldId, final AreaMarker areaMarker, final ProtectedRegion region) {
    AreaStyle as = this.cusstyle.get(worldId + "/" + resid);
    if (as == null) {
      as = this.cusstyle.get(resid);
    }
    as = checkWildcardStyleMatches(resid, as);
    as = checkOwnerStyleMatches(region, as);
    if (as == null)
      as = this.defstyle;

    if (region.getId().startsWith("p-")) {
      areaMarker.setLineStyle(as.strokeweight, as.strokeopacity, 0x00ff00);
      areaMarker.setFillStyle(as.fillopacity, 0x00ff00);
    } else if (isUnowned(region)) {
      areaMarker.setLineStyle(as.strokeweight, as.strokeopacity, 0xffcc00);
      areaMarker.setFillStyle(as.fillopacity, 0xffcc00);
    } else {
      areaMarker.setLineStyle(as.strokeweight, as.strokeopacity, 0xff0000);
      areaMarker.setFillStyle(as.fillopacity, 0xff0000);
    }

    if (as.label != null) {
      areaMarker.setLabel(as.label);
      //TODO (Abgie) 21.09.2018: WAS SOLL DAS BRINGEN? // REFACTORING
    }
  }

  private AreaStyle checkWildcardStyleMatches(final String resid, AreaStyle areaStyle) {
    if (areaStyle == null) {
      for (final String wc : cuswildstyle.keySet()) {
        final String[] tok = wc.split("\\|");
        if (((tok.length == 1) && resid.startsWith(tok[0])) || ((tok.length >= 2) && resid.startsWith(tok[0]) && resid.endsWith(tok[1]))) {
          areaStyle = cuswildstyle.get(wc);
        }
      }

    }
    return areaStyle;
  }

  private AreaStyle checkOwnerStyleMatches(final ProtectedRegion region, AreaStyle areaStyle) {
    if (areaStyle == null) {
      areaStyle = doesOwnerstyleExist(region, areaStyle);
    }
    return areaStyle;
  }

  private AreaStyle doesOwnerstyleExist(final ProtectedRegion region, AreaStyle areaStyle) {
    if (!ownerstyle.isEmpty()) {
      final DefaultDomain defaultDomain = region.getOwners();
      final PlayerDomain playerDomain = defaultDomain.getPlayerDomain();

      areaStyle = checkPlayerDomain(areaStyle, playerDomain);
      if (areaStyle == null) {
        areaStyle = checkGroups(areaStyle, defaultDomain);
      }
    }
    return areaStyle;
  }

  private AreaStyle checkGroups(AreaStyle areaStyle, final DefaultDomain defaultDomain) {
    final Set<String> grp = defaultDomain.getGroups();
    if (grp != null) {
      for (final String p : grp) {
        areaStyle = ownerstyle.get(p.toLowerCase());
        if (areaStyle != null) break;
      }
    }

    return areaStyle;
  }

  private AreaStyle checkPlayerDomain(AreaStyle areaStyle, final PlayerDomain playerDomain) {
    if (playerDomain != null) {
      for (final String p : playerDomain.getPlayers()) {
        if (areaStyle != null) break;
        areaStyle = ownerstyle.get(p.toLowerCase());
      }
      for (final UUID uuid : playerDomain.getUniqueIds()) {
        if (areaStyle != null) break;
        areaStyle = ownerstyle.get(uuid.toString());
      }
      for (final UUID uuid : playerDomain.getUniqueIds()) {
        if (areaStyle != null) break;
        final String p = SurvivalData.getInstance().getAsyncMySQL().getName(uuid);
        if (p != null) {
          areaStyle = ownerstyle.get(p.toLowerCase());
        }
      }
    }

    return areaStyle;
  }

  private void handleRegion(final World world, final ProtectedRegion region, final Map<String, AreaMarker> newmap) {
    String name = region.getId();
    name = name.substring(0, 1).toUpperCase() + name.substring(1); /* Make first letter uppercase */
    handleAreas(world, region, newmap, name);
  }

  private void handleAreas(final World world, final ProtectedRegion region, final Map<String, AreaMarker> newmap, final String name) {
    if (isVisibleOrHidden(region.getId(), world.getName()) && (!isCuboid(region.getType(), region.getMinimumPoint(), region.getMaximumPoint())
        && region.getType() == RegionType.POLYGON)) {
      checkVisible(world, region, newmap, name);
    }
  }

  private void checkVisible(final World world, final ProtectedRegion region, final Map<String, AreaMarker> newmap, final String name) {
    final List<BlockVector2D> points = region.getPoints();
    final double[] x = new double[points.size()], z = new double[points.size()];

    for (int i = 0; i < points.size(); i++) {
      final BlockVector2D pt = points.get(i);
      x[i] = pt.getX();
      z[i] = pt.getZ();
    }

    addArea(world, region, newmap, name, x, z);
  }

  private void addArea(final World world, final ProtectedRegion region, final Map<String, AreaMarker> newmap, final String name,
                       final double[] x, final double[] z) {
    final AreaMarker areaMarker = doesAreaExist(world, name, x, z, world.getName() + "_" + region.getId());
    if (areaMarker != null) {
      check3D(region.getMinimumPoint(), region.getMaximumPoint(), areaMarker);
      addStyle(region.getId(), world.getName(), areaMarker, region); /* Set line and fill properties */
      buildPopup(region, areaMarker);
      newmap.put(world.getName() + "_" + region.getId(), areaMarker); /* Add to map */
    }
  }

  private void buildPopup(final ProtectedRegion region, final AreaMarker areaMarker) {
    final String desc = formatInfoWindow(region, areaMarker);
    areaMarker.setDescription(desc);
  }

  private void check3D(final BlockVector l0, final BlockVector l1, final AreaMarker areaMarker) {
    if (use3d) {
      areaMarker.setRangeY(l1.getY() + 1.0, l0.getY());
    }
  }

  private AreaMarker doesAreaExist(final World world, final String name, final double[] x, final double[] z, final String markerid) {
    AreaMarker m = resareas.remove(markerid); /* Existing area? */
    if (m == null) {
      m = set.createAreaMarker(markerid, name, false, world.getName(), x, z, false);
    } else {
      m.setCornerLocations(x, z); /* Replace corner locations */
      m.setLabel(name); /* Update label */
    }
    return m;
  }

  @SuppressWarnings("MismatchedReadAndWriteOfArray")
  private boolean isCuboid(final RegionType tn, final BlockVector blockVector, final BlockVector blockVector1) {
    final double[] x, z;
    if (tn == RegionType.CUBOID) { /* Make outline */
      x = new double[4];
      z = new double[4];
      x[1] = x[0] = blockVector.getX();
      x[3] = x[2] = blockVector1.getX() + 1.0;
      z[3] = z[0] = blockVector.getZ();
      z[2] = z[1] = blockVector1.getZ() + 1.0;
      return true;
    }
    return false;
  }

  /**
   * Wird bei der Aktivierung des Servers durchgefuehrt
   */
  public void onEnable() {
    final PluginManager pm = Survival.getInstance().getServer().getPluginManager();
    dynmap = pm.getPlugin("dynmap"); /* Get dynmap */
    checkDynmap(pm);
  }

  private void checkDynmap(final PluginManager pluginManager) {
    if (this.dynmap != null) {
      checkWorldGuard(pluginManager.getPlugin("WorldGuard"));
    } else {
      severe("Cannot find dynmap!");
    }
  }

  private void checkWorldGuard(final Plugin worldGuard) {
    if (worldGuard != null) {
      this.wg = (WorldGuardPlugin) worldGuard;
      Survival.getInstance().getServer().getPluginManager().registerEvents(new OurServerListener(), Survival.getInstance());
      if (this.dynmap.isEnabled() && this.wg.isEnabled()) /* If both enabled, activate */
        activate();
    } else {
      severe("Cannot find WorldGuard!");
    }
  }

  @SuppressWarnings("deprecation")
  private void activate() {
    final MarkerAPI markerapi = ((DynmapAPI) this.dynmap).getMarkerAPI(); /* Now, get markers API */
    if (markerapi != null) {
      final FileConfiguration configuration = loadConfiguration();
      addMarkerForMobs(markerapi, configuration);
      checkSet(configuration);
    } else {
      severe("Error loading dynmap marker API!");
    }
  }

  private FileConfiguration loadConfiguration() {
    if (reload) {
      Survival.getInstance().reloadConfig();
    } else {
      reload = true;
    }
    final FileConfiguration cfg = Survival.getInstance().getConfig();
    cfg.options().copyDefaults(true);   /* Load defaults, if needed */
    Survival.getInstance().saveConfig();  /* Save updates, if needed */
    return cfg;
  }

  private void checkSet(final FileConfiguration configuration) {
    if (this.set != null) {
      setupDynmap(configuration);
    } else {
      severe("Error creating marker set");
    }
  }

  @SuppressWarnings("deprecation")
  private void setupDynmap(final FileConfiguration configuration) {
    final int minzoom = configuration.getInt("layer.minzoom", 0);
    if (minzoom > 0)
      this.set.setMinZoom(minzoom);
    this.set.setLayerPriority(configuration.getInt("layer.layerprio", 10));
    this.set.setHideByDefault(configuration.getBoolean("layer.hidebydefault", false));
    this.use3d = configuration.getBoolean("use3dregions", false);
    this.infowindow = configuration.getString("infowindow", DEF_INFOWINDOW);
    this.maxdepth = configuration.getInt("maxdepth", 16);
    this.updatesPerTick = configuration.getInt("updates-per-tick");

    getStyleInformation(configuration);
    Survival.getInstance().getServer().getScheduler().scheduleAsyncDelayedTask(Survival.getInstance(), new UpdateJob(), 40L);
  }

  private void addMarkerForMobs(final MarkerAPI markerapi, final FileConfiguration configuration) {
    set = markerapi.getMarkerSet("worldguard.markerset");
    if (set == null) {
      set = markerapi.createMarkerSet("worldguard.markerset", configuration.getString("layer.name", "Zonen"), null, false);
    } else {
      set.setMarkerSetLabel(configuration.getString("layer.name", "Zonen"));
    }
  }

  private void getStyleInformation(final FileConfiguration cfg) {
    getCuststyleSection(cfg);

    int per = cfg.getInt("update.period", 5);
    if (per < 15) per = 15;
    updperiod = (long) (per * 20);
    stop = false;
  }

  private void getCuststyleSection(final FileConfiguration cfg) {
    final ConfigurationSection sect = cfg.getConfigurationSection("custstyle");
    if (sect != null) {
      final Set<String> ids = sect.getKeys(false);

      cusstyle = new HashMap<>();
      cuswildstyle = new HashMap<>();
      defstyle = new AreaStyle(cfg);
      for (final String id : ids) {
        if (id.indexOf('|') >= 0)
          cuswildstyle.put(id, new AreaStyle(cfg, "custstyle." + id, defstyle));
        else
          cusstyle.put(id, new AreaStyle(cfg, "custstyle." + id, defstyle));
      }
    }
    getOwnerstyleSection(cfg);
  }

  private void getOwnerstyleSection(final FileConfiguration configuration) {
    final ConfigurationSection sect = configuration.getConfigurationSection("ownerstyle");

    if (sect != null) {
      final Set<String> ids = sect.getKeys(false);
      ownerstyle = new HashMap<>();
      ids.forEach(id -> ownerstyle.put(id.toLowerCase(), new AreaStyle(configuration, "ownerstyle." + id, defstyle)));
    }
    readVisibleregions(configuration);
  }

  private void readVisibleregions(final FileConfiguration configuration) {
    final List<String> vis = configuration.getStringList("visibleregions");

    if (vis != null) {
      visible = new HashSet<>(vis);
    }
    readHiddenregions(configuration);
  }

  private void readHiddenregions(final FileConfiguration configuration) {
    final List<String> hid = configuration.getStringList("hiddenregions");

    if (hid != null) {
      hidden = new HashSet<>(hid);
    }
  }

  /**
   * Wird bei der Deaktivierung des Servers durchgefuehrt
   */
  public void onDisable() {
    if (set != null) {
      set.deleteMarkerSet();
      set = null;
    }
    resareas.clear();
    stop = true;
  }

  public RegionManager getRegionManager() {
    return regionManager;
  }


  private static class AreaStyle {

    final double fillopacity, strokeopacity;
    final int strokeweight;
    final String fillcolor, strokecolor, unownedstrokecolor;
    String label;

    AreaStyle(final FileConfiguration cfg, final String path, final AreaStyle def) {
      strokecolor = cfg.getString(path + ".strokeColor", def.strokecolor);
      unownedstrokecolor = cfg.getString(path + ".unownedStrokeColor", def.unownedstrokecolor);
      strokeopacity = cfg.getDouble(path + ".strokeOpacity", def.strokeopacity);
      strokeweight = cfg.getInt(path + ".strokeWeight", def.strokeweight);
      fillcolor = cfg.getString(path + ".fillColor", def.fillcolor);
      fillopacity = cfg.getDouble(path + ".fillOpacity", def.fillopacity);
      label = cfg.getString(path + ".label", null);
    }

    AreaStyle(final FileConfiguration cfg) {
      strokecolor = cfg.getString("regionstyle.strokeColor", "#FF0000");
      unownedstrokecolor = cfg.getString("regionstyle.unownedStrokeColor", "#00FF00");
      strokeopacity = cfg.getDouble("regionstyle.strokeOpacity", 0.8);
      strokeweight = cfg.getInt("regionstyle.strokeWeight", 3);
      fillcolor = cfg.getString("regionstyle.fillColor", "#FF0000");
      fillopacity = cfg.getDouble("regionstyle.fillOpacity", 0.35);
    }
  }

  /**
   * @see org.bukkit.event.Listener
   */
  private class OurServerListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPluginEnable(final PluginEnableEvent event) {
      final Plugin p = event.getPlugin();
      final String name = p.getDescription().getName();
      if (name.equals("dynmap") || name.equals("WorldGuard")) {
        if (dynmap.isEnabled() && wg.isEnabled())
          activate();
      }
    }
  }

  /**
   * Handle specific region
   *
   * @see java.lang.Runnable
   */
  private class UpdateJob implements Runnable {
    final Map<String, AreaMarker> newmap = new HashMap<>();
    List<ProtectedRegion> regionsToDo = null;
    List<World> worldsToDo = null;
    World curworld = null;

    @SuppressWarnings("deprecation")
    public void run() {
      if (!stop) {
        primeWorlds();
        checkPendingRegions();
      }
    }

    @SuppressWarnings("deprecation")
    private void checkPendingRegions() {
      while (regionsToDo == null) {
        if (!worldsToDo.isEmpty()) {
          proceedWorld();
        } else {
          noMoreWorlds();
          break;
        }
      }
    }

    @SuppressWarnings("deprecation")
    private void proceedWorld() {
      curworld = worldsToDo.remove(0);
      if (regionManager != null) {
        final Map<String, ProtectedRegion> regions = regionManager.getRegions();  /* Get all the regions */

        if (!regions.isEmpty()) {
          regionsToDo = new ArrayList<>(regions.values());
        }
      }
      limitRegions();
      Survival.getInstance().getServer().getScheduler().scheduleAsyncDelayedTask(Survival.getInstance(), this, 1L);
    }

    @SuppressWarnings("deprecation")
    private void noMoreWorlds() {
      resareas.values().forEach(GenericMarker::deleteMarker); /* Now, review old map - anything left is gone */
      resareas = newmap; /* And replace with new map */
      Survival.getInstance().getServer().getScheduler().scheduleAsyncDelayedTask(Survival.getInstance(), new UpdateJob(), updperiod);
    }

    private void primeWorlds() {
      if (worldsToDo == null) {
        worldsToDo = new ArrayList<>(Survival.getInstance().getServer().getWorlds());
      }
    }

    private void limitRegions() {
      for (int i = 0; i < updatesPerTick; i++) {
        if (regionsToDo.isEmpty()) {
          regionsToDo = null;
          break;
        }

        ProtectedRegion region = regionsToDo.remove(regionsToDo.size() - 1);
        int depth = 1;
        while (region.getParent() != null) {
          depth++;
          region = region.getParent();
        }
        if (depth > maxdepth)
          continue;
        handleRegion(curworld, region, newmap);
      }
    }

  }

}
