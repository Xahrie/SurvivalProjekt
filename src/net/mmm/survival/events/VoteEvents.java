package net.mmm.survival.events;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import net.mmm.survival.SurvivalData;
import net.mmm.survival.player.SurvivalPlayer;
import net.mmm.survival.util.Messages;
import net.mmm.survival.util.UUIDUtils;
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

  /**
   * @param event VotifierEvent -> Tritt ein, wenn fuer den Server gevotet wird
   * @see com.vexsoftware.votifier.model.VotifierEvent
   */
  @EventHandler
  public void onVote(final VotifierEvent event) {
    final Player player = UUIDUtils.getPlayer(event.getVote().getUsername());

    if (player != null) { // Gueltiger Spieler
      if (votes.containsKey(event.getVote().getUsername().toLowerCase())) { //schon einmal gevotet
        final List<Vote> voteList = votes.get(event.getVote().getUsername().toLowerCase());
        voteList.add(event.getVote());
        votes.put(event.getVote().getUsername().toLowerCase(), voteList);
      } else {
        votes.put(event.getVote().getUsername().toLowerCase(),
            Collections.singletonList(event.getVote()));
      }
      // Vote in Datenbank speichern
      SurvivalData.getInstance().getAsyncMySQL().addVote(player.getUniqueId(), event.getVote().getServiceName());
      player.sendMessage(Messages.PREFIX + " §7Danke das du für uns gevotet hast. §8[§event" +
          event.getVote().getServiceName() + "§8]");
      updateVotes(player);
    }

  }

  private void updateVotes(final Player voter) {
    final SurvivalPlayer survivalPlayer = SurvivalPlayer.findSurvivalPlayer(voter);
    survivalPlayer.setVotes((short) (survivalPlayer.getVotes() + 1));
  }

  static Map<String, List<Vote>> getVotes() {
    return votes;
  }
}
