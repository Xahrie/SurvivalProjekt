package net.mmm.survival.commands;

import net.mmm.survival.player.SurvivalPlayer;
import net.mmm.survival.util.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /home Command
 */
public class Home extends Teleport implements CommandExecutor {
  @Override
  public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
    if (sender instanceof Player) {
      final Player p = (Player) sender;
      final SurvivalPlayer survivalPlayer = SurvivalPlayer.findSurvivalPlayer(p);

      if (checkConfig(survivalPlayer) && checkTeleport(survivalPlayer)) {
        survivalPlayer.setTeleport(true);
        super.teleport(p, survivalPlayer.getHome());
      }
    }
    return false;
  }

  /**
   * Check, ob bereits ein Home existiert
   *
   * @param survivalPlayer Spieler als SurvivalPlayer
   * @return booleanischer Wert
   */
  private boolean checkConfig(final SurvivalPlayer survivalPlayer) {
    if (survivalPlayer.getHome() != null) {
      return true;
    }
    survivalPlayer.getPlayer().sendMessage(Messages.PREFIX + " §7Du hast noch keinen Home-Punkt gesetzt.");
    return false;

  }

  /**
   * Check, ob bereits teleportiert wird
   *
   * @param survivalPlayer Spieler als SurvivalPlayer
   * @return booleanischer Wert
   */
  private boolean checkTeleport(final SurvivalPlayer survivalPlayer) {
    if (!survivalPlayer.isTeleport()) {
      return true;
    }
    survivalPlayer.getPlayer().sendMessage(Messages.PREFIX + " §7Du wirst bereits teleportiert.");
    return false;
  }

}
