package net.mmm.survival.events;

import java.util.ArrayList;
import java.util.Collections;

import com.vexsoftware.votifier.model.Vote;
import net.mmm.survival.SurvivalData;
import net.mmm.survival.player.Complaint;
import net.mmm.survival.player.SurvivalPlayer;
import net.mmm.survival.util.ItemManager;
import net.mmm.survival.util.Konst;
import net.mmm.survival.util.Messages;
import net.mmm.survival.util.Scoreboards;
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
   * @param event PlayerJoinEvent -> Wenn ein Spieler den Server betritt
   * @see org.bukkit.event.player.PlayerJoinEvent
   */
  @EventHandler
  public void onJoin(final PlayerJoinEvent event) {
    SurvivalData.getInstance().getAsyncMySQL().updatePlayer(event.getPlayer()); // muss oben stehen
    final SurvivalPlayer joined = SurvivalPlayer.findSurvivalPlayer(event.getPlayer());

    isFirstJoin(joined, event);
    event.setJoinMessage(null);
    Scoreboards.setScoreboard(event.getPlayer()); //Scoreboard initialisieren
    handleVotes(event, joined); //Vote-Plugin
    handleComplaints(joined); //Beschwerden
  }

  private void handleComplaints(final SurvivalPlayer joined) {
    if (joined.getComplaints().size() > 0) {
      joined.getPlayer().sendMessage(Messages.COMPLAINT_INFO);
      for (final Complaint complaint : joined.getComplaints()) {
        joined.outputComplaint(complaint);
      }
      //TODO (BlueIronGirl) 30.09.2018: bei Admins immer alle offenen Beschwerden anzeigen, aber kumuliert.
    }
  }

  private void handleVotes(final PlayerJoinEvent event, final SurvivalPlayer joined) {
    if (VoteEvents.getVotes().containsKey(joined.getPlayer().getName().toLowerCase())) {
      for (final Vote vote : VoteEvents.getVotes().get(event.getPlayer().getName().toLowerCase())) {
        joined.getPlayer().sendMessage(Messages.PREFIX + " §7Danke das du für uns gevotest hast. §8[§e" +
            vote.getServiceName() + "§8]");
        joined.setMoney(joined.getMoney() + Konst.VOTE_REWARD); //wenn Player-UUID in Players
        joined.getPlayer().getInventory().addItem(ItemManager.build(Material.IRON_NUGGET, "§cMünze",
            Collections.singletonList(Messages.VOTE_REWARD)));
      }
      VoteEvents.getVotes().remove(joined.getPlayer().getName().toLowerCase());
    }
  }

  private void isFirstJoin(SurvivalPlayer joined, final PlayerJoinEvent event) {
    if (joined == null) { // First-Join
      joined = new SurvivalPlayer(event.getPlayer().getUniqueId(), 0, new ArrayList<>(),
          new ArrayList<>(), (short) 0, Konst.ZONE_SIZE_DEFAULT, null);
      SurvivalData.getInstance().getAsyncMySQL().createPlayer(joined);
      SurvivalData.getInstance().getPlayers().put(joined.getUuid(), joined);
    }
  }

  /**
   * Wenn ein Spieler den Server verlaesst
   *
   * @param event PlayerQuitEvent
   * @see org.bukkit.event.player.PlayerQuitEvent
   */
  @EventHandler
  public void onQuit(final PlayerQuitEvent event) {
    final SurvivalPlayer quited = SurvivalPlayer.findSurvivalPlayer(event.getPlayer());
    quited.setZonensearch(false);
    event.setQuitMessage(null);
  }

}
