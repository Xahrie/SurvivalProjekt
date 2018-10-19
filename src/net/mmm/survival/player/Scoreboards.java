package net.mmm.survival.player;

import java.util.ArrayList;
import java.util.List;

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

  /**
   * Setzt fuer einen spezifischen Spieler {@code p} das Scoreboard um den Rang anzeigen zu lassen
   *
   * @param scoreboardOwner Owner des Scoreboards
   */
  public static void setScoreboard(final Player scoreboardOwner) {
    final SurvivalPlayer survivalPlayer = SurvivalPlayer.findSurvivalPlayer(scoreboardOwner);

    if (survivalPlayer.isScoreboard()) {
      createScoreboard(scoreboardOwner, survivalPlayer);
    } else {
      updateScoreboard(scoreboardOwner);
    }
  }

  private static void createScoreboard(final Player scoreboardOwner, final SurvivalPlayer survivalPlayer) {
    final ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
    final Scoreboard scoreboard = scoreboardManager.getNewScoreboard();

    for (final Group group : manager.tablist.keySet()) {
      final String prefix = manager.getKurzel(group);
      scoreboard.registerNewTeam(manager.tablist.get(group)).setPrefix(prefix);
    }

    evaluateObjectives(scoreboardOwner);

    scoreboardOwner.setScoreboard(scoreboard);
    determinePrefix(scoreboardOwner);

    survivalPlayer.setScoreboardTrue();
  }

  private static void updateScoreboard(final Player scoreboardOwner) {
    final Objective obj = scoreboardOwner.getScoreboard().getObjective("aaa") != null ? scoreboardOwner.getScoreboard().getObjective("aaa") : scoreboardOwner.getScoreboard().registerNewObjective("aaa", "bbb");

    for (final String score : obj.getScoreboard().getEntries()) {
      obj.getScoreboard().resetScores(score);
    }

    evaluateObjectives(scoreboardOwner);

  }

  private static void evaluateObjectives(final Player scoreboardOwner) {
    final Objective objective = scoreboardOwner.getScoreboard().getObjective("aaa") != null ? scoreboardOwner.getScoreboard().getObjective("aaa") : scoreboardOwner.getScoreboard().registerNewObjective("aaa", "bbb");
    objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    objective.setDisplayName("   Survival   ");

    final World ownerWorld = scoreboardOwner.getWorld();
    final SurvivalWorld world = SurvivalWorld.getWorld(ownerWorld.getName());


    final SurvivalPlayer survivalPlayer = SurvivalPlayer.findSurvivalPlayer(scoreboardOwner);
    final int platzierung = getPlatzierung(survivalPlayer);

    final Score scoreWelt;
    if (world != null) {
      scoreWelt = objective.getScore("Welt: " + world.name());
      scoreWelt.setScore(3);
    }
    final Score scoreGeld = objective.getScore("Geld: " + Math.round(survivalPlayer.getMoney() * 100) / 100.0 + Konst.CURRENCY);
    scoreGeld.setScore(2);
    final Score scorePlatzierung = objective.getScore("Platzierung: #" + platzierung);
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
      final String onlinePlayerTeam = manager.tablist.get(manager.getGroup(owner.getUniqueId()));
      onlinePlayerScoreboard.getTeam(onlinePlayerTeam).addPlayer(owner);

      final Scoreboard ownerScoreboard = owner.getScoreboard();
      final String ownerTeam = manager.tablist.get(manager.getGroup(onlinePlayer.getUniqueId()));
      ownerScoreboard.getTeam(ownerTeam).addPlayer(onlinePlayer);
    }
    owner.setDisplayName(manager.getPrefix(owner) + owner.getName());
    owner.setPlayerListName(manager.getPrefix(owner) + owner.getName());
  }
}
