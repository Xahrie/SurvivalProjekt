package net.mmm.survival.util;

// Package-Stil ???
import de.PAS123.Group.Main.Spigot.BungeeGroupManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

/**
 * Scoreboards setzt die Scoreboards der Spieler
 */
public class Scoreboards {
  /**
   * Setzt fuer einen spezifischen Spieler <code>p</code> das Scoreboard
   *
   * @param player Owner des Scoreboards
   */
  public static void setScoreboard(final Player player) {
    final Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    final BungeeGroupManager manager = BungeeGroupManager.getGroupManager();

    manager.tablist.keySet().forEach(group -> scoreboard.registerNewTeam("" + manager.tablist.get(group)).setPrefix(manager.getKurzel(group)));

    player.setScoreboard(scoreboard);
    setPrefix(player);
  }

  /**
   * Setzt fuer einen spezifischen Spieler <code>p</code> den Prefix
   *
   * @param player Owner des Prefix
   */
  @SuppressWarnings("deprecation")
  private static void setPrefix(final Player player) {
    final BungeeGroupManager manager = BungeeGroupManager.getGroupManager();

    Bukkit.getOnlinePlayers().forEach(all -> {
      all.getScoreboard().getTeam("" + manager.tablist.get(manager.getGroup(player.getUniqueId()))).addPlayer(player);
      player.getScoreboard().getTeam("" + manager.tablist.get(manager.getGroup(all.getUniqueId()))).addPlayer(all);
    });
    player.setDisplayName(manager.getPrefix(player) + player.getName());
    player.setPlayerListName(manager.getPrefix(player) + player.getName());

  }

}
