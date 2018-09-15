package de.pas123.survival.util;

import java.util.UUID;

/**
 * SurvivalPlayer speichert ueber die uuid den Geldbetrag
 */
public class SurvivalPlayer {
  private short id;
  private UUID uuid;
  private int money;

  /**
   * Konstruktor
   */
  public SurvivalPlayer() {

  }

  /**
   * Konstruktor
   *
   * @param uuid Unique-Id des Spielers
   */
  public SurvivalPlayer(UUID uuid) {

  }

  /**
   * Konstruktor
   *
   * @param uuid Unique-Id des Spielers
   * @param money Kontostand des Spielers
   */
  public SurvivalPlayer(UUID uuid, int money) {

  }


  public UUID getUuid() {
    return uuid;
  }

  public void setUuid(UUID uuid) {
    this.uuid = uuid;
  }

  public int getMoney() {
    return money;
  }

  public void setMoney(int money) {
    this.money = money;
  }

  public short getId() {
    return id;
  }

  public void setId(short id) {
    this.id = id;
  }
}
