package net.mmm.survival.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.UUID;

import net.mmm.survival.SurvivalData;
import net.mmm.survival.player.Complaint;
import net.mmm.survival.player.LevelPlayer;
import net.mmm.survival.player.SurvivalLicence;
import net.mmm.survival.player.SurvivalPlayer;
import net.mmm.survival.regions.SurvivalWorld;
import net.mmm.survival.util.ObjectBuilder;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * Verwaltung der MySQL-Datenbank-Verbindung
 */
public class AsyncMySQL {
  private MySQL sql;

  /**
   * Konstruktor
   */
  public AsyncMySQL() {
    try {
      sql = new MySQL();
    } catch (final ClassNotFoundException | SQLException ex) {
      ex.printStackTrace();
    }
  }

  //<editor-fold desc="getter and setter">
  public MySQL getMySQL() {
    return sql;
  }
  //</editor-fold>

  /**
   * Spielername aus einer UUID
   *
   * @param uuid einzigartiger Identifier
   * @return Name des Spielers
   */
  public String getName(final UUID uuid) {
    return getMySQL().query("SELECT name FROM Playerstatus where UUID='" + uuid + "'");
  }

  /**
   * Vote hinzufuegen
   *
   * @param uuid einzigartiger Identifier
   * @param website Webseite
   */
  public void addVote(final UUID uuid, final String website) {
    sql.update("INSERT INTO Votes (UUID, Time, Website) VALUES (" + uuid + ", " +
        System.currentTimeMillis() + ", " + website + ")");
  }

  /**
   * Playerliste abfragen
   *
   * @return Playerliste
   */
  public Map<UUID, SurvivalPlayer> getPlayers() {
    final Map<UUID, SurvivalPlayer> survivalPlayers = new HashMap<>();

    final Connection connection = getMySQL().getConnection();
    try (final Statement statement = connection.createStatement();
         final ResultSet resultSet = statement
             .executeQuery("SELECT UUID, MONEY, LICENCES, VOTES, MAXZONE, HOME, LEVELPLAYER FROM SurvivalPlayer")) {
      while (resultSet.next()) {
        final UUID uuid = UUID.fromString(resultSet.getString(1));
        survivalPlayers.put(uuid, determinePlayer(resultSet, uuid));
      }
    } catch (final SQLException ex) {
      ex.printStackTrace();
    }
    return survivalPlayers;
  }

  private SurvivalPlayer determinePlayer(final ResultSet resultSet, final UUID uuid) throws SQLException {
    final double money = resultSet.getDouble(2);
    final List<Complaint> complaints = determineComplaints(uuid);
    final List<SurvivalLicence> licences = determineLicences(resultSet.getString(3));
    final short votes = (short) resultSet.getInt(4);
    final int maxzone = resultSet.getInt(5);
    final Location location = determineLocation(resultSet.getString(6));

    LevelPlayer levelPlayer = new LevelPlayer();
    if (resultSet.getString(7) != null) {
      levelPlayer = (LevelPlayer) ObjectBuilder.getObjectOf(resultSet.getString(7));
    }

    return new SurvivalPlayer(uuid, money, complaints, licences, votes, maxzone, location, levelPlayer);
  }

  private List<Complaint> determineComplaints(final UUID uuid) {
    final ArrayList<Complaint> complaints = new ArrayList<>();

    try (final Statement statement = getMySQL().getConnection().createStatement();
         final ResultSet resultSet = statement
             .executeQuery("SELECT UUID, ID, REASON, OPERATOR, DATE FROM `SurvivalPlayerComplaints`")) {
      while (resultSet.next()) {
        final UUID uuidComplaint = UUID.fromString(resultSet.getString(1));
        final int id = resultSet.getInt(2);
        final String reason = resultSet.getString(3);
        final UUID operator = UUID.fromString(resultSet.getString(4));
        final java.util.Date date = resultSet.getTimestamp(5);
        if (uuidComplaint.equals(uuid)) {
          complaints.add(new Complaint(uuid, id, reason, operator, date));
        }
      }
    } catch (final SQLException ex) {
      ex.printStackTrace();
    }

    return complaints;
  }

  private List<SurvivalLicence> determineLicences(final String licencesString) {
    final List<SurvivalLicence> licences = new ArrayList<>();
    if (licencesString != null && !licencesString.isEmpty()) {
      for (final String licence : licencesString.split(",")) {
        licences.add(SurvivalLicence.valueOf(licence));
      }
    }

    return licences;
  }

  private Location determineLocation(final String homeString) {
    final World bauWorld = SurvivalWorld.BAUWELT.get();
    final double x = Double.parseDouble(homeString.split("/")[0]);
    final double y = Double.parseDouble(homeString.split("/")[1]);
    final double z = Double.parseDouble(homeString.split("/")[2]);
    return new Location(bauWorld, x, y, z);
  }

  /**
   * Liste mit allen Spieler auf diesem Server abfragen
   *
   * @return Playerliste
   */
  public Map<UUID, String> getPlayerCache() {
    final Map<UUID, String> cache = new HashMap<>();
    final Connection connection = getMySQL().getConnection();

    try (final Statement statement = connection.createStatement();
         final ResultSet resultSet = statement.executeQuery("SELECT UUID, name FROM Playerstatus")) {
      while (resultSet.next()) {
        final UUID uuid = UUID.fromString(resultSet.getString(1));
        final String name = resultSet.getString(2);
        cache.put(uuid, name);
      }
    } catch (final SQLException ex) {
      ex.printStackTrace();
    }

    return cache;
  }

