package net.mmm.survival.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.mmm.survival.SurvivalData;
import net.mmm.survival.player.Complaint;
import net.mmm.survival.player.Licence;
import net.mmm.survival.player.SurvivalPlayer;
import org.apache.commons.lang3.EnumUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 * Verwaltung der MySQL-Datenbank-Verbindung
 */
public class AsyncMySQL {

  //Verbindungsdaten
  private static final String HOST = "sql430.your-server.de";
  private static final int PORT = 3306;
  private static final String USER = "mcmysql_nick";
  private static final String PASSWORD = "gxI9C2t8i2z6Sf7a";
  private static final String DATABASE = "mcmysql_1_nick";

  private ExecutorService executor;
  private MySQL sql;

  /**
   * Konstruktor
   */
  public AsyncMySQL() {
    try {
      sql = new MySQL(HOST, PORT, USER, PASSWORD, DATABASE);
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
   * @param uuid einzigartiger Identifier
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

    try (final Statement statement = getMySQL().conn.createStatement()) {
      final ResultSet resultSet = statement
          .executeQuery("SELECT uuid, money, complaints, licences, votes, maxzone, home FROM SurvivalPlayer");

      while (resultSet.next()) {
        final String uuidString = resultSet.getString(1);
        final int money = resultSet.getInt(2);
        final String complaintsString = resultSet.getString(3);
        final String licencesString = resultSet.getString(4);
        final short votes = (short) resultSet.getInt(5);
        final int maxzone = resultSet.getInt(6);
        final String homeString = resultSet.getString(7);

        final UUID uuid = UUID.fromString(uuidString);

        final List<String> complaintsList = Arrays.asList(complaintsString.split(","));
        final List<Complaint> complaints = new ArrayList<>();
        complaintsList.stream().filter(complaint -> complaint.contains("/"))
            .forEach(complaint -> complaints.add(new Complaint(complaint.split("/")[0], complaint.split("/")[1],
                complaint.split("/")[2])));

        final List<String> licencesList = Arrays.asList(licencesString.split(","));
        final List<Licence> licences = new ArrayList<>();
        licencesList.stream().filter(licence -> EnumUtils.isValidEnum(Licence.class, licence))
            .forEach(licence -> licences.add(Licence.valueOf(licence)));

        Location location = null;
        if (!homeString.equals("")) {
          final double x = Double.parseDouble(homeString.split("/")[0]);
          final double y = Double.parseDouble(homeString.split("/")[1]);
          final double z = Double.parseDouble(homeString.split("/")[2]);
          location = new Location(Bukkit.getWorld("world"), x, y, z);
        }

        final SurvivalPlayer survivalPlayer = new SurvivalPlayer(uuid, money, complaints, licences, votes, maxzone,
            location);

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
    final Collection<SurvivalPlayer> players = SurvivalData.getInstance().getPlayers().values();

    try (final PreparedStatement statement = sql.conn
        .prepareStatement("UPDATE SurvivalPlayer SET money=?, complaints=?, licences=?, votes=?, maxzone=?, home=? WHERE uuid=?")) {

      for (final SurvivalPlayer survivalPlayer : players) {
        final StringBuilder complaints = new StringBuilder();
        survivalPlayer.getComplaints().forEach(complaints::append);
        if (complaints.length() != 0) {
          complaints.deleteCharAt(complaints.length() - 1);
        }

        final StringBuilder licences = new StringBuilder();
        survivalPlayer.getLicences().forEach(licence -> licences.append(licence.toString()).append(","));
        if (licences.length() != 0) {
          licences.deleteCharAt(licences.length() - 1);
        }

        final String home = survivalPlayer.getHome() != null ? survivalPlayer.getHome().getX() + "/" + survivalPlayer
            .getHome().getY() + "/" + survivalPlayer.getHome().getZ() : "";

        try {
          statement.setInt(1, survivalPlayer.getMoney());
          statement.setString(2, complaints.toString());
          statement.setString(3, licences.toString());
          statement.setInt(4, survivalPlayer.getVotes());
          statement.setInt(5, survivalPlayer.getMaxzone());
          statement.setString(6, home);
          statement.setString(7, survivalPlayer.getUuid().toString());
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
   * Erstellt einen Neuen Spieler in der Datenbank
   *
   * @param survivalPlayer SurvivalPlayer
   */
  public void createPlayer(final SurvivalPlayer survivalPlayer) {
    try (final PreparedStatement statement = sql.conn
        .prepareStatement("INSERT INTO SurvivalPlayer (uuid, money, votes, maxzone) VALUES (?, ?, ?, ?)")) {
      statement.setString(1, survivalPlayer.getUuid().toString());
      statement.setInt(2, survivalPlayer.getMoney());
      statement.setInt(3, survivalPlayer.getVotes());
      statement.setInt(4, survivalPlayer.getMaxzone());
      statement.executeUpdate();
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
     * @param host Host
     * @param port Port
     * @param user Username
     * @param password Passwort
     * @param database Datenbank
     * @throws SQLException SQL-Ausnahme
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
      queryUpdate("CREATE TABLE IF NOT EXISTS `" + database + "`.`SurvivalPlayer` (  `uuid` VARCHAR(40) NOT NULL, " +
          "`money` int(11), `complaints` varchar(10000), `licences` varchar(10000), `votes` int(11), `maxzone` " +
          "int(11), `home` varchar(64))");
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
        return query(conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS));
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
        statement.executeUpdate();
        return statement.getGeneratedKeys();
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
     * @throws SQLException MySQLAusnahme
     */
    void openConnection() throws ClassNotFoundException, SQLException {
      Class.forName("com.mysql.jdbc.Driver");
      this.conn = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database,
          this.user, this.password);
    }

  }

}
