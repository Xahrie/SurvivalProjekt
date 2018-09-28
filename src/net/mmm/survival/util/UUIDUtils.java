package net.mmm.survival.util;

import java.util.Map;
import java.util.UUID;

import net.mmm.survival.SurvivalData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Verbesserter UUIDFetcher - angepasst auf MineMagicMania-Speicher der jemals
 * verbundenen Spieler. Wenn sich ein Spieler auf dem Server einloggt, wird die
 * UUID und der Name des Spielers gespeichert. Auf diesen Speicher wird hierbei
 * zugegriffen.
 *
 * @author Abgie on 27.09.2018 17:55
 * project SurvivalProjekt
 * @version 1.0
 * @since JDK 8
 */
public class UUIDUtils {
  private static final Map<UUID, String> cache = SurvivalData.getInstance().getPlayerCache();

  /**
   * Bestimme die UUID, mit des Namens, mit dem sich der Spieler der gesuchten
   * UUID zuletzt verbunden hat.
   *
   * @param name letzer bekannter Name des Spielers
   * @return Universally Unique Identifier des Spielers
   */
  public static UUID getUUID(final String name) {
    return SurvivalData.getInstance().getPlayerCache().keySet().stream().filter(id -> SurvivalData
        .getInstance().getPlayerCache().get(id).equalsIgnoreCase(name)).findFirst().orElse(null);
  }

  /**
   * Bestimme den Namen, mithilfe der UUID, mit dem sich der Spieler des
   * gesuchten Namen zuletzt verbunden hat.
   *
   * @param uuid Universally Unique Identifier des Spielers
   * @return letzter bekannter Name des Spielers
   */
  public static String getName(final UUID uuid) {
    return cache.get(uuid);
  }

  /**
   * Bestimme den Spieler, mithilfe der UUID, mit dem sich der gesuchte Spieler
   * zuletzt verbunden hat.
   *
   * @param uuid Universally Unique Identifier des Spielers
   * @return org.bukkit.entity.Player des Spielers
   */
  public static Player getPlayer(final UUID uuid) {
    Player player = Bukkit.getPlayer(uuid);
    if (player == null) {
      player = Bukkit.getOfflinePlayer(uuid).getPlayer();
    }
    return player;
  }

  /**
   * Bestimme den Spieler, mithilfe des Namens, mit dem sich der gesuchte Spieler
   * zuletzt verbunden hat.
   *
   * @param name letzer bekannter Name des Spielers
   * @return org.bukkit.entity.Player des Spielers
   */
  public static Player getPlayer(final String name) {
    Player player = Bukkit.getPlayer(getUUID(name));
    if (player == null) {
      player = Bukkit.getOfflinePlayer(getUUID(name)).getPlayer();
    }
    return player;
  }

  /**
   * Bestimme die UUID aus dem String, der eine UUID Darstellen soll
   *
   * @param name UUID als String
   * @return Universally Unique Identifier
   */
  public static UUID uuidFromString(final String name) {
    return UUID.fromString(name);
  }

  /**
   * Ueberfruefe, ob der Spieler mit diesem Namen jemals auif diesem Server war
   *
   * @param name letzer bekannter Name des Spielers
   * @return (ja | nein) - Der Spieler war (bereits | noch nie) auf dem Server.
   */
  public static boolean wasOnline(final String name) {
    return cache.keySet().stream().anyMatch(uuid -> cache.get(uuid).equalsIgnoreCase(name));
  }
}
