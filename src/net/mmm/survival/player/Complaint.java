package net.mmm.survival.player;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Complaint speichert ueber eine Beschwerde Grund der Beschwerde, Operator und Datum
 */
public class Complaint {
  private final int id;
  private final Date date;
  private final String reason;
  private final UUID executor;
  private UUID uuid;

  /**
   * Konstruktor
   *
   * @param uuid UUID des Spielers
   * @param id Beschwerde-ID
   * @param reason Beschwerdegrund
   * @param executor Beschwerender
   * @param date Datum (bereits formatiert)
   */
  public Complaint(final UUID uuid, final int id, final String reason, final UUID executor, final Date date) {
    this.uuid = uuid;
    this.id = id;
    this.reason = reason;
    this.executor = executor;
    this.date = date;
  }

  String outputDate() {
    return new SimpleDateFormat("dd. MMMM yyyy hh:mm").format(date);
    //yyy-MM-dd hh:mm:ss = Mysql
  }

  @Override
  public String toString() {
    return executor + "/" + reason + "/" + date + ",";
  }

  //<editor-fold desc="getter and setter">
  public String getReason() {
    return reason;
  }

  public UUID getUuid() {
    return uuid;
  }

  public void setUuid(final UUID uuid) {
    this.uuid = uuid;
  }

  public int getId() {
    return id;
  }

  public UUID getExecutor() {
    return executor;
  }

  public Date getDate() {
    return date;
  }

  //</editor-fold>
}