  /**
   * Setzt einen Spieler nach dem Join in den Speicher (Playerstatus) bzw.
   * aendert dessen Namen nach Nickaenderung
   *
   * @param target gejointer Spieler
   */
  public void updatePlayer(final Player target) {
    final Map<UUID, SurvivalPlayer> players = SurvivalData.getInstance().getPlayers();

    if (players.containsKey(target.getUniqueId())) { //schon mal angemeldet
      final String qry = "UPDATE Playerstatus SET name=?,online=? WHERE UUID=?";
      try (final PreparedStatement statement = sql.getConnection().prepareStatement(qry)) {
        statement.setString(1, target.getName());
        statement.setInt(2, 1);
        statement.setString(3, target.getUniqueId().toString());

        statement.executeUpdate();
      } catch (final SQLException ex) {
        ex.printStackTrace();
      }
    } else { //neu
      final String qry = "INSERT INTO Playerstatus (name, UUID, online) VALUES (?, ?, ?)";
      try (final PreparedStatement statement = sql.getConnection().prepareStatement(qry)) {
        statement.setString(1, target.getName());
        statement.setString(2, target.getUniqueId().toString());
        statement.setInt(3, 1);
        statement.executeUpdate();
      } catch (final SQLException ex) {
        ex.printStackTrace();
      }
    }
  }

  /**
   * Speicherung von Spielern
   */
  public void storePlayers() {
    getMySQL().openConnectionIfClosed();
    final Map<UUID, SurvivalPlayer> survivalPlayers = SurvivalData.getInstance().getPlayers();
    final Collection<SurvivalPlayer> players = survivalPlayers.values();
    try (final PreparedStatement statement = sql.getConnection()
        .prepareStatement("UPDATE SurvivalPlayer SET MONEY=?, LICENCES=?, VOTES=?, MAXZONE=?, HOME=?, LEVELPLAYER=? WHERE UUID=?")) {

      for (final SurvivalPlayer survivalPlayer : players) {
        final StringJoiner licences = determineLicences(survivalPlayer);

        final Location playerHome = survivalPlayer.getHome();
        final String home = playerHome.getX() + "/" + playerHome.getY() + "/" + playerHome.getZ();

        updateAndExecuteStatement(statement, survivalPlayer, licences.toString(), home);
      }
    } catch (final SQLException ex) {
      ex.printStackTrace();
    }
    storeComplaints();
  }

  private StringJoiner determineLicences(final SurvivalPlayer survivalPlayer) {
    final StringJoiner licences = new StringJoiner(",");
    final List<SurvivalLicence> survivalLicences = survivalPlayer.getLicences();
    for (final SurvivalLicence licence : survivalLicences) {
      licences.add(licence.name());
    }

    return licences;
  }

  private void updateAndExecuteStatement(final PreparedStatement statement, final SurvivalPlayer survivalPlayer, final String licences,
                                         final String home) throws SQLException {
    statement.setDouble(1, survivalPlayer.getMoney());
    statement.setString(2, licences);
    statement.setInt(3, survivalPlayer.getVotes());
    statement.setInt(4, survivalPlayer.getMaxzone());
    statement.setString(5, home);
    statement.setString(6, ObjectBuilder.getStringOf(survivalPlayer.getLevelPlayer()));
    statement.setString(7, survivalPlayer.getUuid().toString());
    statement.executeUpdate();
  }

  private void storeComplaints() {
    final Map<UUID, SurvivalPlayer> survivalPlayers = SurvivalData.getInstance().getPlayers();
    final Collection<SurvivalPlayer> players = survivalPlayers.values();

    try (final PreparedStatement statement = sql.getConnection().prepareStatement("DELETE FROM SurvivalPlayerComplaints")) {
      statement.executeUpdate();
    } catch (final SQLException ex) {
      ex.printStackTrace();
    }

    try (final PreparedStatement statement = sql.getConnection()
        .prepareStatement("INSERT INTO SurvivalPlayerComplaints (UUID, ID, REASON, OPERATOR, DATE) VALUES ( ?, ?, ?, ?, ?)")) {
      for (final SurvivalPlayer player : players) {
        for (final Complaint complaint : player.getComplaints()) {
          statement.setString(1, complaint.getUuid().toString());
          statement.setInt(2, complaint.getId());
          statement.setString(3, complaint.getReason());
          statement.setString(4, complaint.getExecutor().toString());
          final java.util.Date utilDate = complaint.getDate();
          final java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
          final DateFormat df = new SimpleDateFormat("YYYY-MM-dd hh:mm:ss");
          statement.setString(5, df.format(sqlDate));
          statement.executeUpdate();
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
    try (final PreparedStatement statement = sql.getConnection()
        .prepareStatement("INSERT INTO SurvivalPlayer (UUID, MONEY, VOTES, MAXZONE, LEVELPLAYER) VALUES (?, ?, ?, ?, ?)")) {
      statement.setString(1, survivalPlayer.getUuid().toString());
      statement.setDouble(2, survivalPlayer.getMoney());
      statement.setInt(3, survivalPlayer.getVotes());
      statement.setInt(4, survivalPlayer.getMaxzone());
      statement.setString(5, ObjectBuilder.getStringOf(survivalPlayer.getLevelPlayer()));
      statement.executeUpdate();
    } catch (final SQLException ex) {
      ex.printStackTrace();
    }
  }

}