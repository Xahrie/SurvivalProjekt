package net.mmm.survival.vote;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import net.mmm.survival.SurvivalData;
import net.mmm.survival.player.SurvivalPlayer;
import net.mmm.survival.util.ItemManager;
import net.mmm.survival.util.Konst;
import net.mmm.survival.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Implementiert die Vote-Funktion
 */
public class VotifierPlugin implements Listener {
  public static Map<String, List<Vote>> votes = new HashMap<>();

  /**
   * Spieler votet auf bestimmter Webseite fuer unseren Server
   *
   * @param uuid UUID des Spielers
   * @param website Webseite
   */
  public static void vote(final UUID uuid, final String website) {
    SurvivalData.getInstance().getAsyncMySQL().addVote(uuid, website);
  }

  /**
   * Event, wenn gevotet wird
   *
   * @param e VotifierEvent
   */
  @EventHandler
  public void onVote(final VotifierEvent e) {
    final Player player = Bukkit.getPlayer(e.getVote().getUsername());

    if (checkPlayer(player, e)) {
      vote(player.getUniqueId(), e.getVote().getServiceName());
      player.sendMessage(Messages.PREFIX + " §7Danke das du für uns gevotet hast. §8[§e" + e.getVote().getServiceName() + "§8]");
      updateVotes(player);
    }

  }

  private void updateVotes(final Player player) {
    final SurvivalPlayer survivalPlayer = SurvivalPlayer.findSurvivalPlayer(player);
    survivalPlayer.setVotes((short) (survivalPlayer.getVotes() + 1));
    survivalPlayer.setMoney(survivalPlayer.getMoney() + Konst.VOTE_REWARD);
    player.getInventory().addItem(ItemManager.build(Material.IRON_NUGGET, "§cMünze", Collections.singletonList(Messages.VOTE_REWARD)));
  }

  private boolean checkPlayer(final Player player, final VotifierEvent event) {
    if (player != null) {
      return true;
    } else if (votes.containsKey(event.getVote().getUsername().toLowerCase())) {
        votes.put(event.getVote().getUsername().toLowerCase(), votes.put(event.getVote().getUsername().toLowerCase(), add(votes.get(event
            .getVote().getUsername().toLowerCase()), event.getVote())));
    } else {
        votes.put(event.getVote().getUsername().toLowerCase(), add(Collections.emptyList(), event.getVote()));
    }
    return false;
  }

  private List<Vote> add(final List<Vote> list, final Vote add) {
    list.add(add);
    return list;
  }

}
