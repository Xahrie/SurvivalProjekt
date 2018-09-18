package net.mmm.survival.dynmap;

import org.bukkit.configuration.file.FileConfiguration;

/**
 * Definition des AreaStyle
 */
class AreaStyle {
  double strokeopacity, fillopacity;
  int strokeweight;
  private final String strokecolor, unownedstrokecolor, fillcolor;
  private String label;

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

  //<editor-fold desc="getter and setter">
  String getLabel() {
    return label;
  }
  //</editor-fold>

}
