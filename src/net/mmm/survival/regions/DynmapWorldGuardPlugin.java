package net.mmm.survival.regions;

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
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionType;
import net.mmm.survival.Survival;
import net.mmm.survival.SurvivalData;
import net.mmm.survival.mysql.AsyncMySQL;
import net.mmm.survival.util.logger.Logger;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.GenericMarker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;


public class DynmapWorldGuardPlugin {
  private static final String DEF_INFOWINDOW = "<div class=\"infowindow\"><span style=\"font-size:120%;" +
      "\"><center>%a</center></span><br /> %b<span style=\"font-weight:bold;\">%c</span>%d</div>";
  private final Logger logger = new Logger(DynmapWorldGuardPlugin.class.getName());
  private final Map<String, AreaMarker> newmap = new HashMap<>();
  private boolean reload, stop, use3d;
  private int maxdepth, updatesPerTick = 20;
  private long updperiod;
  private DynmapWorldGuardPlugin.AreaStyle defstyle;
  private Map<String, AreaMarker> resareas = new HashMap<>();
  private Map<String, DynmapWorldGuardPlugin.AreaStyle> cusstyle, cuswildstyle, ownerstyle;
  private MarkerSet markerSet;
  private Plugin dynmap;
  private RegionManager regionManager;
  private Set<String> hidden, visible;
  private String infowindow;
  private WorldGuardPlugin worldGuardPlugin;
  private List<ProtectedRegion> regionsToDo;
  private List<World> worldsToDo;
  private World curworld;

  /**
   * Konstruktor
   */
  public DynmapWorldGuardPlugin() {
    if (checkPlatform() && checkRegionContainter(WorldGuard.getInstance().getPlatform())) {
      final WorldGuardPlatform platform = WorldGuard.getInstance().getPlatform();
      final RegionContainer regionContainer = platform.getRegionContainer();
      this.regionManager = regionContainer.get(BukkitAdapter.adapt(SurvivalWorld.BAUWELT.get()));
    }
    logger.exit();
  }

  private boolean checkPlatform() {
    if (WorldGuard.getInstance().getPlatform() != null) {
      return true;
    } else {
      logger.error("Platform is Null");
    }
    return false;
  }

  private boolean checkRegionContainter(final WorldGuardPlatform platform) {
    if (platform.getRegionContainer() != null) {
      return true;
    } else {
      logger.error("RegionContainer is Null");
    }
    return false;
  }

  private void severe(final String msg) {
    logger.error(msg);
  }

  /**
   * Wird bei der Aktivierung des Servers durchgefuehrt
   */
  public void onEnable() {
    final Server server = Survival.getInstance().getServer();
    final PluginManager pluginManager = server.getPluginManager();
    dynmap = pluginManager.getPlugin("dynmap"); /* Get dynamp */
    evaluateDynmap(pluginManager);
  }

  private void evaluateDynmap(final PluginManager pluginManager) {
    if (this.dynmap != null) {
      evaluateWorldGuard(pluginManager.getPlugin("WorldGuard"));
    } else {
      severe("Cannot find dynmap!");
    }
  }

  private void evaluateWorldGuard(final Plugin worldGuard) {
    if (worldGuard != null) {
      this.worldGuardPlugin = (WorldGuardPlugin) worldGuard;
      final Server server = Survival.getInstance().getServer();
      final PluginManager pluginManager = server.getPluginManager();
      pluginManager.registerEvents(new OurServerListener(), Survival.getInstance());
      if (this.dynmap.isEnabled() && this.worldGuardPlugin.isEnabled()) /* If both enabled, activate */
        activate();
    } else {
      severe("Cannot find WorldGuard!");
    }
  }

