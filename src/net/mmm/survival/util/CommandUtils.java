package net.mmm.survival.util;

import de.PAS123.Group.Group.Group;
import de.PAS123.Group.Main.Spigot.BungeeGroupManager;
import net.mmm.survival.player.SurvivalPlayer;
import net.mmm.survival.regions.SurvivalWorld;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * wesentliche Methoden fuer wiederkehrende Algorithmen in Command-Methoden
 */
public final class CommandUtils {

  /**
   * Ueberpruefe ob der Executor ein Spieler ist
   *
   * @param commandSender Executor
   * @return boolean
   */
  public static boolean checkPlayer(final CommandSender commandSender) {
    if (!(commandSender instanceof Player) ||
        SurvivalPlayer.findSurvivalPlayer(((Player) commandSender)) == null) {
      commandSender.sendMessage(Messages.NOT_A_PLAYER);
      return false;
    }

    return true;
  }

  /**
   * besitzt der Spieler die noetigen Gruppen oder ist Op
   *
   * @param player Player
   * @return boolean
   */
  public static boolean isOperator(final Player player) {
    final BungeeGroupManager groupManager = BungeeGroupManager.getGroupManager();
    final Group group = groupManager.getGroup(player);
    if (!player.isOp() && !group.equals(Group.OWNER) && !group.equals(Group.MANAGER) && !group.equals(Group.ADMIN)) {
      player.sendMessage(Messages.NOT_ENOUGH_PERMISSIONS);
      return false;
    }

    return true;
  }

  /**
   * Check, ob bereits teleportiert wird
   *
   * @param teleported Spieler als SurvivalPlayer
   * @return booleanischer Wert
   */
  public static boolean checkTeleport(final SurvivalPlayer teleported) {
    if (teleported.isTeleport()) {
      final Player teleportedPlayer = teleported.getPlayer();
      teleportedPlayer.sendMessage(Messages.ALREADY_TELEPORTED);
      return false;
    }

    return true;
  }

  /**
   * Pruefe ob sich der Spieler auch in der richtigen Welt befindet
   *
   * @param player Spieler
   * @return boolean
   */
  public static boolean checkWorld(final Player player) {
    final World playerWorld = player.getWorld();
    if (!playerWorld.equals(SurvivalWorld.BAUWELT.get())) {
      player.sendMessage(Messages.NO_VALID_WORLD);
      return false;
    }

    return true;
  }

  /**
   * Ueberpruefe, ob bei der Eingabe eine Nummer vorliegt
   *
   * @param input Eingabe
   * @param executor Ausfuehrender Spieler
   * @return !=0 : Eingabe ist eine ganze Zahl (Integer) ; ==0 : NumberFormatException (keine ganze
   * Zahl)
   * @see java.lang.NumberFormatException
   */
  public static int stringToNumber(final String input, final Player executor) {
    try {
      return Integer.parseInt(input);
    } catch (final NumberFormatException ignored) {
      executor.sendMessage(Messages.NOT_A_NUMBER);
    }

    return 0;
  }
}
