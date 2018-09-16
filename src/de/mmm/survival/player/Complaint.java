package de.mmm.survival.player;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Complaint speichert ueber eine Beschwerde Grund der Beschwerde, Operator und Datum
 */
public class Complaint {
  private String reason, operator, date;

  /**
   * Konstruktor
   *
   * @param reason   Beschwerdegrund
   * @param operator Beschwerender
   * @param date     Datum (bereits formatiert)
   */
  public Complaint(final String reason, final String operator, final String date) {
    this.reason = reason;
    this.operator = operator;
    this.date = date;
  }

  /**
   * Berechnung des Datums (Ausgabe)
   *
   * @return Datum als Zeichenkette
   */
  private String calcDate() {
    return new SimpleDateFormat("dd. MMMM yyyy hh:mm").format(new Date());
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

  public String getOperator() {
    return operator;
  }

  public void setOperator(String operator) {
    this.operator = operator;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }
  //</editor-fold>
}
