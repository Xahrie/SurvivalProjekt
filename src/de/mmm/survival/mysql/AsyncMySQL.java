package de.mmm.survival.mysql;

import de.mmm.survival.Survival;
import de.mmm.survival.player.Complaint;
import de.mmm.survival.player.Licence;
import de.mmm.survival.player.SurvivalPlayer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Verwaltung der MySQL-Datenbank-Verbindung
 */
public class AsyncMySQL {
  //Datenbankverbindung Votifier
  public static final String VOTIFIER_HOST = "localhost";
  public static final int VOTIFIER_PORT = 3306;
  public static final String VOTIFIER_USER = "root";
  public static final String VOTIFIER_PASSWORT = "test123";
  public static final String VOTIFIER_DATABASE = "Votes";

  //Datenbankverbindung Players
  public static final String PLAYER_HOST = "sql430.your-server.de";
  public static final int PLAYER_PORT = 3306;
  public static final String PLAYER_USER = "mcmysql_nick";
  public static final String PLAYER_PASSWORT = "gxI9C2t8i2z6Sf7a";
  public static final String PLAYER_DATABASE = "mcmysql_1_nick";

  private ExecutorService executor;
  private MySQL sql;


  /**
   * Konstruktor
   *
   * @param host     Host
   * @param port     Port
   * @param user     Nutzername
   * @param password Passwort
   * @param database Datenbank
   */
  public AsyncMySQL(final String host, final int port, final String user, final String password, final String
          database) {
    try {
      sql = new MySQL(host, port, user, password, database);
      executor = Executors.newCachedThreadPool();

    } catch (final SQLException | ClassNotFoundException ex) {
      ex.printStackTrace();
    }
  }

  /**
   * Updatet die SQL mit einem Statement
   *
   * @param statement Query des Statements
   */
  private void update(final String statement) {
    executor.execute(() -> sql.queryUpdate(statement));
  }

  /**
   * Vote hinzufuegen
   *
   * @param uuid    einzigartiger Identifier
   * @param website Webseite
   */
  public void addVote(final UUID uuid, final String website) {
    update("INSERT INTO `" + getMySQL().database + "`.`Votes` (`UUID`, `Time`, `Website`) VALUES ('" + uuid + "', '"
            + System.currentTimeMillis() + "', '" + website + "');");
  }

  /**
   * Playerliste abfragen
   *
   * @return Playerliste
   */
  public Map<UUID, SurvivalPlayer> getPlayers() {
    final Map<UUID, SurvivalPlayer> players = new HashMap<>();

    try {
      final String query = "SELECT (uuid, money, complaints, licences, votes) FROM SurvivalPlayer";

      final ResultSet resultSet = getMySQL().query(query);

      while (resultSet.next()) {
        final String uuidString = resultSet.getString(1);
        final int money = resultSet.getInt(2);
        final String complaintsString = resultSet.getString(3);
        final String licencesString = resultSet.getString(4);
        final int votes = resultSet.getInt(5);

        final UUID uuid = UUID.fromString(uuidString);
        final List<String> complaintsList = Arrays.asList(complaintsString.split(","));
        final List<Complaint> complaints = new ArrayList<>();
        complaintsList.forEach(complaint -> complaints.add(new Complaint(complaint.split("/")[0], complaint.split("/")
                [1], complaint.split("/")[2])));
        final List<String> licencesList = Arrays.asList(licencesString.split(","));
        final List<Licence> licences = new ArrayList<>();
        licencesList.forEach(licence -> licences.add(Licence.valueOf(licence)));

        final SurvivalPlayer survivalPlayer = new SurvivalPlayer(uuid, money, complaints, licences, (short) votes);

        players.put(uuid, survivalPlayer);
      }

    } catch (final SQLException ex) {
      ex.printStackTrace();
    }

    return players;
  }

