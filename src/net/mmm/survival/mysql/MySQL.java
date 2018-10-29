package net.mmm.survival.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Executors;

/**
 * MySQL-Infos
 */
public final class MySQL {
  private static final int PORT = 3306;
  private static final String DATABASE = "mcmysql_1_nick";
  private static final String HOST = "sql430.your-server.de";
  private static final String PASSWORD = "gxI9C2t8i2z6Sf7a";
  private static final String USER = "mcmysql_nick";
  private Connection connection;

  /**
   * Konstruktor
   *
   * @throws ClassNotFoundException Driver wurde nicht gefunden
   * @throws SQLException SQL-Ausnahme
   */
  MySQL() throws ClassNotFoundException, SQLException {
    this.openConnection();
  }

  //<editor-fold desc="getter and setter">
  Connection getConnection() {
    return connection;
  }
  //</editor-fold>

  /**
   * Erstellung einer Connection
   *
   * @throws ClassNotFoundException Driver wurde nicht gefunden
   * @throws SQLException MySQLAusnahme
   */
  private void openConnection() throws ClassNotFoundException, SQLException {
    Class.forName("com.mysql.jdbc.Driver");
    this.connection = DriverManager.getConnection("jdbc:mysql://" + HOST + ":" + PORT +
        "/" + DATABASE, USER, PASSWORD);
  }

  /**
   * Erstellung einer Tabelle
   */
  public void createTables() {
    queryUpdate("CREATE TABLE IF NOT EXISTS Votes (UUID varchar(40) NOT NULL, Time varchar(10)" +
        " NOT NULL, Website varchar(40) NOT NULL);");
    queryUpdate("CREATE TABLE IF NOT EXISTS SurvivalPlayer (UUID varchar(40) NOT NULL, MONEY " +
        "double, LICENCES varchar(10000), VOTES int(11), MAXZONE int(11), HOME varchar(64), " +
        "LEVELPLAYER varchar(1024));");
    queryUpdate("CREATE TABLE IF NOT EXISTS Playerstatus (id int PRIMARY KEY AUTO_INCREMENT, " +
        "UUID VARCHAR(45), name VARCHAR(20), online int(1), lastonline timestamp, firstjoin " +
        "timestamp, ip VARCHAR(20));");
  }

  /**
   * Erstellung einer Query eines Strings
   *
   * @param query Query als String
   */
  private void queryUpdate(final String query) {
    openConnectionIfClosed();
    try (final PreparedStatement statement = connection.prepareStatement(query)) {
      queryUpdate(statement);
    } catch (final SQLException ex) {
      ex.printStackTrace();
    }
  }

  void openConnectionIfClosed() {
    try {
      if (this.connection == null || !this.connection.isValid(10) || this.connection.isClosed()) {
        openConnection();
      }
    } catch (final SQLException | ClassNotFoundException ex) {
      ex.printStackTrace();
    }
  }

  /**
   * Update einer Query eines PreparedStatements
   *
   * @param statement PreparedStatement
   * @see PreparedStatement
   */
  private void queryUpdate(final PreparedStatement statement) {
    openConnectionIfClosed();

    try (final PreparedStatement preparedStatement = statement) {
      preparedStatement.executeUpdate();
    } catch (final SQLException ex) {
      ex.printStackTrace();
    }
  }

  /**
   * Erstellung einer Query eines Strings
   *
   * @param query Query als String
   * @return Stringvalue
   */
  String query(final String query) {
    openConnectionIfClosed();

    try (final PreparedStatement statement = connection.prepareStatement(query);
         final ResultSet resultSet = statement.executeQuery()) {
      if (resultSet.next()) {
        return resultSet.getString(1);
      }
    } catch (final SQLException ex) {
      ex.printStackTrace();
    }
    return null;
  }

  /**
   * Schliessen einer Connection
   * <p>
   * !!! WICHTIG
   */
  public void closeConnection() {
    if (connection != null) {
      try {
        connection.close();
      } catch (final SQLException e) {
        e.printStackTrace();
      }
    }
  }

  void update(final String statement) {
    Executors.newCachedThreadPool().execute(() -> queryUpdate(statement));
  }
}
