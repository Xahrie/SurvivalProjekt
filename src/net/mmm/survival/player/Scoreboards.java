package net.mmm.survival.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.PAS123.Group.Group.Group;
import de.PAS123.Group.Main.Spigot.BungeeGroupManager;
import net.mmm.survival.SurvivalData;
import net.mmm.survival.regions.SurvivalWorld;
import net.mmm.survival.util.Konst;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
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
   * @param scoreboardOwner Owner des Scoreboards
   */
  public static void setScoreboard(final Player scoreboardOwner) {
    final ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
    final Scoreboard scoreboard = scoreboardManager.getNewScoreboard();

    for (final Group group : tablist.keySet()) {
      final String prefix = manager.getKurzel(group);
      scoreboard.registerNewTeam(tablist.get(group)).setPrefix(prefix);
    }

    evaluateObjectives(scoreboardOwner, scoreboard);

    scoreboardOwner.setScoreboard(scoreboard);
    determinePrefix(scoreboardOwner);
  }

  private static void evaluateObjectives(final Player scoreboardOwner, final Scoreboard scoreboard) {
    final Objective objective = scoreboard.registerNewObjective("test", "dummy");
    objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    objective.setDisplayName("   Survival   ");

    final World ownerWorld = scoreboardOwner.getWorld();
    final SurvivalWorld world = SurvivalWorld.getWorld(ownerWorld.getName());
    String worldname = null;
    if (world != null) {
      worldname = world.name();
    }


    final SurvivalPlayer survivalPlayer = SurvivalPlayer.findSurvivalPlayer(scoreboardOwner);
    final int platzierung = getPlatzierung(survivalPlayer);

    final Score scoreWelt = objective.getScore("Welt: " + worldname);
    final Score scoreGeld = objective.getScore("Geld: " + Math.round(survivalPlayer.getMoney() * 100) / 100.0 + Konst.CURRENCY);
    final Score scorePlatzierung = objective.getScore("Platzierung: #" + platzierung);
    scoreWelt.setScore(3);
    scoreGeld.setScore(2);
    scorePlatzierung.setScore(1);
  }

  private static int getPlatzierung(final SurvivalPlayer survivalPlayer) {
    //Players absteigend sortiert nach Anzahl Geld
    final List<SurvivalPlayer> playersSorted = new ArrayList<>(SurvivalData.getInstance().getPlayers().values());
    playersSorted.sort((o1, o2) -> Double.compare(o2.getMoney(), o1.getMoney()));

    int platzierung = 1;
    for (final SurvivalPlayer player : playersSorted) {
      if (player.getUuid().equals(survivalPlayer.getUuid())) {
        break;
      }
      platzierung++;
    }
    return platzierung;
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
