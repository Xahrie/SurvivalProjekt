package net.mmm.survival.player;

import java.util.Map;

import de.PAS123.Group.Group.Group;
import de.PAS123.Group.Main.Spigot.BungeeGroupManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

/**
 * Scoreboards setzt die Scoreboards der Spieler
 */
public class Scoreboards {
  private static final BungeeGroupManager manager = BungeeGroupManager.getGroupManager();
  private static final Map<Group, String> tablist = manager.tablist;

  /**
   * Setzt fuer einen spezifischen Spieler <code>p</code> das Scoreboard um den Rang anzeigen zu lassen
   *
   * @param owner Owner des Scoreboards
   */
  public static void setScoreboard(final Player owner) {
    final Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    tablist.keySet().forEach(group ->
        scoreboard.registerNewTeam(manager.tablist.get(group)).setPrefix(manager.getKurzel(group)));

    owner.setScoreboard(scoreboard);
    setPrefix(owner);
  }

  @SuppressWarnings("deprecation")
  private static void setPrefix(final Player owner) {
    for (final Player onlinePlayer : Bukkit.getOnlinePlayers()) {
      final Scoreboard onlinePlayerScoreboard = onlinePlayer.getScoreboard();
      final String onlinePlayerTeam = tablist.get(manager.getGroup(owner.getUniqueId()));
      onlinePlayerScoreboard.getTeam(onlinePlayerTeam).addPlayer(owner);
      final Scoreboard ownerScoreboard = owner.getScoreboard();
      final String ownerTeam = tablist.get(manager.getGroup(onlinePlayer.getUniqueId()));
      ownerScoreboard.getTeam(ownerTeam).addPlayer(onlinePlayer);
    }

    owner.setDisplayName(manager.getPrefix(owner) + owner.getName());
    owner.setPlayerListName(manager.getPrefix(owner) + owner.getName());
  }
}
