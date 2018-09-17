package net.mmm.survival.dynmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
import net.mmm.survival.SurvivalData;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.GenericMarker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;

public class DynmapWorldGuardPlugin {

  private static final String DEF_INFOWINDOW = "<div class=\"infowindow\"><span style=\"font-size:120%;\"><center>%a</center></span><br /> %b<span style=\"font-weight:bold;\">%c</span>%d</div>";
  private final JavaPlugin plugin;
  public RegionManager rg;
  private boolean reload = false;
  //  public static final String BOOST_FLAG = "dynmap-boost";
  private Plugin dynmap;
  private DynmapAPI api;
  private WorldGuardPlugin wg;
  private BooleanFlag boost_flag;
  private int updatesPerTick = 20;
  private MarkerSet set;
  private long updperiod;
  private boolean use3d;
  private String infowindow;
  private AreaStyle defstyle;
  private Map<String, AreaStyle> cusstyle, cuswildstyle, ownerstyle;
  private Set<String> visible, hidden;
  private boolean stop;
  private int maxdepth;
  private Map<String, AreaMarker> resareas = new HashMap<>();

  /**
   * Konstruktor
   *
   * @param plugin Plugin
   */
  public DynmapWorldGuardPlugin(final JavaPlugin plugin) {
    this.plugin = plugin;
    rg = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(Bukkit.getWorld("world")));
  }

  /**
   * Schwerwiegende Mitteilung fuer den Logger/Konsole
   *
   * @param msg Mitteilung
   */
  private static void severe(final String msg) {
    System.err.println(msg);
  }

  /**
   * Formatierung des Infofensters
   *
   * @param region Region
   * @param m Marker
   * @return Formatierung als String
   * @see com.sk89q.worldguard.protection.regions.ProtectedRegion
   * @see org.dynmap.markers.AreaMarker
   */
  private String formatInfoWindow(final ProtectedRegion region, final AreaMarker m) {
    String v = "<div class=\"regioninfo\">" + infowindow + "</div>";
    boolean unowned = false;
    if ((region.getOwners().getPlayers().size() == 0) &&
        (region.getOwners().getUniqueIds().size() == 0) &&
        (region.getOwners().getGroups().size() == 0)) {
      unowned = true;
    }

    if (m.getLabel().startsWith("P-")) {
      v = v.replace("%a", "<center>" + m.getLabel() + "</center>");
      v = v.replace("%c", "<center>Projekt-Zone</center>");

      StringBuilder owner = null;
      for (final UUID uuid : region.getOwners().getUniqueIds()) {
        owner = (owner == null) ? new StringBuilder(SurvivalData.getInstance().getAsyncMySQL().getMySQL().getName(uuid)) : owner
            .append(", ").append(SurvivalData.getInstance().getAsyncMySQL().getMySQL().getName(uuid));
      }

      v = v.replace("%d", "<br />Projektleiter:<br /><span style=\"font-weight:bold;\">" + owner + "</span>");
    } else if (unowned) {
      v = v.replace("%a", m.getLabel());
      v = v.replace("%c", "<center>Server-Zone</center>");
    } else {
      v = v.replace("%a", SurvivalData.getInstance().getAsyncMySQL().getMySQL().getName(UUID.fromString(m.getLabel().toLowerCase())));

      StringBuilder owner = null;
      for (final UUID uuid : region.getOwners().getUniqueIds()) {
        owner = (owner == null) ? new StringBuilder(SurvivalData.getInstance().getAsyncMySQL().getMySQL().getName(uuid)) : owner
            .append(", ").append(SurvivalData.getInstance().getAsyncMySQL().getMySQL().getName(uuid));
      }

      v = v.replace("%b", "Owner: ");
      v = v.replace("%c", Objects.requireNonNull(owner).toString());
      v = v.replace("%groupowners%", region.getOwners().toGroupsString());
      v = v.replace("%groupmembers%", region.getMembers().toGroupsString());
      v = v.replace("*", "");
    }
    v = v.replace("%a", "");
    v = v.replace("%b", "");
    v = v.replace("%c", "");
    v = v.replace("%d", "");

    return v;
  }

  /**
   * Ist die Region in Dynmap sichtbar
   *
   * @param id Regionen-ID
   * @param worldname Weltname
   * @return booleanischer Wert
   */
  private boolean isVisible(final String id, final String worldname) {
    if ((visible != null) && (visible.size() > 0)) {
      if ((!visible.contains(id)) && (!visible.contains("world:" + worldname)) && (!visible.contains(worldname + "/" +
          id))) {
        return false;
      }
    }
    if ((hidden != null) && (hidden.size() > 0)) {
      return !hidden.contains(id) && !hidden.contains("world:" + worldname) && !hidden.contains(worldname + "/" + id);
    }
    return true;
  }

  /**
   * Fuegt einen neuen Stil hinzu
   *
   * @param resid Res-ID
   * @param worldid Welt-ID
   * @param m Marker
   * @param region Region
   * @see com.sk89q.worldguard.protection.regions.ProtectedRegion
   * @see org.dynmap.markers.AreaMarker
   */
  private void addStyle(final String resid, final String worldid, final AreaMarker m, final ProtectedRegion region) {
    AreaStyle as = cusstyle.get(worldid + "/" + resid);
    if (as == null) {
      as = cusstyle.get(resid);
    }
    if (as == null) {    /* Check for wildcard style matches */
      for (final String wc : cuswildstyle.keySet()) {
        final String[] tok = wc.split("\\|");
        if ((tok.length == 1) && resid.startsWith(tok[0]))
          as = cuswildstyle.get(wc);
        else if ((tok.length >= 2) && resid.startsWith(tok[0]) && resid.endsWith(tok[1]))
          as = cuswildstyle.get(wc);
      }
    }
    if (as == null) {    /* Check for owner style matches */
      if (!ownerstyle.isEmpty()) {
        final DefaultDomain dd = region.getOwners();
        final PlayerDomain pd = dd.getPlayerDomain();
        if (pd != null) {
          for (final String p : pd.getPlayers()) {
            as = ownerstyle.get(p.toLowerCase());
            if (as != null) break;
          }
          if (as == null) {
            for (final UUID uuid : pd.getUniqueIds()) {
              as = ownerstyle.get(uuid.toString());
              if (as != null) break;
            }
          }
          if (as == null) {
            for (final UUID uuid : pd.getUniqueIds()) {
              final String p = SurvivalData.getInstance().getAsyncMySQL().getMySQL().getName(uuid);
              if (p != null) {
                as = ownerstyle.get(p.toLowerCase());
                if (as != null) break;
              }
            }
          }
        }
        if (as == null) {
          final Set<String> grp = dd.getGroups();
          if (grp != null) {
            for (final String p : grp) {
              as = ownerstyle.get(p.toLowerCase());
              if (as != null) break;
            }
          }
        }
      }
    }
    if (as == null)
      as = defstyle;

    boolean unowned = false;
    if ((region.getOwners().getPlayers().size() == 0) &&
        (region.getOwners().getUniqueIds().size() == 0) &&
        (region.getOwners().getGroups().size() == 0)) {
      unowned = true;
    }

    if (region.getId().startsWith("p-")) {
      m.setLineStyle(as.strokeweight, as.strokeopacity, 0x00ff00);
      m.setFillStyle(as.fillopacity, 0x00ff00);
    } else if (unowned) {
      m.setLineStyle(as.strokeweight, as.strokeopacity, 0xffcc00);
      m.setFillStyle(as.fillopacity, 0xffcc00);
    } else {
      m.setLineStyle(as.strokeweight, as.strokeopacity, 0xff0000);
      m.setFillStyle(as.fillopacity, 0xff0000);
    }

    if (as.label != null) {
      m.setLabel(as.label);
    }
    if (boost_flag != null) {
      final Boolean b = region.getFlag(boost_flag);
      m.setBoostFlag((b != null) && b);
    }
  }

  /**
   * Verwalte die Region
   *
   * @param world Welt
   * @param region Region
   * @param newmap Karte
   * @see com.sk89q.worldguard.protection.regions.ProtectedRegion
   */
  private void handleRegion(final World world, final ProtectedRegion region, final Map<String, AreaMarker> newmap) {
    String name = region.getId();
    /* Make first letter uppercase */
    name = name.substring(0, 1).toUpperCase() + name.substring(1);
    final double[] x;
    final double[] z;

    /* Handle areas */
    if (isVisible(region.getId(), world.getName())) {
      final String id = region.getId();
      final RegionType tn = region.getType();
      final BlockVector l0 = region.getMinimumPoint();
      final BlockVector l1 = region.getMaximumPoint();

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
        IntStream.range(0, points.size()).forEach(i -> {
          final BlockVector2D pt = points.get(i);
          x[i] = pt.getX();
          z[i] = pt.getZ();
        });
      } else {  /* Unsupported type */
        return;
      }
      final String markerid = world.getName() + "_" + id;
      AreaMarker m = resareas.remove(markerid); /* Existing area? */
      if (m == null) {
        m = set.createAreaMarker(markerid, name, false, world.getName(), x, z, false);
        if (m == null)
          return;
      } else {
        m.setCornerLocations(x, z); /* Replace corner locations */
        m.setLabel(name);   /* Update label */
      }
      if (use3d) { /* If 3D? */
        m.setRangeY(l1.getY() + 1.0, l0.getY());
      }
      /* Set line and fill properties */
      addStyle(id, world.getName(), m, region);


      /* Build popup */
      final String desc = formatInfoWindow(region, m);

      m.setDescription(desc); /* Set popup */

      /* Add to map */
      newmap.put(markerid, m);
    }
  }

  /**
   * Wird bei der Aktivierung des Servers durchgefuehrt
   */
  public void enable() {
    final PluginManager pm = plugin.getServer().getPluginManager();
    /* Get dynmap */
    dynmap = pm.getPlugin("dynmap");
    if (dynmap == null) {
      severe("Cannot find dynmap!");
      return;
    }
    api = (DynmapAPI) dynmap; /* Get API */
    /* Get WorldGuard */
    final Plugin p = pm.getPlugin("WorldGuard");
    if (p == null) {
      severe("Cannot find WorldGuard!");
      return;
    }
    wg = (WorldGuardPlugin) p;
//        pc = wg.getProfileCache();

    plugin.getServer().getPluginManager().registerEvents(new OurServerListener(), plugin);

    /* If both enabled, activate */
    if (dynmap.isEnabled() && wg.isEnabled())
      activate();
  }

  /**
   * Aktivierung von Dynmap
   */
  @SuppressWarnings("deprecation")
  private void activate() {
    /* Now, get markers API */
    final MarkerAPI markerapi = api.getMarkerAPI();
    if (markerapi == null) {
      severe("Error loading dynmap marker API!");
      return;
    }
    /* Load configuration */
    if (reload) {
      plugin.reloadConfig();
    } else {
      reload = true;
    }
    final FileConfiguration cfg = plugin.getConfig();
    cfg.options().copyDefaults(true);   /* Load defaults, if needed */
    plugin.saveConfig();  /* Save updates, if needed */

    /* Now, add marker set for mobs (make it transient) */
    set = markerapi.getMarkerSet("worldguard.markerset");
    if (set == null)
      set = markerapi.createMarkerSet("worldguard.markerset", cfg.getString("layer.name", "Zonen"), null, false);
    else
      set.setMarkerSetLabel(cfg.getString("layer.name", "Zonen"));
    if (set == null) {
      severe("Error creating marker set");
      return;
    }
    final int minzoom = cfg.getInt("layer.minzoom", 0);
    if (minzoom > 0)
      set.setMinZoom(minzoom);
    set.setLayerPriority(cfg.getInt("layer.layerprio", 10));
    set.setHideByDefault(cfg.getBoolean("layer.hidebydefault", false));
    use3d = cfg.getBoolean("use3dregions", false);
    infowindow = cfg.getString("infowindow", DEF_INFOWINDOW);
    maxdepth = cfg.getInt("maxdepth", 16);
    updatesPerTick = cfg.getInt("updates-per-tick", 20);

    /* Get style information */
    defstyle = new AreaStyle(cfg, "regionstyle");
    cusstyle = new HashMap<>();
    ownerstyle = new HashMap<>();
    cuswildstyle = new HashMap<>();
    ConfigurationSection sect = cfg.getConfigurationSection("custstyle");
    if (sect != null) {
      final Set<String> ids = sect.getKeys(false);

      for (final String id : ids) {
        if (id.indexOf('|') >= 0)
          cuswildstyle.put(id, new AreaStyle(cfg, "custstyle." + id, defstyle));
        else
          cusstyle.put(id, new AreaStyle(cfg, "custstyle." + id, defstyle));
      }
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

    plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new UpdateJob(), 40L);  /* First time is 2 seconds */

//        info("version " + plugin.getDescription().getVersion() + " is activated");
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

  private static class AreaStyle {
    String strokecolor;
    String unownedstrokecolor;
    double strokeopacity;
    int strokeweight;
    String fillcolor;
    double fillopacity;
    String label;

    /**
     * Konstruktor
     *
     * @param cfg Konfigurationsdatei
     * @param path Dateienpath
     * @param def definierer AreaStyle
     */
    AreaStyle(final FileConfiguration cfg, final String path, final AreaStyle def) {
      strokecolor = cfg.getString(path + ".strokeColor", def.strokecolor);
      unownedstrokecolor = cfg.getString(path + ".unownedStrokeColor", def.unownedstrokecolor);
      strokeopacity = cfg.getDouble(path + ".strokeOpacity", def.strokeopacity);
      strokeweight = cfg.getInt(path + ".strokeWeight", def.strokeweight);
      fillcolor = cfg.getString(path + ".fillColor", def.fillcolor);
      fillopacity = cfg.getDouble(path + ".fillOpacity", def.fillopacity);
      label = cfg.getString(path + ".label", null);
    }

    /**
     * Konstruktor
     *
     * @param cfg Konfigurationsdatei
     * @param path Dateienpath
     */
    AreaStyle(final FileConfiguration cfg, final String path) {
      strokecolor = cfg.getString(path + ".strokeColor", "#FF0000");
      unownedstrokecolor = cfg.getString(path + ".unownedStrokeColor", "#00FF00");
      strokeopacity = cfg.getDouble(path + ".strokeOpacity", 0.8);
      strokeweight = cfg.getInt(path + ".strokeWeight", 3);
      fillcolor = cfg.getString(path + ".fillColor", "#FF0000");
      fillopacity = cfg.getDouble(path + ".fillOpacity", 0.35);
    }
  }

  /**
   * Innere Klasse UpdateJob verwaltet eine spezifische Region
   *
   * @see java.lang.Runnable
   */
  private class UpdateJob implements Runnable {
    Map<String, AreaMarker> newmap = new HashMap<>(); /* Build new map */
    List<World> worldsToDo = null;
    List<ProtectedRegion> regionsToDo = null;
    World curworld = null;

    @Override
    @SuppressWarnings("deprecation")
    public void run() {
      if (stop) {
        return;
      }
      // If worlds list isn't primed, prime it
      if (worldsToDo == null) {
        worldsToDo = new ArrayList<>(plugin.getServer().getWorlds());
      }
      while (regionsToDo == null) {  // No pending regions for world
        if (worldsToDo.isEmpty()) { // No more worlds?
          /* Now, review old map - anything left is gone */
          resareas.values().forEach(GenericMarker::deleteMarker);
          /* And replace with new map */
          resareas = newmap;
          // Set up for next update (new job)
          plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new UpdateJob(), updperiod);
          return;
        } else {
          curworld = worldsToDo.remove(0);
          final RegionManager rm = rg; /* Get region manager for world */

          if (rm != null) {
            final Map<String, ProtectedRegion> regions = rm.getRegions();  /* Get all the regions */
            if (!regions.isEmpty()) {
              regionsToDo = new ArrayList<>(regions.values());
            }
          }
        }
      }
      /* Now, process up to limit regions */
      for (int i = 0; i < updatesPerTick; i++) {
        if (regionsToDo.isEmpty()) {
          regionsToDo = null;
          break;
        }
        final ProtectedRegion pr = regionsToDo.remove(regionsToDo.size() - 1);
        int depth = 1;
        ProtectedRegion p = pr;
        while (p.getParent() != null) {
          depth++;
          p = p.getParent();
        }
        if (depth > maxdepth)
          continue;
        handleRegion(curworld, pr, newmap);
      }
      // Tick next step in the job
      plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, this, 1L);
    }
  }

  /**
   * Listener fuer den Server
   *
   * @see org.bukkit.event.Listener
   */
  private class OurServerListener implements Listener {

    /**
     * Event beim enablen des Plugins
     *
     * @param event Event
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPluginEnable(final PluginEnableEvent event) {
      final Plugin p = event.getPlugin();
      final String name = p.getDescription().getName();

      if ((name.equals("dynmap") || name.equals("WorldGuard")) && dynmap.isEnabled() && wg.isEnabled()) {
        activate();
      }
    }
  }

}
