package net.mmm.survival.events;

import java.util.ArrayList;
import java.util.Collections;

import net.mmm.survival.SurvivalData;
import net.mmm.survival.farming.Type;
import net.mmm.survival.player.SurvivalPlayer;
import net.mmm.survival.util.ItemManager;
import net.mmm.survival.util.Konst;
import net.mmm.survival.util.Messages;
import net.mmm.survival.util.Scoreboards;
import net.mmm.survival.util.SurvivalWorld;
import org.bukkit.Material;
import org.bukkit.World;
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
    final SurvivalPlayer joined = SurvivalPlayer
        .findSurvivalPlayer(event.getPlayer(), event.getPlayer().getName());

    isFirstJoin(joined, event);
    event.setJoinMessage(null);
    Scoreboards.setScoreboard(event.getPlayer()); //Scoreboard initialisieren
    handleVotes(event, joined);     //Vote-Plugin
    handleComplaints(joined);
    checkWorld(joined);
  }

  private void checkWorld(final SurvivalPlayer joined) {
    final World joinedWorld = joined.getPlayer().getWorld();
    if (joinedWorld.equals(SurvivalWorld.FARMWELT.get())) {
      joined.getStats().getStatistic(Type.WALK_LENGTH_CM).calculate(joined);
    }
  }

  private void handleComplaints(final SurvivalPlayer joined) {
    if (joined.getComplaints().size() > 0) {
      joined.getPlayer().sendMessage(Messages.COMPLAINT_INFO);
      joined.getComplaints().forEach(joined::outputComplaint);
    }
  }

  private void handleVotes(final PlayerJoinEvent event, final SurvivalPlayer joined) {
    if (VoteEvents.getVotes().containsKey(joined.getPlayer().getName().toLowerCase())) {
      VoteEvents.getVotes().get(event.getPlayer().getName().toLowerCase()).forEach(vote -> {
        joined.getPlayer().sendMessage(Messages.PREFIX + " §7Danke das du für uns gevotest hast. §8[§e" +
            vote.getServiceName() + "§8]");
        joined.setMoney(joined.getMoney() + Konst.VOTE_REWARD); //wenn Player-UUID in Players
        VoteEvents.addVote(joined.getUuid(), vote.getServiceName());
        joined.getPlayer().getInventory().addItem(ItemManager.build(Material.IRON_NUGGET, "§cMünze",
            Collections.singletonList(Messages.VOTE_REWARD)));
      });

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
    final SurvivalPlayer quited = SurvivalPlayer
        .findSurvivalPlayer(event.getPlayer(), event.getPlayer().getName());
    quited.setZonensearch(false);
    event.setQuitMessage(null);
  }

}
