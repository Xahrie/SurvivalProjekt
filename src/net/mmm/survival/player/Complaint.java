package net.mmm.survival.player;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Complaint speichert ueber eine Beschwerde Grund der Beschwerde, Operator und Datum
 */
public class Complaint {
  private UUID uuid;
  private int id;
  private String reason;
  private UUID operator;
  private Date date;

  /**
   * Konstruktor
   *
   * @param reason Beschwerdegrund
   * @param operator Beschwerender
   * @param date Datum (bereits formatiert)
   */
  public Complaint(final UUID uuid, final int id, final String reason, final UUID operator, final Date date) {
    this.uuid = uuid;
    this.id = id;
    this.reason = reason;
    this.operator = operator;
    this.date = date;
  }

  /**
   * Berechnung des Datums (Ausgabe)
   *
   * @return Datum als Zeichenkette
   */
  public String outputDate() {
    return new SimpleDateFormat("dd. MMMM yyyy hh:mm").format(date);
    //yyy-MM-dd hh:mm:ss = Mysql
  }

  @Override
  public String toString() {
    return operator + "/" + reason + "/" + date + ",";
  }

  //<editor-fold desc="getter and setter">
  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public UUID getUuid() {
    return uuid;
  }

  public void setUuid(UUID uuid) {
    this.uuid = uuid;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public UUID getOperator() {
    return operator;
  }

  public void setOperator(UUID operator) {
    this.operator = operator;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

//</editor-fold>
}
