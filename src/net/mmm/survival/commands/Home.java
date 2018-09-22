package net.mmm.survival.commands;

import net.mmm.survival.player.SurvivalPlayer;
import net.mmm.survival.util.CommandUtils;
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
    if (CommandUtils.checkPlayer(sender)) {
      final Player player = (Player) sender;
      final SurvivalPlayer survivalPlayer = SurvivalPlayer.findSurvivalPlayer(player);

      if (checkHome(survivalPlayer) && CommandUtils.checkTeleport(survivalPlayer)) {
        super.teleport(player, survivalPlayer.getHome());
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
  private boolean checkHome(final SurvivalPlayer survivalPlayer) {
    if (survivalPlayer.getHome() == null) {
      survivalPlayer.getPlayer().sendMessage(Messages.NO_HOME_SET);
      return false;
    }

    return true;
  }

}
