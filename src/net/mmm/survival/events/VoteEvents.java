package net.mmm.survival.events;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import net.mmm.survival.SurvivalData;
import net.mmm.survival.mysql.AsyncMySQL;
import net.mmm.survival.player.SurvivalPlayer;
import net.mmm.survival.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Implementiert die Vote-Funktion
 *
 * @see com.vexsoftware.votifier.model.VotifierEvent
 */
public class VoteEvents implements Listener {
  private static final Map<String, List<Vote>> votes = new HashMap<>(); // Map mit allen Votes

  static Map<String, List<Vote>> getVotes() {
    return votes;
  }

  /**
   * @param event VotifierEvent => Tritt ein, wenn fuer den Server gevotet wird
   * @see com.vexsoftware.votifier.model.VotifierEvent
   */
  @EventHandler
  public void onVote(final VotifierEvent event) {
    final Vote vote = event.getVote();
    final String voterName = vote.getUsername();
    final Player voter = Bukkit.getPlayer(voterName);

    // Gueltiger Spieler
    if (voter != null) {
      evaluateVote(vote, voterName, voter);
    } else {
      SurvivalData.getInstance().getNamesOfPlayersToVote().add(voterName);
    }

  }

  private void evaluateVote(final Vote vote, final String voterName, final Player voter) {
    if (hasAlreadyVoted(vote)) { //schon einmal gevotet
      final List<Vote> voteList = votes.get(voterName.toLowerCase());
      voteList.add(vote);
      votes.put(voterName.toLowerCase(), voteList);
    }

    // Vote in Datenbank speichern
    final AsyncMySQL mySQL = SurvivalData.getInstance().getAsyncMySQL();
    mySQL.addVote(voter.getUniqueId(), vote.getServiceName());
    voter.sendMessage(Messages.PREFIX + " §7Danke das du für uns gevotet hast. §8[§event" +
        vote.getServiceName() + "§8]");
    updateVotes(voter);
  }

  private boolean hasAlreadyVoted(final Vote vote) {
    final String voterName = vote.getUsername();
    if (votes.containsKey(voterName.toLowerCase())) {
      return true;
    } else {
      votes.put(voterName.toLowerCase(), Collections.singletonList(vote));
    }

    return false;
  }

  private void updateVotes(final Player voter) {
    final SurvivalPlayer survivalPlayer = SurvivalPlayer.findSurvivalPlayer(voter);
    survivalPlayer.setVotes((short) (survivalPlayer.getVotes() + 1));
  }
}
