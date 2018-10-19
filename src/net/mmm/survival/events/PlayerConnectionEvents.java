package net.mmm.survival.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.vexsoftware.votifier.model.Vote;
import net.mmm.survival.SurvivalData;
import net.mmm.survival.mysql.AsyncMySQL;
import net.mmm.survival.player.Complaint;
import net.mmm.survival.player.LevelPlayer;
import net.mmm.survival.player.Scoreboards;
import net.mmm.survival.player.SurvivalPlayer;
import net.mmm.survival.regions.SurvivalWorld;
import net.mmm.survival.util.CommandUtils;
import net.mmm.survival.util.ItemManager;
import net.mmm.survival.util.Konst;
import net.mmm.survival.util.Messages;
import net.mmm.survival.util.UUIDUtils;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.PlayerInventory;

/**
 * Events, wenn ein Spieler eine Verbindung aufbaut oder trennt
 *
 * @see org.bukkit.event.player.PlayerJoinEvent
 * @see org.bukkit.event.player.PlayerQuitEvent
 */
public class PlayerConnectionEvents implements Listener {

  /**
   * @param event PlayerJoinEvent => Wenn ein Spieler den Server betritt
   * @see org.bukkit.event.player.PlayerJoinEvent
   */
  @EventHandler
  public void onJoin(final PlayerJoinEvent event) {
    final Player player = event.getPlayer();
    final SurvivalPlayer joinedPlayer = updatePlayer(player);

    event.setJoinMessage(null);

    evaluateFirstJoin(joinedPlayer, event);

    //Scoreboard initialisieren
    Scoreboards.setScoreboard(event.getPlayer());

    //Vote-Plugin
    evaluateVotes(joinedPlayer);

    //Beschwerden
    evaluateComplaints(joinedPlayer);
  }

  private SurvivalPlayer updatePlayer(final Player player) {
    //Datenbankinfos aktualisieren
    final AsyncMySQL mySQL = SurvivalData.getInstance().getAsyncMySQL();
    mySQL.updatePlayer(player);

    //Globale Liste aktualisieren
    final Map<UUID, String> playerCache = SurvivalData.getInstance().getPlayerCache();
    playerCache.put(player.getUniqueId(), player.getName());

    //Listen Objekt zurueckliefern
    return SurvivalPlayer.findSurvivalPlayer(player);
  }

  private void evaluateComplaints(final SurvivalPlayer joined) {
    final Player joinedPlayer = joined.getPlayer();

    //Offene Beschwerden ueber den eingeloggten Player
    if (!joined.getComplaints().isEmpty()) {
      joinedPlayer.sendMessage(Messages.COMPLAINT_INFO);
      for (final Complaint complaint : joined.getComplaints()) {
        joined.outputComplaint(complaint, joinedPlayer);
      }
    }

    //Generell offene Beschwerden
    if (CommandUtils.isOperator(joinedPlayer)) {
      joinedPlayer.sendMessage(Messages.PREFIX + "Offene Beschwerden (Details koennen uber /complain list <Spieler> aufgelistet werden):");
      boolean bFound = false;
      for (final SurvivalPlayer survivalPlayer : SurvivalData.getInstance().getPlayers().values()) {
        if (!survivalPlayer.getComplaints().isEmpty()) {
          final int amoutComplaints = survivalPlayer.getComplaints().size();
          joinedPlayer.sendMessage(Messages.PREFIX + "Spieler " + UUIDUtils.getName(survivalPlayer.getUuid()) + ": " + amoutComplaints);
          bFound = true;
        }
      }
      if (!bFound) {
        joinedPlayer.sendMessage(Messages.PREFIX + "Keine offenen Beschwerden");
      }
    }
  }

  private void evaluateVotes(final SurvivalPlayer joined) {
    final Map<String, List<Vote>> votes = VoteEvents.getVotes();
    final Player joinedPlayer = joined.getPlayer();
    final String joinedPlayerName = joinedPlayer.getName();

    if (votes.containsKey(joinedPlayerName.toLowerCase())) {
      for (final Vote vote : votes.get(joinedPlayerName.toLowerCase())) {
        joinedPlayer.sendMessage(Messages.PREFIX + " §7Danke das du für uns gevotest hast. §8[§e" +
            vote.getServiceName() + "§8]");
        joined.setMoney(joined.getMoney() + Konst.VOTE_REWARD); //wenn Player-UUID in Players
        final PlayerInventory joinedPlayerInventory = joinedPlayer.getInventory();
        joinedPlayerInventory.addItem(ItemManager.build(Material.IRON_NUGGET, "§cMünze",
            Collections.singletonList(Messages.VOTE_REWARD)));
      }

      votes.remove(joinedPlayerName.toLowerCase());
    }
  }

  private void evaluateFirstJoin(SurvivalPlayer joined, final PlayerJoinEvent event) {
    if (joined == null) { // First-Join
      final Player eventPlayer = event.getPlayer();
      final World world = SurvivalWorld.BAUWELT.get();
      joined = new SurvivalPlayer(eventPlayer.getUniqueId(), 0, new ArrayList<>(), new ArrayList<>(),
          (short) 0, Konst.ZONE_SIZE_DEFAULT, world.getSpawnLocation(), new LevelPlayer());

      final AsyncMySQL mySQL = SurvivalData.getInstance().getAsyncMySQL();
      mySQL.createPlayer(joined);

      final Map<UUID, SurvivalPlayer> survivalPlayers = SurvivalData.getInstance().getPlayers();
      survivalPlayers.put(joined.getUuid(), joined);
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
