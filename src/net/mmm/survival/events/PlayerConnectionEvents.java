package net.mmm.survival.events;

import java.util.ArrayList;
import java.util.Collections;

import net.mmm.survival.SurvivalData;
import net.mmm.survival.player.SurvivalPlayer;
import net.mmm.survival.util.ItemManager;
import net.mmm.survival.util.Konst;
import net.mmm.survival.util.Messages;
import net.mmm.survival.util.Scoreboards;
import net.mmm.survival.vote.VotifierPlugin;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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
    final SurvivalPlayer survivalPlayer = SurvivalPlayer.findSurvivalPlayer(e.getPlayer());

    isFirstJoin(survivalPlayer, e);
    e.setJoinMessage(null);
    Scoreboards.setScoreboard(e.getPlayer()); //Scoreboard initialisieren
    verarbeiteVotes(e, survivalPlayer);     //Vote-Plugin
  }

  private void isFirstJoin(SurvivalPlayer survivalPlayer, final PlayerJoinEvent event) {
    if (survivalPlayer == null) { // First-Join
      survivalPlayer = new SurvivalPlayer(event.getPlayer().getUniqueId(), 0, new ArrayList<>(), new ArrayList<>(), (short) 0, 20, null);
      SurvivalData.getInstance().getAsyncMySQL().createPlayer(survivalPlayer);
      SurvivalData.getInstance().getPlayers().put(event.getPlayer().getUniqueId(), survivalPlayer);
    }
  }

  private void verarbeiteVotes(final PlayerJoinEvent e, final SurvivalPlayer survivalPlayer) {
    if (VotifierPlugin.votes.containsKey(e.getPlayer().getName().toLowerCase())) {
      VotifierPlugin.votes.get(e.getPlayer().getName().toLowerCase()).forEach(vote -> {
        e.getPlayer().sendMessage(Messages.PREFIX + " §7Danke das du für uns gevotest hast. §8[§e" + vote.getServiceName() + "§8]");
        survivalPlayer.setMoney(survivalPlayer.getMoney() + Konst.VOTE_REWARD); //wenn Player-UUID in Players
        VotifierPlugin.vote(e.getPlayer().getUniqueId(), vote.getServiceName());
        e.getPlayer().getInventory().addItem(ItemManager.build(Material.IRON_NUGGET, "§cMünze", Collections.singletonList("§7§oDu kannst " +
            "diese Münzen beim Markt eintauschen.")));
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
