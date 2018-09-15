package de.mmm.survival.player;

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
  public static final List<Player> move = new ArrayList<>();
  private UUID uuid;
  private short votes;
  private int money;
  private List<Complaint> complaints;
  private List<Licence> licences;

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

  //</editor-fold>
}
