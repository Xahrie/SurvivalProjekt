package de.mmm.survival.player;

import de.mmm.survival.Survival;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * SurvivalPlayer speichert ueber die uuid den Geldbetrag
 *
 * @author BlueIronGirl, Abgie
 */
public class SurvivalPlayer {
  //TODO (Abgie): Warum Liste aus Spielern in SurvivalPlayer fuer move?
  public static final List<Player> move = new ArrayList<>();

  private boolean zonenedit, zonensearch, tamed;
  private int maxzone;

  //<editor-fold desc="mysql parameter">
  private UUID uuid;
  private short votes;
  private int money;
  private List<Complaint> complaints;
  private List<Licence> licences;
  //</editor-fold>

  /**
   * Konstruktor
   *
   * @param uuid       Eindeutige Id
   * @param money      Geld
   * @param complaints Beschwerden
   * @param licences   Lizenzen
   * @param votes      Anzahl Votes
   */
  public SurvivalPlayer(final UUID uuid, final int money, final List<Complaint> complaints, final List<Licence>
          licences, final short votes) {
    this.uuid = uuid;
    this.money = money;
    this.complaints = complaints;
    this.licences = licences;
    this.votes = votes;

    this.zonenedit = false;
    this.zonensearch = false;
    this.tamed = false;
    this.maxzone = 100;
  }

  /**
   * Ermittle den SurvivalPlayer aus dem SpielerCache
   *
   * @param player Spieler
   * @return SurvivalPlayer von <code>player</code>. Wenn nicht vorhanden, dann <code>return null</code>
   */
  public static SurvivalPlayer findSurvivalPlayer(final Player player) {
    final UUID uuid = player.getUniqueId();

    return Survival.getInstance().players.get(uuid);
  }

  //<editor-fold desc="getter and setter">
  public List<Complaint> getComplaints() {
    return complaints;
  }

  public void setComplaints(final List<Complaint> complaints) {
    this.complaints = complaints;
  }

  public List<Licence> getLicences() {
    return licences;
  }

  public void setLicences(final List<Licence> licences) {
    this.licences = licences;
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

  public List<Player> getMove() {
    return move;
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
  //</editor-fold>

}
