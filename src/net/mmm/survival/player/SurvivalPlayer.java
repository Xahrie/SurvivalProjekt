package net.mmm.survival.player;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import net.mmm.survival.SurvivalData;
import net.mmm.survival.farming.PlayerStats;
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
public class SurvivalPlayer extends HotbarMessager {
  private final Date lastComplaint;
  private final List<Complaint> complaints;
  private List<SurvivalLicense> licences;
  private final PlayerStats stats;
  private boolean tamed, teleport, zonenedit, zonensearch;
  //<editor-fold desc="mysql parameter">
  private double money;
  private int maxzone;
  private short votes;
  private Location home;
  private UUID uuid;

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
  public SurvivalPlayer(final UUID uuid, final double money, final List<Complaint> complaints, final List<SurvivalLicense> licences, final short votes,
                        final int maxzone, final Location home) {
    this.uuid = uuid;
    this.money = money;
    this.complaints = complaints;
    this.licences = licences;
    this.votes = votes;
    this.maxzone = maxzone;
    this.home = home;

    this.lastComplaint = new Date(); // Setze Datum der letzten Beschwerde
    this.stats = new PlayerStats(); // Erstelle Statistiken
  }

  /**
   * Ermittle den SurvivalPlayer aus dem SpielerCache
   *
   * @param player Ausfuehrender Spieler -> Name des Spielers wird zur Ermittlung genommen
   * @return SurvivalPlayer von <code>player</code>. Wenn nicht vorhanden, dann
   * <code>return null</code>
   */
  public static SurvivalPlayer findSurvivalPlayer(final Player player) {
    return findSurvivalPlayer(player, player.getName());
  }

  /**
   * Ermittle den SurvivalPlayer aus dem SpielerCache
   *
   * @param executor Ausfuehrender Spieler
   * @param playerName Spielername
   * @return SurvivalPlayer von <code>player</code>. Wenn nicht vorhanden, dann
   * <code>return null</code>
   */
  public static SurvivalPlayer findSurvivalPlayer(final Player executor, final String playerName) {
    final SurvivalPlayer survivalPlayer = determinePlayer(playerName);
    if (survivalPlayer == null) {
      executor.sendMessage(Messages.PLAYER_NOT_FOUND);
    }
    return survivalPlayer;
  }
  //</editor-fold>

  private static SurvivalPlayer determinePlayer(final String playerName) {
    if (SurvivalData.getInstance().getPlayerCache().containsValue(playerName)) {
      final UUID uuid = UUIDUtils.getUUID(playerName);
      if (SurvivalData.getInstance().getPlayers().containsKey(uuid)) {
        return SurvivalData.getInstance().getPlayers().get(uuid);
      }
    }
    return null;
  }

  public Player getPlayer() {
    return UUIDUtils.getPlayer(this.uuid);
  }

  public void addComplaint(final Complaint complaint) {
    complaints.add(complaint);
    outputComplaint(complaint);
  }

  public void outputComplaint(final Complaint complaint) {
    getPlayer().sendMessage(Messages.PREFIX + "§c Der Spieler: §e" + SurvivalData.getInstance().getPlayers()
        .get(complaint.getExecutor()).getPlayer().getDisplayName() + "§c hat sich am §e" +
        complaint.outputDate() + "§c über dich beschwert: §e" + complaint.getReason());
  }

  public void sendHotbarMessage(final String message) {
    //sendHotbarMessage(getPlayer(), message);
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

  public List<SurvivalLicense> getLicences() {
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

  public short getVotes() {
    return votes;
  }

  public void setVotes(final short votes) {
    this.votes = votes;
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

  public boolean hasLicence(final SurvivalLicense licence) {
    return licences.contains(licence);
  }
  
  public void setLicence(final List<SurvivalLicense> licences) {
    this.licences = licences;
  }
  
  //</editor-fold>
}
