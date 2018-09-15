package de.mmm.survival.vote;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import de.mmm.survival.Survival;
import de.mmm.survival.player.SurvivalPlayer;
import de.mmm.survival.util.ItemManager;
import de.mmm.survival.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Inplementiert die Vote-Funktion
 */
public class VotifierPlugin implements Listener {

  public static Map<String, List<Vote>> votes = new HashMap<>();

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
    if (Bukkit.getPlayer(e.getVote().getUsername()) != null) {
      vote(Bukkit.getPlayer(e.getVote().getUsername()).getUniqueId(), e.getVote().getServiceName());
      Bukkit.getPlayer(e.getVote().getUsername()).sendMessage(Messages.PREFIX + " §7Danke das du für uns gevotet hast" +
              ". §8[§e" + e.getVote().getServiceName() + "§8]");

      if (Survival.getInstance().playerList.stream().anyMatch(player -> player.getUuid().equals(Bukkit.getPlayer(e
              .getVote().getUsername()).getUniqueId()))) {
        final SurvivalPlayer survivalPlayer = Survival.getInstance().playerList.stream().filter(player -> player.getUuid()
                .equals(Bukkit.getPlayer(e.getVote().getUsername()).getUniqueId())).findFirst().get();
        survivalPlayer.setMoney(survivalPlayer.getMoney() + 1);
      } else {
        final SurvivalPlayer survivalPlayer = new SurvivalPlayer(Bukkit.getPlayer(e.getVote().getUsername())
                .getUniqueId(), 0, new ArrayList<>(), new ArrayList<>(), (short) 1);
        Survival.getInstance().playerList.add(survivalPlayer);
      }

      Bukkit.getPlayer(e.getVote().getUsername()).getInventory().addItem(ItemManager.build(Material.IRON_NUGGET,
              "§cMünze", Collections.singletonList("§7§oDu kannst diese Münzen beim Markt eintauschen.")));
    } else {
      votes.put(e.getVote().getUsername().toLowerCase(), votes.containsKey(e.getVote().getUsername().toLowerCase()) ?
              votes.put(e.getVote().getUsername().toLowerCase(), add(votes.get(e.getVote().getUsername().toLowerCase
                      ()), e.getVote())) : add(Collections.emptyList(), e.getVote()));
    }

  }

  private List<Vote> add(final List<Vote> list, final Vote add) {
    list.add(add);
    return list;
  }

}
