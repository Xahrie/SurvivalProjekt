package net.mmm.survival.player;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.mmm.survival.SurvivalData;
import net.mmm.survival.farming.PlayerStats;
import net.mmm.survival.util.Konst;
import net.mmm.survival.util.Messages;
import net.mmm.survival.util.UUIDUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * SurvivalPlayer speichert ueber die uuid den Geldbetrag, Beschwerden,
 * Lizenzen, Votes, Maxzone und Homepunkt
 *
 * @author BlueIronGirl, Abgie
 */
public class SurvivalPlayer {
  private final Date lastComplaint;
  private final List<Complaint> complaints;
  private final PlayerStats stats;
  private final LevelPlayer levelPlayer;
  private final List<SurvivalLicence> licences;
  private boolean tamed, teleport, zonenedit, zonensearch, scoreboard;
  //<editor-fold desc="mysql parameter">
  private double money;
  private int maxzone;
  private short votes;
  private Location home;
  private UUID uuid;
  //</editor-fold>

  /**
   * Konstruktor
   *
   * @param uuid Eindeutige Id
   * @param money Geld
   * @param complaints Beschwerden
   * @param licences Lizenzen
   * @param votes Anzahl Votes
   * @param maxzone Maximal zulaessige Groeße der Zone
   * @param home Homepunkt
   */
  public SurvivalPlayer(final UUID uuid, final double money, final List<Complaint> complaints, final List<SurvivalLicence> licences,
                        final short votes, final int maxzone, final Location home, final LevelPlayer levelPlayer) {
    this.uuid = uuid;
    this.money = money;
    this.complaints = complaints;
    this.licences = licences;
    this.votes = votes;
    this.maxzone = maxzone;
    this.home = home;

    this.levelPlayer = levelPlayer;

    final Date lastComplaint = new Date(); // Setze Datum der letzten Beschwerde
    this.lastComplaint = new Date(lastComplaint.getTime() - Konst.COMPLAIN_HALBE_STUNDE);
    this.stats = new PlayerStats(); // Erstelle Statistiken
  }

  /**
   * Ermittle den SurvivalPlayer aus dem SpielerCache
   *
   * @param player Ausfuehrender Spieler => Name des Spielers wird zur Ermittlung genommen
   * @return SurvivalPlayer von {@code player}. Wenn nicht vorhanden, dann
   * {@code return null}
   */
  public static SurvivalPlayer findSurvivalPlayer(final Player player) {
    return findSurvivalPlayer(player, player.getName());
  }

  public static SurvivalPlayer findSurvivalPlayer(final UUID uuid) {
    final Map<UUID, SurvivalPlayer> players = SurvivalData.getInstance().getPlayers();
    if (players.containsKey(uuid)) {
      return players.get(uuid);
    }
    return null;
  }

  /**
   * Ermittle den SurvivalPlayer aus dem SpielerCache
   *
   * @param executor Ausfuehrender Spieler
   * @param playerName Spielername
   * @return SurvivalPlayer von {@code player}. Wenn nicht vorhanden, dann
   * {@code return null}
   */
  public static SurvivalPlayer findSurvivalPlayer(final Player executor, final String playerName) {
    final SurvivalPlayer survivalPlayer = determinePlayer(playerName);
    if (survivalPlayer == null) {
      executor.sendMessage(Messages.PLAYER_NOT_FOUND);
    }
    return survivalPlayer;
  }

  private static SurvivalPlayer determinePlayer(final String playerName) {
    final Map<UUID, String> playerCache = SurvivalData.getInstance().getPlayerCache();
    if (playerCache.containsValue(playerName)) {
      final UUID uuid = UUIDUtils.getUUID(playerName);
      final Map<UUID, SurvivalPlayer> players = SurvivalData.getInstance().getPlayers();
      if (players.containsKey(uuid)) {
        return players.get(uuid);
      }
    }
    return null;
  }

  public Player getPlayer() {
    return UUIDUtils.getPlayer(this.uuid);
  }

  public void addComplaint(final Complaint complaint) {
    complaints.add(complaint);
    outputComplaint(complaint, getPlayer());
  }

  public void addOrTakeMoney(final double amount) {
    money += amount;
  }

  public void outputComplaint(final Complaint complaint, final Player player) {
    final Map<UUID, SurvivalPlayer> players = SurvivalData.getInstance().getPlayers();
    final Player complaintExecutor = players.get(complaint.getExecutor()).getPlayer();
    if (player != null) { //online ?
      if (getUuid().equals(player.getUniqueId())) {
        player.sendMessage(Messages.PREFIX + "§c Der Spieler §e" + complaintExecutor.getDisplayName() +
            "§c hat sich am §e" + complaint.outputDate() + "§c über dich beschwert: §e" + complaint.getReason());
      } else {
        player.sendMessage(Messages.PREFIX + "§c Der Spieler §e" + complaintExecutor.getDisplayName() +
            "§c hat sich am §e" + complaint.outputDate() + "§c über " + UUIDUtils.getName(getUuid()) + " beschwert: §e" + complaint.getReason());
      }
    }
  }
  //<editor-fold desc="getter and setter">

  public List<Complaint> getComplaints() {
    return complaints;
  }

  public Location getHome() {
    return home;
  }

  public void setHome(final Location home) {
    this.home = home;
  }

  public Date getLastComplaint() {
    return lastComplaint;
  }

  public List<SurvivalLicence> getLicences() {
    return licences;
  }

  public int getMaxzone() {
    return maxzone;
  }

  public void setMaxzone(final int maxzone) {
    this.maxzone = maxzone;
  }

  public double getMoney() {
    return money;
  }

  public void setMoney(final double money) {
    this.money = money;
  }

  public PlayerStats getStats() {
    return stats;
  }

  public UUID getUuid() {
    return uuid;
  }

  public void setUuid(final UUID uuid) {
    this.uuid = uuid;
  }

  public LevelPlayer getLevelPlayer() {
    return this.levelPlayer;
  }

  public short getVotes() {
    return votes;
  }

  public void setVotes(final short votes) {
    this.votes = votes;
  }

  boolean isScoreboard() {
    return scoreboard;
  }

  void setScoreboardTrue() {
    this.scoreboard = true;
  }

  public boolean isTamed() {
    return tamed;
  }

  public void setTamed(final boolean tamed) {
    this.tamed = tamed;
  }

  public boolean isTeleport() {
    return teleport;
  }

  public void setTeleport(final boolean teleport) {
    this.teleport = teleport;
  }

  public boolean isZonenedit() {
    return zonenedit;
  }

  public void setZonenedit(final boolean zonenedit) {
    this.zonenedit = zonenedit;
  }

  public boolean isZonensearch() {
    return zonensearch;
  }

  public void setZonensearch(final boolean zonensearch) {
    this.zonensearch = zonensearch;
  }

  public boolean hasLicence(final SurvivalLicence licence) {
    return licences.contains(licence);
  }

  //</editor-fold>
}
