package de.mmm.survival.events;

import de.mmm.survival.Survival;
import de.mmm.survival.player.SurvivalPlayer;
import de.mmm.survival.util.Cache;
import de.mmm.survival.util.ItemManager;
import de.mmm.survival.util.Messages;
import de.mmm.survival.util.Scoreboards;
import de.mmm.survival.vote.VotifierPlugin;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Events, wenn ein Spieler eine Verbindung aufbaut oder trennt
 *
 * @see org.bukkit.event.player.PlayerJoinEvent
 * @see org.bukkit.event.player.PlayerQuitEvent
 */
public class PlayerConnectionEvents implements Listener {

  /**
   * Wenn ein Spieler den Server betritt
   *
   * @param e PlayerJoinEvent
   * @see org.bukkit.event.player.PlayerJoinEvent
   */
  @EventHandler
  public void onJoin(final PlayerJoinEvent e) {
    SurvivalPlayer survivalPlayer = SurvivalPlayer.findSurvivalPlayer(e.getPlayer());
    if (survivalPlayer == null) { // First-Join
      survivalPlayer = new SurvivalPlayer(e.getPlayer().getUniqueId(), 0, new ArrayList<>(), new ArrayList<>(),
              (short) 0);

      Survival.getInstance().players.put(e.getPlayer().getUniqueId(), survivalPlayer);
    }


    e.setJoinMessage(null);
    Scoreboards.setScoreboard(e.getPlayer());

    //Vote-Plugin
    if (VotifierPlugin.votes.containsKey(e.getPlayer().getName().toLowerCase())) {
      final SurvivalPlayer finalSurvivalPlayer = survivalPlayer;
      VotifierPlugin.votes.get(e.getPlayer().getName().toLowerCase()).forEach(vote -> {
        e.getPlayer().sendMessage(Messages.PREFIX + " §7Danke das du für uns gevotest hast. §8[§e" + vote
                .getServiceName() + "§8]");

        //wenn Player-UUID in Players
        finalSurvivalPlayer.setMoney(finalSurvivalPlayer.getMoney() + Cache.VOTE_REWARD);

        VotifierPlugin.vote(e.getPlayer().getUniqueId(), vote.getServiceName());
        e.getPlayer().getInventory().addItem(ItemManager.build(Material.IRON_NUGGET, "§cMünze", Collections
                .singletonList("§7§oDu kannst diese Münzen beim Markt eintauschen.")));
      });

      VotifierPlugin.votes.remove(e.getPlayer().getName().toLowerCase());
    }

  }

  /**
   * Wenn ein Spieler den Server verlaesst
   *
   * @param e PlayerQuitEvent
   * @see org.bukkit.event.player.PlayerQuitEvent
   */
  @EventHandler
  public void onQuit(final PlayerQuitEvent e) {
    final SurvivalPlayer survivalPlayer = SurvivalPlayer.findSurvivalPlayer(e.getPlayer());
    survivalPlayer.setZonensearch(false);

    e.setQuitMessage(null);
  }

}
