package net.mmm.survival.player;

import java.util.ArrayList;
import java.util.List;

import de.pas123.bungeegroupmanager.groups.Group;
import de.pas123.bungeegroupmanager.spigot.BungeeGroupManager;
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

/**
 * Scoreboards setzt die Scoreboards der Spieler
 */
public final class Scoreboards {
  public static void setScoreboards(final Player player) {
    if (SurvivalPlayer.findSurvivalPlayer(player).isScoreboard()) {
      updateScoreboard(player);
    } else {
      createScoreboard(player);
    }
    setPrefix(player);
  }

  private static void createScoreboard(final Player player) {
    final Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    final Objective objective = getObjective(scoreboard);
    objective.setDisplayName("   Survival   ");
    objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    for (final Group group : Group.values()) {
      scoreboard.registerNewTeam(group.getTabId()).setPrefix(group.getColor());
    }

    player.setScoreboard(scoreboard);
    evaluateObjectives(player, objective);
    SurvivalPlayer.findSurvivalPlayer(player).setScoreboard(true);
  }

  private static void updateScoreboard(final Player player) {
    final Scoreboard scoreboard = player.getScoreboard();
    final Objective objective = getObjective(scoreboard);
    for (final String score : objective.getScoreboard().getEntries()) {
      objective.getScoreboard().resetScores(score);
    }
    evaluateObjectives(player, objective);
  }

  private static Objective getObjective(final Scoreboard scoreboard) {
    if (scoreboard.getObjective("aaa") != null) {
      return scoreboard.getObjective("aaa");
    }
    return scoreboard.registerNewObjective("aaa", "bbb", "Stats");
  }

  private static void evaluateObjectives(final Player scoreboardOwner, final Objective objective) {
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

  private static void setPrefix(final Player p) {
    final BungeeGroupManager manager = BungeeGroupManager.getGroupManager();
    final Group group = manager.getGroup(p);

    p.setDisplayName(group.getPrefix() + p.getName());
    p.setPlayerListName(group.getPrefix() + p.getName());

    for (final Player all : Bukkit.getOnlinePlayers()) {
      if (all != null) {
        all.getScoreboard().getTeam(group.getTabId()).addEntry(p.getName());
        p.getScoreboard().getTeam(group.getTabId()).addEntry(p.getName());
      }
    }
  }
}
