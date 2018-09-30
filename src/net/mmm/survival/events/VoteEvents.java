package net.mmm.survival.events;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import net.mmm.survival.SurvivalData;
import net.mmm.survival.player.SurvivalPlayer;
import net.mmm.survival.util.ItemManager;
import net.mmm.survival.util.Konst;
import net.mmm.survival.util.Messages;
import net.mmm.survival.util.UUIDUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Implementiert die Vote-Funktion
 *
 * @see com.vexsoftware.votifier.model.VotifierEvent
 */
public class VoteEvents implements Listener {
  private static final Map<String, List<Vote>> VOTES = new HashMap<>(); // Map mit allen Votes

  /**
   * @param event VotifierEvent -> Tritt ein, wenn fuer den Server gevotet wird
   * @see com.vexsoftware.votifier.model.VotifierEvent
   */
  @EventHandler
  public void onVote(final VotifierEvent event) {
    final Player player = UUIDUtils.getPlayer(event.getVote().getUsername());

    if (player != null) { // Gueltiger Spieler
      if (VOTES.containsKey(event.getVote().getUsername().toLowerCase())) { //schon einmal gevotet
        final List<Vote> voteList = VOTES.get(event.getVote().getUsername().toLowerCase());
        voteList.add(event.getVote());
        VOTES.put(event.getVote().getUsername().toLowerCase(), voteList);
      } else {
        VOTES.put(event.getVote().getUsername().toLowerCase(),
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
    rewardVoter(voter, survivalPlayer);
  }

  private void rewardVoter(final Player voter, final SurvivalPlayer survivalPlayer) {
    survivalPlayer.setMoney(survivalPlayer.getMoney() + Konst.VOTE_REWARD);
    voter.getInventory().addItem(ItemManager.build(Material.IRON_NUGGET, "§cMünze",
        Collections.singletonList(Messages.VOTE_REWARD)));
  }

  static Map<String, List<Vote>> getVotes() {
    return VOTES;
  }
}
