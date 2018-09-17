package net.mmm.survival.util;

import de.PAS123.Group.Group.Group;
import net.mmm.survival.player.SurvivalPlayer;
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
    if (!(commandSender instanceof Player)) {
      commandSender.sendMessage(Messages.NOT_A_PLAYER);
      return false;
    }

    return true;
  }

  /**
   * besitzt der Spieler die noetigen Gruppen oder ist Op
   *
   * @param player Player
   * @param group Group
   * @return boolean
   */
  public static boolean isOperator(final Player player, final Group group) {
    if (!player.isOp() && !group.equals(Group.OWNER) && !group.equals(Group.MANAGER) || !group.equals(Group.ADMIN)) {
      player.sendMessage(Messages.NOT_ENOUGH_PERMISSIONS);
      return false;
    }

    return true;
  }

  /**
   * Check, ob bereits teleportiert wird
   *
   * @param survivalPlayer Spieler als SurvivalPlayer
   * @return booleanischer Wert
   */
  public static boolean checkTeleport(final SurvivalPlayer survivalPlayer) {
    if (survivalPlayer.isTeleport()) {
      survivalPlayer.getPlayer().sendMessage(Messages.ALREADY_TELEPORTED);
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
    if (!player.getWorld().getName().equals("world")) {
      player.sendMessage(Messages.NO_VALID_WORLD);
      return false;
    }

    return true;
  }
}
