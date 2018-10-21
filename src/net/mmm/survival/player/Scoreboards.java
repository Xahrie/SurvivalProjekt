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


  public static void setScoreboards(Player p) {
    if (SurvivalPlayer.findSurvivalPlayer(p).isScoreboard()) {

      Objective obj = p.getScoreboard().getObjective("aaa") != null ? p.getScoreboard().getObjective("aaa") : p.getScoreboard().registerNewObjective("aaa", "bbb");
      for (String score : obj.getScoreboard().getEntries()) {
        obj.getScoreboard().resetScores(score);
      }
      obj.getScore("Geld: " + SurvivalPlayer.findSurvivalPlayer(p).getMoney()).setScore(1);


    } else {
      Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();

      Objective obj = sb.getObjective("aaa") != null ? sb.getObjective("aaa") : sb.registerNewObjective("aaa", "bbb");

      obj.setDisplayName("test");
      obj.setDisplaySlot(DisplaySlot.SIDEBAR);

      BungeeGroupManager manager = BungeeGroupManager.getGroupManager();

      for (Group group : manager.tablist.keySet()) {
        sb.registerNewTeam("" + manager.tablist.get(group)).setPrefix(BungeeGroupManager.getGroupManager().getKurzel(group));
      }

      obj.getScore("Geld: " + SurvivalPlayer.findSurvivalPlayer(p).getMoney()).setScore(1);

      p.setScoreboard(sb);
      setPrefix(p, BungeeGroupManager.getGroupManager().getGroup(p));
      SurvivalPlayer.findSurvivalPlayer(p).setScoreboard(true);
    }
  }

  private static void setPrefix(final Player p, final Group group) {
    final BungeeGroupManager manager = BungeeGroupManager.getGroupManager();
    p.setDisplayName(manager.getPrefix(group) + p.getName());
    p.setPlayerListName(manager.getPrefix(group) + p.getName());

    for (final Player all : Bukkit.getOnlinePlayers()) {
      if (all != null) {
        all.getScoreboard().getTeam(manager.tablist.get(group)).addPlayer(p);
        p.getScoreboard().getTeam(manager.tablist.get(group)).addPlayer(all);
      }
    }
  }


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

    scoreboardOwner.setScoreboard(scoreboard);
    evaluateObjectives(scoreboardOwner);

    determinePrefix(scoreboardOwner);

    survivalPlayer.setScoreboard(true);
  }

  private static void updateScoreboard(final Player scoreboardOwner) {
    final Objective obj = getObjective(scoreboardOwner);

    for (final String score : obj.getScoreboard().getEntries()) {
      obj.getScoreboard().resetScores(score);
    }

    evaluateObjectives(scoreboardOwner);

  }

  private static void evaluateObjectives(final Player scoreboardOwner) {
    final Objective objective = getObjective(scoreboardOwner);
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

  private static Objective getObjective(final Player scoreboardOwner) {
    final String sKey = scoreboardOwner.getEntityId() + "_aaa";
    return scoreboardOwner.getScoreboard().getObjective(sKey) != null ? scoreboardOwner.getScoreboard().getObjective(sKey) : scoreboardOwner.getScoreboard().registerNewObjective(sKey, "bbb");
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
