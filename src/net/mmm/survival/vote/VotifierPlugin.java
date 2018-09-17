package net.mmm.survival.vote;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.vexsoftware.votifier.model.VotifierEvent;
import net.mmm.survival.Survival;
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

  public static Map<String, List<com.vexsoftware.votifier.model.Vote>> votes = new HashMap<>();

  public static void vote(final UUID uuid, final String website) {
    Survival.getInstance().async.addVote(uuid, website);
  }

  /**
   * Event, wenn gevotet wird
   *
   * @param e VotifierEvent
   */
  @EventHandler
  public void onVote(final VotifierEvent e) {
    final Player player = Bukkit.getPlayer(e.getVote().getUsername());
    if (player != null) {
      vote(player.getUniqueId(), e.getVote().getServiceName());
      player.sendMessage(Messages.PREFIX + " §7Danke das du für uns gevotet hast. §8[§e" + e.getVote().getServiceName
          () + "§8]");

      SurvivalPlayer survivalPlayer = SurvivalPlayer.findSurvivalPlayer(player);
      if (survivalPlayer != null) {
        survivalPlayer.setVotes((short) (survivalPlayer.getVotes() + 1));
        survivalPlayer.setMoney(survivalPlayer.getMoney() + Konst.VOTE_REWARD);
      } else {
        survivalPlayer = new SurvivalPlayer(player.getUniqueId(), Konst.VOTE_REWARD, new ArrayList<>(), new
            ArrayList<>(), (short) 1, 100, null);
        Survival.getInstance().async.createPlayer(survivalPlayer);

        Survival.getInstance().players.put(player.getUniqueId(), survivalPlayer);
      }

      player.getInventory().addItem(ItemManager.build(Material.IRON_NUGGET,
          "§cMünze", Collections.singletonList("§7§oDu kannst diese Münzen beim Markt eintauschen.")));
    } else {
      votes.put(e.getVote().getUsername().toLowerCase(), votes.containsKey(e.getVote().getUsername().toLowerCase()) ?
          votes.put(e.getVote().getUsername().toLowerCase(), add(votes.get(e.getVote().getUsername().toLowerCase
              ()), e.getVote())) : add(Collections.emptyList(), e.getVote()));
    }

  }

  /**
   * Vote hinzufuegen
   *
   * @param list Liste mit Votes
   * @param add Vote, der hinzukommen soll
   * @return Liste mit Votes
   */
  private List<com.vexsoftware.votifier.model.Vote> add(final List<com.vexsoftware.votifier.model.Vote> list,
                                                        final com.vexsoftware.votifier.model.Vote add) {
    list.add(add);
    return list;
  }

}
