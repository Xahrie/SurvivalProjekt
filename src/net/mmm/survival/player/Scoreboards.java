package net.mmm.survival.player;

import java.util.Map;

import de.PAS123.Group.Group.Group;
import de.PAS123.Group.Main.Spigot.BungeeGroupManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

/**
 * Scoreboards setzt die Scoreboards der Spieler
 */
public final class Scoreboards {
  private static final BungeeGroupManager manager = BungeeGroupManager.getGroupManager();
  private static final Map<Group, String> tablist = manager.tablist;

  /**
   * Setzt fuer einen spezifischen Spieler {@code p} das Scoreboard um den Rang anzeigen zu lassen
   *
   * @param owner Owner des Scoreboards
   */
  public static void setScoreboard(final Player owner) {
    final ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
    final Scoreboard scoreboard = scoreboardManager.getNewScoreboard();

    for (final Group group : tablist.keySet()) {
      final String prefix = manager.getKurzel(group);
      scoreboard.registerNewTeam(tablist.get(group)).setPrefix(prefix);
    }

    owner.setScoreboard(scoreboard);
    determinePrefix(owner);
  }

  @SuppressWarnings("deprecation")
  private static void determinePrefix(final Player owner) {
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
