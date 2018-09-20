package net.mmm.survival.dynmap;

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
import com.sk89q.worldguard.protection.flags.BooleanFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionType;
import net.mmm.survival.Survival;
import net.mmm.survival.SurvivalData;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;

/**
 * Dynmap-WorldGuard Fix fuer 1.13
 */
public class DynmapWorldGuardPlugin {
  private static final String DEF_INFOWINDOW = "<div class=\"infowindow\"><span style=\"font-size:120%;\"><center>%a</center></span><br /> " +
      "%b<span style=\"font-weight:bold;\">%c</span>%d</div>";

  private boolean use3d, stop, reload = false;
  private long updperiod;
  private AreaStyle defstyle;
  private BooleanFlag boost_flag;
  private DynmapAPI api;
  private Map<String, AreaMarker> resareas = new HashMap<>();
  private Map<String, AreaStyle> cusstyle, cuswildstyle, ownerstyle;
  private MarkerSet set;
  private final RegionManager region;
  private Set<String> visible, hidden;
  private String infowindow;
  private WorldGuardPlugin worldGuard;

  /**
   * Konstruktor
   */
  public DynmapWorldGuardPlugin() {
    this.region = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(Bukkit.getWorld("world")));
  }

  private static void severe(final String msg) {
    System.err.println(msg);
  }

  private String formatInfoWindow(final ProtectedRegion region, final AreaMarker m) {
    String v = "<div class=\"regioninfo\">" + infowindow + "</div>";

    v = checkLabelname(region, m, v);
    v = v.replace("%a", "").replace("%b", "").replace("%c", "").replace("%d", "");

    return v;
  }

  private String checkLabelname(final ProtectedRegion region, final AreaMarker marker, String v) {
    if (marker.getLabel().startsWith("P-")) {
      v = v.replace("%a", "<center>" + marker.getLabel() + "</center>").replace("%c", "<center>Projekt-Zone</center>");

      v = appendOwner(region, v);

    } else {
      v = isUnowned(region, v, marker);
    }

    return v;
  }

  private String appendOwner(final ProtectedRegion region, String v) {
    final StringBuilder owner = new StringBuilder();
    region.getOwners().getUniqueIds().forEach(uuid -> owner.append(SurvivalData.getInstance().getAsyncMySQL().getName(uuid)).append(", "));
    v = v.replace("%d", "<br />Projektleiter:<br /><span style=\"font-weight:bold;\">" + owner + "</span>");
    return v;
  }

  private String isUnowned(final ProtectedRegion region, String v, final AreaMarker marker) {
    if (checkUnowned(region)) {
      v = v.replace("%a", marker.getLabel()).replace("%c", "<center>Server-Zone</center>");

    } else {
      final StringBuilder owner = new StringBuilder();
      region.getOwners().getUniqueIds().forEach(uuid -> owner.append(SurvivalData.getInstance().getAsyncMySQL().getName(uuid)).append(", "));

      v = v.replace("%a", SurvivalData.getInstance().getAsyncMySQL().getName(UUID.fromString(marker.getLabel().toLowerCase())))
          .replace("%b", "Owner: ").replace("%c", owner.toString()).replace("%groupowners%", region.getOwners().toGroupsString())
          .replace("%groupmembers%", region.getMembers().toGroupsString()).replace("*", "");
    }

    return v;
  }

  private boolean isVisible(final String id, final String worldname) {
    if (!visible.isEmpty() && (!visible.contains(id)) && (!visible.contains("world:" + worldname)) && (!visible.contains(worldname + "/" +
        id))) {
      return false;
    }

    return hidden.isEmpty() || (!hidden.contains(id) && !hidden.contains("world:" + worldname) && !hidden.contains(worldname + "/" + id));
  }

  private void addStyle(final String resid, final String worldid, final AreaMarker m, final ProtectedRegion region) {
    AreaStyle style = cusstyle.get(worldid + "/" + resid);
    style = determineStyle(resid, style);

    checkUnowned(region);
    checkRegionId(m, region, style);
    determineLabel(m, style);
    determineBoostFlag(m, region);

  }

  private void determineLabel(final AreaMarker m, final AreaStyle style) {
    if (style.getLabel() != null) {
      m.setLabel(style.getLabel());
    }
  }

  private void determineBoostFlag(final AreaMarker m, final ProtectedRegion region) {
    if (boost_flag != null) {
      final Boolean b = region.getFlag(boost_flag);
      m.setBoostFlag((b != null) && b);
    }
  }

  private AreaStyle determineStyle(final String resid, AreaStyle style) {
    style = (style == null) ? cusstyle.get(resid) : null;

    style = checkWildcardStyle(resid, style);

    style = checkOwnerStyle(style);

    style = (style == null) ? defstyle : null;

    return style;
  }

  private AreaStyle checkOwnerStyle(final AreaStyle style) {
    return style;
  }

  private AreaStyle checkWildcardStyle(final String resid, AreaStyle style) {
    if (style == null) {
      for (final String wc : cuswildstyle.keySet()) {
        final String[] tok = wc.split("\\|");

        if (((tok.length == 1) && resid.startsWith(tok[0])) || ((tok.length >= 2) && resid.startsWith(tok[0]) && resid.endsWith(tok[1]))) {
          style = cuswildstyle.get(wc);
        }
      }
    }
    return style;
  }

  private void checkRegionId(final AreaMarker m, final ProtectedRegion region, final AreaStyle as) {
    if (region.getId().startsWith("p-")) {
      m.setLineStyle(as.strokeweight, as.strokeopacity, 0x00ff00);
      m.setFillStyle(as.fillopacity, 0x00ff00);
    } else if (checkUnowned(region)) {
      m.setLineStyle(as.strokeweight, as.strokeopacity, 0xffcc00);
      m.setFillStyle(as.fillopacity, 0xffcc00);
    } else {
      m.setLineStyle(as.strokeweight, as.strokeopacity, 0xff0000);
      m.setFillStyle(as.fillopacity, 0xff0000);
    }
  }

  private boolean checkUnowned(final ProtectedRegion region) {
    return region.getOwners().getPlayers().isEmpty() && region.getOwners().getUniqueIds().isEmpty() && region.getOwners().getGroups().isEmpty();
  }

  /**
   * Verwalte die Region
   *
   * @param world Welt
   * @param region Region
   * @param newmap Karte
   * @see com.sk89q.worldguard.protection.regions.ProtectedRegion
   */
  void handleRegion(final World world, final ProtectedRegion region, final Map<String, AreaMarker> newmap) {
    String name = region.getId();
    name = name.substring(0, 1).toUpperCase() + name.substring(1); /* Make first letter uppercase */

    /* Handle areas */
    handleAreas(world, region, newmap, name);
  }

  private void handleAreas(final World world, final ProtectedRegion region, final Map<String, AreaMarker> newmap, final String name) {
    if (isVisible(region.getId(), world.getName())) {
      final RegionType tn = region.getType();
      final BlockVector l0 = region.getMinimumPoint();
      final BlockVector l1 = region.getMaximumPoint();
      final double[] x, z;
      if (tn == RegionType.CUBOID) { /* Cubiod region? */
        /* Make outline */
        x = new double[4];
        z = new double[4];
        x[0] = l0.getX();
        z[0] = l0.getZ();
        x[1] = l0.getX();
        z[1] = l1.getZ() + 1.0;
        x[2] = l1.getX() + 1.0;
        z[2] = l1.getZ() + 1.0;
        x[3] = l1.getX() + 1.0;
        z[3] = l0.getZ();
      } else if (tn == RegionType.POLYGON) {
        final ProtectedPolygonalRegion ppr = (ProtectedPolygonalRegion) region;
        final List<BlockVector2D> points = ppr.getPoints();
        x = new double[points.size()];
        z = new double[points.size()];
        for (int i = 0; i < points.size(); i++) {
          final BlockVector2D pt = points.get(i);
          x[i] = pt.getX();
          z[i] = pt.getZ();
        }
      } else {  /* Unsupported type */
        return;
      }
      final String markerid = world.getName() + "_" + region.getId();
      AreaMarker m = resareas.remove(markerid);
      m = doesAreaExist(world, name, x, z, markerid, m);
      check3d(region.getMinimumPoint(), region.getMaximumPoint(), m);
      addStyle(region.getId(), world.getName(), m, region);
      determinePopup(region, m);
      newmap.put(markerid, m); /* Add to map */
    }
  }

  private AreaMarker doesAreaExist(final World world, final String name, final double[] x, final double[] z, final String markerid,
                                   AreaMarker m) {
    if (m == null) {
      m = createAreaMarker(world, name, x, z, markerid);
    } else {
      replaceCornerLocation(x, z, m);
      m.setLabel(name);
    }
    return m;
  }

  private void check3d(final BlockVector minimumPoint, final BlockVector maximumPoint, final AreaMarker m) {
    if (use3d) {
      m.setRangeY(maximumPoint.getY() + 1.0, minimumPoint.getY());
    }
  }

  private void determinePopup(final ProtectedRegion region, final AreaMarker m) {
    final String desc = formatInfoWindow(region, m);
    m.setDescription(desc);
  }

  private AreaMarker createAreaMarker(final World world, final String name, final double[] x, final double[] z, final String markerid) {
    final AreaMarker m;
    m = set.createAreaMarker(markerid, name, false, world.getName(), x, z, false);
    return m;
  }

  private void replaceCornerLocation(final double[] x, final double[] z, final AreaMarker m) {
    m.setCornerLocations(x, z);
  }

  /**
   * Wird bei der Aktivierung des Servers durchgefuehrt
   */
  public void enable() {
    final PluginManager pluginManager = Survival.getInstance().getServer().getPluginManager();
    final Plugin dynmap = enablePlugin(pluginManager, "dynmap", "Cannot find dynmap!");
    if (dynmap == null) return;
    api = (DynmapAPI) dynmap;
    final Plugin plugin = enablePlugin(pluginManager, "WorldGuard", "Cannot find WorldGuard!");
    if (plugin == null) return;

    worldGuard = (WorldGuardPlugin) plugin;
    checkEnabled(dynmap);

  }

  private void checkEnabled(final Plugin dynmap) {
    if (dynmap.isEnabled() && worldGuard.isEnabled()) {
      activate();
    }
  }

  private Plugin enablePlugin(final PluginManager pluginManager, final String dynmap2, final String message) {
    final Plugin dynmap = pluginManager.getPlugin(dynmap2);

    if (dynmap == null) {
      severe(message);
      return null;
    }

    return dynmap;
  }

  /**
   * Aktivierung von Dynmap
   */
  @SuppressWarnings("deprecation")
  public void activate() {
    final MarkerAPI markerapi = activateMarkerAPI();
    if (markerapi == null) return;
    if (loadConfig(markerapi)) return;
    Survival.getInstance().getServer().getScheduler().scheduleAsyncDelayedTask(Survival.getInstance(), new UpdateJob(), 40L);  /* First time is 2 seconds */
  }

  private boolean loadConfig(final MarkerAPI markerapi) {
    reloadConfig();
    return setupConfig(markerapi);
  }

  private MarkerAPI activateMarkerAPI() {
    final MarkerAPI markerapi = api.getMarkerAPI(); /* Now, get markers API */
    if (markerapi == null) {
      severe("Error loading dynmap marker API!");
      return null;
    }
    return markerapi;
  }

  private boolean setupConfig(final MarkerAPI markerapi) {
    final FileConfiguration cfg = initConfig();

    set = markerapi.getMarkerSet("worldguard.markerset");

    if (set == null) {
      set = markerapi.createMarkerSet("worldguard.markerset", cfg.getString("layer.name", "Zonen"), null, false);
    } else {
      set.setMarkerSetLabel(cfg.getString("layer.name", "Zonen"));
    }

    if (set == null) {
      severe("Error creating marker set");
      return true;
    }

    loadDefaults(cfg);

    return false;
  }

  private void loadDefaults(final FileConfiguration cfg) {
    final int minzoom = cfg.getInt("layer.minzoom", 0);

    if (minzoom > 0) {
      set.setMinZoom(minzoom);
    }

    set.setLayerPriority(cfg.getInt("layer.layerprio", 10));
    set.setHideByDefault(cfg.getBoolean("layer.hidebydefault", false));
    use3d = cfg.getBoolean("use3dregions", false);
    infowindow = cfg.getString("infowindow", DEF_INFOWINDOW);
    defstyle = new AreaStyle(cfg, "regionstyle");
    cusstyle = new HashMap<>();
    ownerstyle = new HashMap<>();
    cuswildstyle = new HashMap<>();

    ConfigurationSection sect = cfg.getConfigurationSection("custstyle");
    if (sect != null) {
      final Set<String> ids = sect.getKeys(false);

      ids.forEach(id -> {
        if (id.indexOf('|') >= 0)
          cuswildstyle.put(id, new AreaStyle(cfg, "custstyle." + id, defstyle));
        else
          cusstyle.put(id, new AreaStyle(cfg, "custstyle." + id, defstyle));
      });
    }

    sect = cfg.getConfigurationSection("ownerstyle");

    if (sect != null) {
      final Set<String> ids = sect.getKeys(false);

      ids.forEach(id -> ownerstyle.put(id.toLowerCase(), new AreaStyle(cfg, "ownerstyle." + id, defstyle)));
    }

    final List<String> vis = cfg.getStringList("visibleregions");

    if (vis != null) {
      visible = new HashSet<>(vis);
    }

    final List<String> hid = cfg.getStringList("hiddenregions");

    if (hid != null) {
      hidden = new HashSet<>(hid);
    }

    int per = cfg.getInt("update.period", 5);
    if (per < 15) per = 15;

    updperiod = (long) (per * 20);
    stop = false;
  }

  private FileConfiguration initConfig() {
    final FileConfiguration cfg = Survival.getInstance().getConfig();
    cfg.options().copyDefaults(true);   /* Load defaults, if needed */
    Survival.getInstance().saveConfig();  /* Save updates, if needed */
    return cfg;
  }

  private void reloadConfig() {
    if (reload) {
      Survival.getInstance().reloadConfig();
    } else {
      reload = true;
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

  //<editor-fold desc="getter and setter">
  public RegionManager getRegion() {
    return region;
  }

  public WorldGuardPlugin getWorldGuard() {
    return worldGuard;
  }

  boolean isStop() {
    return stop;
  }

  Map<String, AreaMarker> getResareas() {
    return resareas;
  }

  void setResareas(final Map<String, AreaMarker> resareas) {
    this.resareas = resareas;
  }

  long getUpdperiod() {
    return updperiod;
  }
  //</editor-fold>

}