  @SuppressWarnings("deprecation")
  private void activate() {
    final MarkerAPI markerapi = ((DynmapAPI) dynmap).getMarkerAPI(); /* Now, get markers API */
    if (markerapi != null) {
      final FileConfiguration configuration = loadConfiguration();
      addMarkerForMobs(markerapi, configuration);
      evaluateMarkerSet(configuration);
    } else {
      severe("Error loading regions marker API!");
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

  private void evaluateMarkerSet(final FileConfiguration configuration) {
    if (this.markerSet != null) {
      setupDynmap(configuration);
    } else {
      severe("Error creating marker set");
    }
  }

  @SuppressWarnings("deprecation")
  private void setupDynmap(final FileConfiguration configuration) {
    final int minzoom = configuration.getInt("layer.minzoom", 0);
    if (minzoom > 0 && markerSet != null) {
      markerSet.setMinZoom(minzoom);
      markerSet.setLayerPriority(configuration.getInt("layer.layerprio", 10));
      markerSet.setHideByDefault(configuration.getBoolean("layer.hidebydefault", false));
      use3d = configuration.getBoolean("use3dregions", false);
      infowindow = configuration.getString("infowindow", DEF_INFOWINDOW);
      maxdepth = configuration.getInt("maxdepth", 16);
      updatesPerTick = configuration.getInt("updates-per-tick");
    }

    getStyleInformation(configuration);
    final Server server = Survival.getInstance().getServer();
    final BukkitScheduler scheduler = server.getScheduler();
    scheduler.scheduleAsyncDelayedTask(Survival.getInstance(), () -> {
      if (!stop) {
        primeWorlds();
        evaluatePendingRegions();
      }
    }, 40L);
  }

  private void addMarkerForMobs(final MarkerAPI markerapi, final FileConfiguration configuration) {
    markerSet = markerapi.getMarkerSet("worldguard.markerset");
    if (markerSet == null) {
      markerSet = markerapi.createMarkerSet("worldguard.markerset", configuration
          .getString("layer.name", "Zonen"), null, false);
    } else {
      markerSet.setMarkerSetLabel(configuration.getString("layer.name", "Zonen"));
    }
  }

  private void getStyleInformation(final FileConfiguration cfg) {
    getCuststyleSection(cfg);

    int per = cfg.getInt("update.period", 5);
    if (per < 15) per = 15;
    updperiod = per * 20;
    stop = false;
  }

  private void getCuststyleSection(final FileConfiguration cfg) {
    final ConfigurationSection sect = cfg.getConfigurationSection("custstyle");
    if (sect != null) {
      final Set<String> ids = sect.getKeys(false);

      cusstyle = new HashMap<>();
      cuswildstyle = new HashMap<>();
      defstyle = new DynmapWorldGuardPlugin.AreaStyle(cfg);
      for (final String id : ids) {
        if (id.indexOf('|') >= 0)
          cuswildstyle.put(id, new DynmapWorldGuardPlugin.AreaStyle(cfg, "custstyle." + id, defstyle));
        else
          cusstyle.put(id, new DynmapWorldGuardPlugin.AreaStyle(cfg, "custstyle." + id, defstyle));
      }
    }
    getOwnerstyleSection(cfg);
  }

  private void getOwnerstyleSection(final FileConfiguration configuration) {
    final ConfigurationSection sect = configuration.getConfigurationSection("ownerstyle");
    if (sect != null) {
      final Set<String> ids = sect.getKeys(false);
      ownerstyle = new HashMap<>();
      for (final String id : ids) {
        ownerstyle.put(id.toLowerCase(), new DynmapWorldGuardPlugin.AreaStyle(configuration, "ownerstyle." + id, defstyle));
      }
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
    if (markerSet != null) {
      markerSet.deleteMarkerSet();
      markerSet = null;
    }
    resareas.clear();
    stop = true;
  }

  public RegionManager getRegionManager() {
    return regionManager;
  }

  @SuppressWarnings("deprecation")
  private void evaluatePendingRegions() {
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
    final Server server = Survival.getInstance().getServer();
    final BukkitScheduler scheduler = server.getScheduler();
    scheduler.scheduleAsyncDelayedTask(Survival.getInstance(), () -> {
      if (!stop) {
        primeWorlds();
        evaluatePendingRegions();
      }
    }, 1L);
  }

  @SuppressWarnings("deprecation")
  private void noMoreWorlds() {
    resareas.values().forEach(GenericMarker::deleteMarker); /* Now, review old map - anything left is gone */
    resareas = newmap; /* And replace with new map */
    final Server server = Survival.getInstance().getServer();
    final BukkitScheduler scheduler = server.getScheduler();
    scheduler.scheduleAsyncDelayedTask(Survival.getInstance(), () -> {
      if (!stop) {
        primeWorlds();
        evaluatePendingRegions();
      }
    }, updperiod);
  }

  private void primeWorlds() {
    if (worldsToDo == null) {
      final Server server = Survival.getInstance().getServer();
      worldsToDo = new ArrayList<>(server.getWorlds());
    }
  }

  private void limitRegions() {
    for (int i = 0; i < updatesPerTick; i++) {
      if (regionsToDo != null && regionsToDo.isEmpty()) {
        regionsToDo = null;
        break;
      }

      ProtectedRegion region = null;
      if (regionsToDo != null) {
        region = regionsToDo.remove(regionsToDo.size() - 1);
      }
      int depth = 1;
      if (region != null) {
        while (region.getParent() != null) {
          depth++;
          region = region.getParent();
        }
      }
      if (depth > maxdepth)
        continue;
      if (region != null) {
        handleRegion(curworld, region, newmap);
      }
    }
  }

  private void handleRegion(final World world, final ProtectedRegion region, final Map<String, AreaMarker> newmap) {
    String name = region.getId();
    name = name.substring(0, 1).toUpperCase() + name.substring(1); /* Make first letter uppercase */
    handleAreas(world, region, newmap, name);
  }

  private void handleAreas(final World world, final ProtectedRegion region, final Map<String, AreaMarker> newmap, final String name) {
    final String id = region.getId();
    final String worldname = world.getName();
    if (((visible == null || visible.isEmpty() || visible.contains(id) || visible.contains("world:" +
        worldname) || visible.contains(worldname + "/" + id)) && (hidden == null || hidden.isEmpty()
        || hidden.contains(id) || hidden.contains("world:" + worldname) || hidden.contains(worldname +
        "/" + id))) && !isCuboid(region.getType(), region.getMinimumPoint(), region.getMaximumPoint())
        && region.getType() == RegionType.POLYGON) {
      evaluateVisible(world, region, newmap, name);
    }
  }

  private void evaluateVisible(final World world, final ProtectedRegion region, final Map<String, AreaMarker> newmap, final String name) {
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

  private void addStyle(final String resid, final String worldId, final AreaMarker areaMarker, final ProtectedRegion region) {
    DynmapWorldGuardPlugin.AreaStyle as = cusstyle.get(worldId + "/" + resid);
    if (as == null) {
      as = cusstyle.get(resid);
    }
    as = evaluateWildcardStyleMatches(resid, as);
    as = evaluateOwnerStyleMatches(region, as);
    if (as == null)
      as = defstyle;

    if (region.getId().startsWith("p-")) {
      areaMarker.setLineStyle(as.getStrokeweight(), as.getStrokeopacity(), 0x00ff00);
      areaMarker.setFillStyle(as.getFillopacity(), 0x00ff00);
    } else if (isUnowned(region)) {
      areaMarker.setLineStyle(as.getStrokeweight(), as.getStrokeopacity(), 0xffcc00);
      areaMarker.setFillStyle(as.getFillopacity(), 0xffcc00);
    } else {
      areaMarker.setLineStyle(as.getStrokeweight(), as.getStrokeopacity(), 0xff0000);
      areaMarker.setFillStyle(as.getFillopacity(), 0xff0000);
    }

    if (as.getLabel() != null) {
      areaMarker.setLabel(as.getLabel());
    }
  }

  private void check3D(final BlockVector l0, final BlockVector l1, final AreaMarker areaMarker) {
    if (use3d) {
      areaMarker.setRangeY(l1.getY() + 1.0, l0.getY());
    }
  }

  private void buildPopup(final ProtectedRegion region, final AreaMarker areaMarker) {
    final String desc = formatInfoWindow(region, areaMarker);
    areaMarker.setDescription(desc);
  }

  private String formatInfoWindow(final ProtectedRegion region, final AreaMarker marker) {
    final StringBuilder owner = determineOwner(region);
    String format = "<div class=\"regioninfo\">" + infowindow + "</div>";

    format = (marker.getLabel().startsWith("P-") ? format
        .replace("%a", "<center>" + marker.getLabel() + "</center>")
        .replace("%c", "<center>Projekt-Zone</center>")
        .replace("%d", "<br />Projektleiter:<br /><span style=\"font-weight:bold;\">" +
            owner + "</span>") : (isUnowned(region) ? format
        .replace("%a", marker.getLabel())
        .replace("%c", "<center>Server-Zone</center>") : modifyFormat(region, marker, format, owner)))
        .replace("%a", "")
        .replace("%b", "")
        .replace("%c", "")
        .replace("%d", "");

    return format;
  }

  private StringBuilder determineOwner(final ProtectedRegion region) {
    final StringBuilder owner = new StringBuilder();
    final DefaultDomain regionOwners = region.getOwners();
    regionOwners.getUniqueIds().forEach(uuid -> {
      final AsyncMySQL asyncMySQL = SurvivalData.getInstance().getAsyncMySQL();
      owner.append(", ")
          .append(asyncMySQL.getName(uuid));
    });
    owner.replace(0, 2, "");
    return owner;
  }

  private String modifyFormat(final ProtectedRegion region, final AreaMarker marker, String format, final StringBuilder owner) {
    format = owner.length() == 0 ? format.replace("%c", "") : format;
    final AsyncMySQL asyncMySQL = SurvivalData.getInstance().getAsyncMySQL();
    format = format.replace("%a", asyncMySQL.getName(UUID.fromString(marker.getLabel().toLowerCase())))
        .replace("%b", "Owner: ")
        .replace("%groupowners%", region.getOwners().toGroupsString())
        .replace("%groupmembers%", region.getMembers().toGroupsString())
        .replace("*", "");
    return format;
  }

  private boolean isUnowned(final ProtectedRegion region) {
    final DefaultDomain regionOwners = region.getOwners();
    final Set<String> regionOwnersPlayers = regionOwners.getPlayers();
    return regionOwnersPlayers.isEmpty() && regionOwners.getUniqueIds().isEmpty() &&
        regionOwners.getGroups().isEmpty();
  }

  private DynmapWorldGuardPlugin.AreaStyle evaluateWildcardStyleMatches(final String resid, DynmapWorldGuardPlugin.AreaStyle areaStyle) {
    if (areaStyle == null) {
      for (final String wc : cuswildstyle.keySet()) {
        final String[] tok = wc.split("\\|");
        if (((tok.length == 1) && resid.startsWith(tok[0])) || ((tok.length >= 2) &&
            resid.startsWith(tok[0]) && resid.endsWith(tok[1]))) {
          areaStyle = cuswildstyle.get(wc);
        }
      }
    }
    return areaStyle;
  }

  private DynmapWorldGuardPlugin.AreaStyle evaluateOwnerStyleMatches(final ProtectedRegion region,
                                                                     DynmapWorldGuardPlugin.AreaStyle areaStyle) {
    if (areaStyle == null) {
      areaStyle = evaluateOwnerStyleExist(region);
    }
    return areaStyle;
  }

  private DynmapWorldGuardPlugin.AreaStyle evaluateOwnerStyleExist(final ProtectedRegion region) {
    if (!ownerstyle.isEmpty()) {
      final DefaultDomain defaultDomain = region.getOwners();
      final PlayerDomain playerDomain = defaultDomain.getPlayerDomain();
      final DynmapWorldGuardPlugin.AreaStyle areaStyle = evaluatePlayerDomain(playerDomain);
      return areaStyle != null ? evaluatePlayerDomain(playerDomain) : evaluateGroups(defaultDomain);
    }
    return null;
  }

  private DynmapWorldGuardPlugin.AreaStyle evaluateGroups(final DefaultDomain defaultDomain) {
    final Set<String> grp = defaultDomain.getGroups();
    return grp != null ? grp.stream().filter(p ->
        ownerstyle.get(p.toLowerCase()) != null).findFirst().map(p ->
        ownerstyle.get(p.toLowerCase())).orElse(null) : null;
  }

  private DynmapWorldGuardPlugin.AreaStyle evaluatePlayerDomain(final PlayerDomain playerDomain) {
    if (playerDomain != null) {

      for (final String p : playerDomain.getPlayers()) {
        final DynmapWorldGuardPlugin.AreaStyle areaStyle = ownerstyle.get(p.toLowerCase());
        if (areaStyle != null) {
          return areaStyle;
        }
      }

      for (final UUID uuid : playerDomain.getUniqueIds()) {
        final DynmapWorldGuardPlugin.AreaStyle areaStyle = ownerstyle.get(uuid.toString());
        if (areaStyle != null) {
          return areaStyle;
        }
      }

      for (final UUID uuid : playerDomain.getUniqueIds()) {
        final AsyncMySQL asyncMySQL = SurvivalData.getInstance().getAsyncMySQL();
        final String p = asyncMySQL.getName(uuid);
        final DynmapWorldGuardPlugin.AreaStyle areaStyle = ownerstyle.get(p);
        if (areaStyle != null) {
          return ownerstyle.get(p.toLowerCase());
        }
      }
    }
    return null;
  }

  private AreaMarker doesAreaExist(final World world, final String name, final double[] x, final double[] z, final String markerid) {
    AreaMarker m = resareas.remove(markerid); /* Existing area? */
    if (m == null) {
      m = markerSet.createAreaMarker(markerid, name, false, world.getName(), x, z, false);
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

  private class AreaStyle {
    private final double fillopacity, strokeopacity;
    private final int strokeweight;
    private final String fillcolor, strokecolor, unownedstrokecolor;
    private String label;

    AreaStyle(final FileConfiguration cfg, final String path, final DynmapWorldGuardPlugin.AreaStyle def) {
      strokecolor = cfg.getString(path + ".strokeColor", def.getStrokecolor());
      unownedstrokecolor = cfg.getString(path + ".unownedStrokeColor", def.getUnownedstrokecolor());
      strokeopacity = cfg.getDouble(path + ".strokeOpacity", def.getStrokeopacity());
      strokeweight = cfg.getInt(path + ".strokeWeight", def.getStrokeweight());
      fillcolor = cfg.getString(path + ".fillColor", def.getFillcolor());
      fillopacity = cfg.getDouble(path + ".fillOpacity", def.getFillopacity());
      setLabel(cfg.getString(path + ".label", null));
    }

    AreaStyle(final FileConfiguration cfg) {
      strokecolor = cfg.getString("regionstyle.strokeColor", "#FF0000");
      unownedstrokecolor = cfg.getString("regionstyle.unownedStrokeColor", "#00FF00");
      strokeopacity = cfg.getDouble("regionstyle.strokeOpacity", 0.8);
      strokeweight = cfg.getInt("regionstyle.strokeWeight", 3);
      fillcolor = cfg.getString("regionstyle.fillColor", "#FF0000");
      fillopacity = cfg.getDouble("regionstyle.fillOpacity", 0.35);
    }

    private double getFillopacity() {
      return fillopacity;
    }

    private double getStrokeopacity() {
      return strokeopacity;
    }

    private int getStrokeweight() {
      return strokeweight;
    }

    private String getFillcolor() {
      return fillcolor;
    }

    private String getStrokecolor() {
      return strokecolor;
    }

    private String getUnownedstrokecolor() {
      return unownedstrokecolor;
    }

    private String getLabel() {
      return label;
    }

    private void setLabel(final String label) {
      this.label = label;
    }
  }

  /**
   * @see org.bukkit.event.Listener
   */
  private class OurServerListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPluginEnable(final PluginEnableEvent event) {
      final Plugin plugin = event.getPlugin();
      final PluginDescriptionFile pluginDescription = plugin.getDescription();
      final String name = pluginDescription.getName();
      if (name.equals("dynmap") || name.equals("WorldGuard")) {
        if (dynmap.isEnabled() && worldGuardPlugin.isEnabled())
          activate();
      }
    }
  }
}