  /**
   * Speicherung von Spielern
   */
  public void storePlayers() {
    final List<SurvivalPlayer> players = new ArrayList<>();
    Survival.getInstance().players.keySet().forEach(uuid -> players.add(Survival.getInstance().players.get(uuid)));

    getMySQL().query("DELETE FROM SurvivalPlayer");

    try (final PreparedStatement statement = sql.conn.prepareStatement("INSERT INTO SurvivalPlayer (uuid, money, " +
            "complaints, licences, votes) VALUES (?, ?, ?, ?, ?)")) {

      for (final SurvivalPlayer survivalPlayer : players) {
        final StringBuilder complaints = new StringBuilder();
        survivalPlayer.getComplaints().forEach(complaints::append);
        complaints.deleteCharAt(complaints.length()-1);

        final StringBuilder licences = new StringBuilder();
        survivalPlayer.getLicences().forEach(licence -> licences.append(licence.toString()).append(","));
        licences.deleteCharAt(licences.length()-1);

        try {
          statement.setString(1, survivalPlayer.getUuid().toString());
          statement.setInt(2, survivalPlayer.getMoney());
          statement.setString(3, complaints.toString());
          statement.setString(4, licences.toString());
          statement.setInt(5, survivalPlayer.getVotes());
          statement.executeUpdate();
        } catch (final SQLException ex) {
          ex.printStackTrace();
        }
      }

    } catch (final SQLException ex) {
      ex.printStackTrace();
    }

  }

  /**
   * @return Instanz
   */
  public MySQL getMySQL() {
    return sql;
  }

  public static class MySQL {

    private final int port;
    String host, user, password, database;
    private Connection conn;

    /**
     * Konstruktor
     *
     * @param host     Host
     * @param port     Port
     * @param user     Username
     * @param password Passwort
     * @param database Datenbank
     * @throws SQLException           SQL-Ausnahme
     * @throws ClassNotFoundException Driver wurde nicht gefunden
     */
    MySQL(final String host, final int port, final String user, final String password, final String database)
            throws SQLException, ClassNotFoundException {
      this.host = host;
      this.port = port;
      this.user = user;
      this.password = password;
      this.database = database;

      this.openConnection();
    }

    /**
     * Spielername aus einer UUID
     *
     * @param uuid einzigartiger Identifier
     * @return Name des Spielers
     */
    public String getName(final UUID uuid) {
      String name = null;
      try {
        final ResultSet rs = query("SELECT `name` FROM `Playerstatus` where UUID = '" + uuid + "';");
        while (rs.next()) {
          name = rs.getString("name");
        }
      } catch (final SQLException ignored) {
      }
      return name;
    }

    /**
     * Erstellung einer Query eines Strings
     *
     * @param query Query als String
     */
    void queryUpdate(final String query) {
      checkConnection();
      try (final PreparedStatement statement = conn.prepareStatement(query)) {
        queryUpdate(statement);
      } catch (final SQLException ex) {
        ex.printStackTrace();
      }
    }

    /**
     * Erstellung einer Tabelle
     */
    public void createTables() {
      queryUpdate("CREATE TABLE IF NOT EXISTS `" + database + "`.`Votes` (  `UUID` VARCHAR(40) NOT NULL,  `Time` " +
              "VARCHAR(10) NOT NULL,  `Website` VARCHAR(40) NOT NULL);");
    }

    /**
     * Update einer Query eines PreparedStatements
     *
     * @param statement PreparedStatement
     * @see java.sql.PreparedStatement
     */
    void queryUpdate(final PreparedStatement statement) {
      checkConnection();
      try {
        statement.executeUpdate();
      } catch (final SQLException ex) {
        ex.printStackTrace();
      } finally {
        try {
          statement.close();
        } catch (final SQLException ex) {
          ex.printStackTrace();
        }
      }
    }

    /**
     * Erstellung einer Query eines Strings
     *
     * @param query Query als String
     * @return Query
     * @see java.sql.ResultSet
     */
    ResultSet query(final String query) {
      checkConnection();
      try {
        return query(conn.prepareStatement(query));
      } catch (final SQLException ex) {
        ex.printStackTrace();
      }
      return null;
    }

    /**
     * Erstellung einer Query eines PreparedStatements
     *
     * @param statement PreparedStatement
     * @return Query
     * @see java.sql.ResultSet
     * @see java.sql.PreparedStatement
     */
    ResultSet query(final PreparedStatement statement) {
      checkConnection();
      try {
        return statement.executeQuery();
      } catch (final SQLException ex) {
        ex.printStackTrace();
      }
      return null;
    }

    /**
     * Pruefe Verbindung
     */
    private void checkConnection() {
      try {
        if (this.conn == null || !this.conn.isValid(10) || this.conn.isClosed()) {
          openConnection();
        }
      } catch (final SQLException | ClassNotFoundException ex) {
        ex.printStackTrace();
      }
    }

    /**
     * Erstellung einer Connection
     *
     * @throws ClassNotFoundException Driver wurde nicht gefunden
     * @throws SQLException           MySQLAusnahme
     */
    void openConnection() throws ClassNotFoundException, SQLException {
      Class.forName("com.mysql.jdbc.Driver");
      this.conn = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database,
              this.user, this.password);
    }


  }

}
