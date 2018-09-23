package net.mmm.survival.player;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import net.mmm.survival.SurvivalData;
import net.mmm.survival.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * SurvivalPlayer speichert ueber die uuid den Geldbetrag, Beschwerden, Lizenzen, Votes, die
 * Maxzone und den Homepunkt
 *
 * @author BlueIronGirl, Abgie
 */
public class SurvivalPlayer {
  private final List<Complaint> complaints;
  private final Date lastComplaint;
  private final List<Licence> licences;
  private boolean zonenedit, zonensearch, tamed, teleport;
  //<editor-fold desc="mysql parameter">
  private UUID uuid;
  private short votes;
  private int money, maxzone;
  private Location home;
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
  public SurvivalPlayer(final UUID uuid, final int money, final List<Complaint> complaints, final List<Licence> licences, final short votes,
                        final int maxzone, final Location home) {
    this.uuid = uuid;
    this.money = money;
    this.complaints = complaints;
    this.licences = licences;
    this.votes = votes;
    this.maxzone = maxzone;
    this.home = home;

    this.teleport = false;
    this.zonenedit = false;
    this.zonensearch = false;
    this.tamed = false;
    this.lastComplaint = new Date();
  }

  public static Player getPlayer(final Player executor, final String playerName) {
    final Player player = Bukkit.getPlayer(playerName);
    if (player == null) {
      executor.sendMessage(Messages.PLAYER_NOT_FOUND);
    }
    return player;
  }

  /**
   * Ermittle den SurvivalPlayer aus dem SpielerCache
   *
   * @param player Spieler
   * @return SurvivalPlayer von <code>player</code>. Wenn nicht vorhanden, dann <code>return null</code>
   */
  public static SurvivalPlayer findSurvivalPlayer(final Player player) {
    final UUID uuid = player.getUniqueId();

    return SurvivalData.getInstance().getPlayers().get(uuid);
  }

  public Player getPlayer() {
    Player player = Bukkit.getPlayer(uuid);
    if (player == null) {
      player = Bukkit.getOfflinePlayer(uuid).getPlayer();
    }
    return player;
  }

  public void addComplaint(final Complaint complaint) {
    complaints.add(complaint);
    outputComplaint(complaint);
  }

  public void outputComplaint(final Complaint complaint) {
    getPlayer().sendMessage(Messages.PREFIX + "§c┃ Der Spieler: " + SurvivalData.getInstance().getPlayers()
        .get(complaint.getOperator()).getPlayer().getDisplayName() + " hat sich am " +
        complaint.outputDate() + " über dich beschwert: " + complaint.getReason());
  }

  //<editor-fold desc="getter and setter">
  public List<Complaint> getComplaints() {
    return complaints;
  }

  public List<Licence> getLicences() {
    return licences;
  }

  public UUID getUuid() {
    return uuid;
  }

  public void setUuid(final UUID uuid) {
    this.uuid = uuid;
  }

  public int getMoney() {
    return money;
  }

  public void setMoney(final int money) {
    this.money = money;
  }

  public short getVotes() {
    return votes;
  }

  public void setVotes(final short votes) {
    this.votes = votes;
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

  public boolean isTamed() {
    return tamed;
  }

  public void setTamed(final boolean tamed) {
    this.tamed = tamed;
  }

  public int getMaxzone() {
    return maxzone;
  }

  public void setMaxzone(final int maxzone) {
    this.maxzone = maxzone;
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

  //</editor-fold>
}
