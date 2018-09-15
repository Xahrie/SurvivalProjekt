package de.PAS123.Survival.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class AsyncMySQL {

	private ExecutorService executor;
	private Plugin plugin;
	private MySQL sql;

	public AsyncMySQL(Plugin owner, String host, int port, String user, String password, String database) {
		try {
			sql = new MySQL(host, port, user, password, database);
			executor = Executors.newCachedThreadPool();
			plugin = owner;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void update(PreparedStatement statement) {
		executor.execute(() -> sql.queryUpdate(statement));
	}
	
	public void update(String statement) {
		executor.execute(() -> sql.queryUpdate(statement));
	}
	
	public void query(PreparedStatement statement, Consumer<ResultSet> consumer) {
		executor.execute(() -> {
			ResultSet result = sql.query(statement);
			Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> consumer.accept(result));
		});
	}
	public void query(String statement, Consumer<ResultSet> consumer) {
		executor.execute(() -> {
			ResultSet result = sql.query(statement);
			Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> consumer.accept(result));
		});
	}
	public void addVote(UUID uuid, String website) {
		update("INSERT INTO `" + getMySQL().database + "`.`Votes` (`UUID`, `Time`, `Website`) VALUES ('" + uuid + "', '" + System.currentTimeMillis() + "', '" + website + "');");
//		update("INSERT `" + getMySQL().database + "`.`Votes` SET `Votes` = '" + i+1 + "' where UUID = '" + uuid + "';");
	}
	
	public void setMoney(UUID uuid, float f) {
		update("INSERT INTO `" + getMySQL().database + "`.`Coins` (`UUID`, `Money`) VALUES ('" + uuid + "', '" + f + "'();");
	}
	
	public PreparedStatement prepare(String query) {
		try {
			return sql.getConnection().prepareStatement(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean exist(UUID uuid, String table) {
		try {
			ResultSet rs = sql.query("SELECT * FROM `" + getMySQL().database +  "`.`" + table + "` where UUID= '" + uuid.toString() + "';");
			if(rs.next()) {
				return rs.getString("UUID") != null;
			}
			return false;
		} catch (SQLException e) {}
		return false;
	}
	
	public MySQL getMySQL() {
		return sql;
	}
	
	public static class MySQL {
		
		public String host, user, password, database;
		private int port;
		
		private Connection conn;
		
		public MySQL(String host, int port, String user, String password, String database) throws Exception {
			this.host = host;
			this.port = port;
			this.user = user;
			this.password = password;
			this.database = database;
			
			this.openConnection();
		}
		
		public String getName(UUID uuid) {
			String name = null;
			try {
				ResultSet rs = query("SELECT `name` FROM `Playerstatus` where UUID = '" + uuid + "';");
				while(rs.next()) {
					name = rs.getString("name");
				}
			} catch (SQLException e) {}
			return name;
		}
		
		public void queryUpdate(String query) {
			checkConnection();
			try (PreparedStatement statement = conn.prepareStatement(query)) {
				queryUpdate(statement);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		public void createTables() {
			queryUpdate("CREATE TABLE IF NOT EXISTS `" + database + "`.`Votes` (  `UUID` VARCHAR(40) NOT NULL,  `Time` VARCHAR(10) NOT NULL,  `Website` VARCHAR(40) NOT NULL);");
		}
		public void set(String update, String table, String name, String updatevalue, String searchquery) {
			queryUpdate("UPDATE `" + database + "`.`" + table + "` SET `" + name + "` = '" + updatevalue + "' where `"  + searchquery + "` = '" + update + "';");
		}
		public void delete(String update, String table, String updatevalue) {
			queryUpdate("DELETE FROM `" + database + "`.`" + table + "` ` WHERE `" + updatevalue + "` = '" + update + "'");
		}
		public void deletePlayer(UUID uuid) {
			queryUpdate("DELETE FROM `" + database + "`.`Votes` WHERE `UUID` = '" + uuid + "'");
		}
		public Integer getVotes(UUID uuid) {
			int i = -1;
			try {
				ResultSet rs = query("SELECT Votes FROM `Votes` where UUID = '" + uuid + "';");
				while(rs.next()) {
					i = rs.getInt("Votes");
				}
			} catch (SQLException e) {}
			return i;
		}
		public Integer getCoins(UUID uuid) {
			int i = 0;
			try {
				ResultSet rs = query("SELECT Coins FROM `LobbySystem` where UUID = '" + uuid + "';");
				while(rs.next()) {
					i = rs.getInt("Coins");
				}
			} catch (SQLException e) {}
			return i;
		}
		public Object get(String name, String table, UUID search) {
			Object obj = null;
			try {
				ResultSet rs = query("SELECT " + name + " FROM `" + table + "` where UUID = '" + search + "';");
				while(rs.next()) {
					obj = rs.getObject(name);
				}
			} catch (SQLException e) {
				e.printStackTrace();
				return null;
			}
			return obj;
		}
		public boolean exist(UUID uuid, String table) {
			try {
				ResultSet rs = query("SELECT * FROM `" + database +  "`.`" + table + "` where UUID= '" + uuid.toString() + "';");
				if(rs.next()) {
					return rs.getString("UUID") != null;
				}
				return false;
			} catch (SQLException e) {}
			return false;
		}
		public int getRank(UUID UUID) {
			int rank = -1;
			try {
				PreparedStatement ps = (PreparedStatement) conn.prepareStatement("SELECT * FROM `" + database + "`.`Skywars` ORDER BY Wins DESC");
				ResultSet result = ps.executeQuery();
				while (result.next()) {
					String uuid2 = result.getString("UUID");
					if (uuid2.equalsIgnoreCase(UUID.toString())) {
						rank = result.getRow();
						break;
					}
				}
		    } catch (Exception ex) {
		    	ex.printStackTrace();
		    }
		    return rank;
		}
		public void queryUpdate(PreparedStatement statement) {
			checkConnection();
			try {
				statement.executeUpdate();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					statement.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		public ResultSet query(String query) {
			checkConnection();
			try {
				return query(conn.prepareStatement(query));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		public ResultSet query(PreparedStatement statement) {
			checkConnection();
			try {
				return statement.executeQuery();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		public Connection getConnection() {
			return this.conn;
		}
		
		private void checkConnection() {
			try {
				if (this.conn == null || !this.conn.isValid(10) || this.conn.isClosed()) openConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		public Connection openConnection() throws Exception {
			Class.forName("com.mysql.jdbc.Driver");
			return this.conn = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, this.user, this.password);
		}
		
		public void closeConnection() {
			try {
				this.conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				this.conn = null;
			}
		}
	}
	
}
