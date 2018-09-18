package net.mmm.survival.dynmap;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.IntStream;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.domains.PlayerDomain;
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

      final StringBuilder owner = new StringBuilder();
      region.getOwners().getUniqueIds().forEach(uuid -> owner.append(SurvivalData.getInstance().getAsyncMySQL().getName(uuid)).append(", "));
      v = v.replace("%d", "<br />Projektleiter:<br /><span style=\"font-weight:bold;\">" + owner + "</span>");

    } else {
      v = isUnowned(region, v, marker);
    }

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
    style = setStyle(resid, region, style);

    checkUnowned(region);
    checkRegionId(m, region, style);

    if (style.getLabel() != null) {
      m.setLabel(style.getLabel());
    }

    if (boost_flag != null) {
      final Boolean b = region.getFlag(boost_flag);
      m.setBoostFlag((b != null) && b);
    }

  }

  private AreaStyle setStyle(final String resid, final ProtectedRegion region, AreaStyle style) {
    style = (style == null) ? cusstyle.get(resid) : null;

    style = checkWildcardStyle(resid, style);

    style = checkOwnerStyle(region, style);

    style = (style == null) ? defstyle : null;

    return style;
  }

  private AreaStyle checkOwnerStyle(final ProtectedRegion region, AreaStyle style) {
    if (style == null) {
      if (!ownerstyle.isEmpty()) {
        final DefaultDomain defaultDomain = region.getOwners();
        final PlayerDomain playerDomain = defaultDomain.getPlayerDomain();

        if (playerDomain != null) {
          for (final String p : playerDomain.getPlayers()) {
            style = ownerstyle.get(p.toLowerCase());
            if (style != null) break;
          }

          if (style == null) {
            for (final UUID uuid : playerDomain.getUniqueIds()) {
              style = ownerstyle.get(uuid.toString());
              if (style != null) break;
            }
          }

          if (style == null) {
            for (final UUID uuid : playerDomain.getUniqueIds()) {
              final String p = SurvivalData.getInstance().getAsyncMySQL().getName(uuid);

              if (p != null) {
                style = ownerstyle.get(p.toLowerCase());
                if (style != null) break;
              }
            }
          }

        }

        if (style == null) {
          final Set<String> grp = defaultDomain.getGroups();

          if (grp != null) {
            for (final String p : grp) {
              style = ownerstyle.get(p.toLowerCase());
              if (style != null) break;
            }
          }
        }

      }
    }
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
    final double[] x = new double[0];
    final double[] z = new double[0];

    /* Handle areas */
    handleAreas(world, region, newmap, name, x, z);
  }

  private void handleAreas(final World world, final ProtectedRegion region, final Map<String, AreaMarker> newmap, final String name,
                           double[] x, double[] z) {
    if (isVisible(region.getId(), world.getName())) {
      final String id = region.getId();
      final RegionType regionType = region.getType();
      final BlockVector minimumPoint = region.getMinimumPoint();
      final BlockVector maximumPoint = region.getMaximumPoint();

      if (regionType == RegionType.CUBOID) { /* Cubiod region? Make outline */
        final List<double[]> arrays = handleCuboid(minimumPoint, maximumPoint);
        x = arrays.get(0);
        z = arrays.get(1);
      } else if (regionType == RegionType.POLYGON) {
        final List<double[]> arrays = handlePolygon(region);
        x = arrays.get(0);
        z = arrays.get(1);
      } //TODO (Abgie) 18.09.2018: else ???

      final String markerid = world.getName() + "_" + id;
      AreaMarker m = resareas.remove(markerid); /* Existing area? */

      if (m == null) {
        m = set.createAreaMarker(markerid, name, false, world.getName(), x, z, false);
      } else {
        m.setCornerLocations(x, z); /* Replace corner locations */
        m.setLabel(name);   /* Update label */
      }

      if (use3d) { /* If 3D? */
        m.setRangeY(maximumPoint.getY() + 1.0, minimumPoint.getY());
      }

      addStyle(id, world.getName(), m, region); /* Set line and fill properties */

      final String desc = formatInfoWindow(region, m); /* Build popup */
      m.setDescription(desc); /* Set popup */

      newmap.put(markerid, m); /* Add to map */
    }
  }

  private List<double[]> handlePolygon(final ProtectedRegion region) {
    final ProtectedPolygonalRegion ppr = (ProtectedPolygonalRegion) region;
    final List<BlockVector2D> points = ppr.getPoints();
    final double[] x = new double[points.size()];
    final double[] z = new double[points.size()];

    IntStream.range(0, points.size()).forEach(i -> {
      final BlockVector2D pt = points.get(i);
      x[i] = pt.getX();
      z[i] = pt.getZ();
    });

    return Arrays.asList(x, z);
  }

  private List<double[]> handleCuboid(final BlockVector vectorX, final BlockVector vectorZ) {
    final double[] x = new double[4];
    final double[] z = new double[4];

    x[0] = vectorX.getX();
    z[0] = vectorX.getZ();
    x[1] = vectorX.getX();
    z[1] = vectorZ.getZ() + 1.0;
    x[2] = vectorZ.getX() + 1.0;
    z[2] = vectorZ.getZ() + 1.0;
    x[3] = vectorZ.getX() + 1.0;
    z[3] = vectorX.getZ();

    return Arrays.asList(x, z);
  }

  /**
   * Wird bei der Aktivierung des Servers durchgefuehrt
   */
  public void enable() {
    final PluginManager pluginManager = Survival.getInstance().getServer().getPluginManager();
    final Plugin dynmap = pluginManager.getPlugin("dynmap");

    if (dynmap == null) {
      severe("Cannot find dynmap!");
      return;
    }

    api = (DynmapAPI) dynmap; /* Get API */
    final Plugin p = pluginManager.getPlugin("WorldGuard"); /* Get WorldGuard */

    if (p == null) {
      severe("Cannot find WorldGuard!");
      return;
    }

    worldGuard = (WorldGuardPlugin) p;

    if (dynmap.isEnabled() && worldGuard.isEnabled()) {
      activate();
    }

  }

  /**
   * Aktivierung von Dynmap
   */
  @SuppressWarnings("deprecation")
  public void activate() {
    final MarkerAPI markerapi = api.getMarkerAPI(); /* Now, get markers API */

    if (markerapi == null) {
      severe("Error loading dynmap marker API!");
      return;
    }

    reloadConfig();

    if (setupConfig(markerapi)) {
      return;
    }

    Survival.getInstance().getServer().getScheduler().scheduleAsyncDelayedTask(Survival.getInstance(), new UpdateJob(), 40L);  /* First time is 2 seconds */
  }

  private boolean setupConfig(final MarkerAPI markerapi) {
    final FileConfiguration cfg = Survival.getInstance().getConfig();
    cfg.options().copyDefaults(true);   /* Load defaults, if needed */
    Survival.getInstance().saveConfig();  /* Save updates, if needed */

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

    return false;
  }

  private void reloadConfig() {
    if (reload) { /* Load configuration */
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
